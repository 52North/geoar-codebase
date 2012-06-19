/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.ar.geoarCodebase.resources;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FilenameUtils;
import org.n52.ar.geoarCodebase.CodebaseApplication;
import org.n52.ar.geoarCodebase.CodebaseDatabase;
import org.n52.ar.geoarCodebase.ds.Datasource;
import org.n52.ar.geoarCodebase.util.CodebaseProperties;
import org.n52.ar.geoarCodebase.util.HtmlHelper;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoResource extends ServerResource {

    private static final String CODEBASE_PATH_CONTEXT_PARAM = "codebase";

    public static final String FORM_INPUT_DESCRIPTION = "descriptionOfResource";

    public static final String FORM_INPUT_FILE = "fileOfResource";

    public static final String FORM_INPUT_IMAGE_LINK = "imageOfResource";

    public static final String FORM_INPUT_NAME = "nameOfResource";

    public static final String FORM_INPUT_PLATFORM = "targetPlatformOfResource";

    private static Logger log = LoggerFactory.getLogger(InfoResource.class);

    private static final String MSG_NO_RESULT = "No results matching your input!";

    private static final String SERVICE_INFO_CONTEXT_PARAM = "serviceInfo";

    private static final String SERVICE_URL_CONTEXT_PARAM = "serviceUrl";

    private static final String URL_APK_PATH = "apk";

    public static final Object FORM_INPUT_IDENTIFIER = "identifierOfResource";

    private String downloadUrlPostfix;

    private String downloadUrlPrefix;

    private String id = null;

    private Collection<Datasource> resources;

    private String serviceInfo;

    private boolean uploadAuthorized = false;

    private boolean deleteAuthorized;

    public InfoResource() {
        log.info("NEW {}", this);

        Series<Parameter> parameters = getApplication().getContext().getParameters();
        this.downloadUrlPrefix = parameters.getFirstValue(SERVICE_URL_CONTEXT_PARAM, true) + "/"
                + CODEBASE_PATH_CONTEXT_PARAM + "/";
        this.downloadUrlPostfix = "/" + URL_APK_PATH;
        this.serviceInfo = parameters.getFirstValue(SERVICE_INFO_CONTEXT_PARAM, true);

        CodebaseProperties.getInstance(getApplication());
    }

    /**
     * Accepts and processes a representation posted to the resource. As response, the content of the uploaded
     * file is sent back the client.
     * 
     * See http://wiki.restlet.org/docs_2.2/13-restlet/28-restlet/64-restlet.html
     */
    @Post
    public Representation acceptPost(Representation entity) throws Exception {
        Representation rep = null;

        if (entity == null) {
            // POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);

            log.error("POST without multipart content.");
        }
        else {

            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
                rep = handleUpload();
            }
            else if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType(), true)) {
                Form f = new Form(entity);
                Parameter action = f.getFirst(CodebaseApplication.FORM_ACTION);
                if (action != null) {
                    String name = action.getName();
                    String value = action.getValue();
                    log.debug("formAction: {} : {}", name, value);
                    
                    if (value.equals(CodebaseApplication.FORM_ACTION_DELETE)) {
                        if ( !this.deleteAuthorized)
                            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        else
                            rep = handleDelete();
                    }
                    else {
                        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "formAction not supported.");
                    }
                }
                else
                    log.warn("Unhandled form action: {}", action);
            }
            else {
                log.warn("Unhandled media type: {}", entity.getMediaType());
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        }

        return rep;
    }

    private Representation handleDelete() {
        boolean delete = CodebaseDatabase.getInstance().deleteResource(this.id);
        Representation rep = null;
        
        if(delete) {
            setStatus(Status.SUCCESS_OK);
            rep = new StringRepresentation("Resource deleted.", MediaType.TEXT_PLAIN);
        }
        else {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            rep = new StringRepresentation("Resource could not be deleted.", MediaType.TEXT_PLAIN);
        }
        
        return rep;
    }

    private Representation handleUpload() throws FileUploadException, Exception {
        Representation rep = null;
        String fileName = null;

        // 1/ Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1000240);

        // 2/ Create a new file upload handler based on the Restlet FileUpload extension that will
        // parse Restlet requests and generates FileItems.
        RestletFileUpload upload = new RestletFileUpload(factory);
        List<FileItem> items;

        // 3/ Request is parsed by the handler which generates a list of FileItems
        items = upload.parseRequest(getRequest());

        // Process the uploaded item and the associated fields, then save file on disk

        // get the form inputs
        String dsName = "";
        String dsDescription = "";
        String dsImageLink = "";
        String dsPlatform = "";
        FileItem dsFileItem = null;
        boolean newFileGiven = false;

        for (FileItem fi : items) {
            if (fi.getFieldName().equals(FORM_INPUT_FILE)) {
                dsFileItem = fi;

                // create id based on filename
                if (this.id == null || this.id.isEmpty())
                    this.id = FilenameUtils.getBaseName(dsFileItem.getName());

                newFileGiven = !dsFileItem.getName().isEmpty(); // empty id == no file in upload form
            }
            else if (fi.getFieldName().equals(FORM_INPUT_DESCRIPTION))
                dsDescription = fi.getString();
            else if (fi.getFieldName().equals(FORM_INPUT_NAME))
                dsName = fi.getString();
            else if (fi.getFieldName().equals(FORM_INPUT_IMAGE_LINK))
                dsImageLink = fi.getString();
            else if (fi.getFieldName().equals(FORM_INPUT_PLATFORM))
                dsPlatform = fi.getString();
        }

        // see if it already exists
        CodebaseDatabase db = CodebaseDatabase.getInstance();
        boolean databaseEntryExists = db.containsResource(this.id);

        // if anything is missing, return error, or use existing database entry
        if (newFileGiven && !databaseEntryExists) {
            rep = new StringRepresentation("Missing file!", MediaType.TEXT_PLAIN);
        }
        else if (dsFileItem != null && newFileGiven
                && !FilenameUtils.getExtension(dsFileItem.getName()).equals(CodebaseProperties.APK_FILE_EXTENSION)) {
            rep = new StringRepresentation("File extension must be .apk!", MediaType.TEXT_PLAIN);
        }
        else {
            // see if all values are given
            if (databaseEntryExists) {
                Datasource ds = db.getResource(this.id).iterator().next();
                if (dsName.isEmpty())
                    dsName = ds.getName();
                if (dsDescription.isEmpty())
                    dsDescription = ds.getDescription();
                if (dsImageLink.isEmpty())
                    dsImageLink = ds.getImageLink();
                if (dsPlatform.isEmpty())
                    dsPlatform = ds.getPlatform();
            }
            else if (dsName.isEmpty() || dsDescription.isEmpty() || dsImageLink.isEmpty() || dsPlatform.isEmpty())
                rep = new StringRepresentation("Not all required information is provided!", MediaType.TEXT_PLAIN);

            // all info given, or taken from db
            log.debug("Read form: {}, {}, {}, {} >>> {}", new Object[] {this.id,
                                                                        dsName,
                                                                        dsDescription,
                                                                        dsImageLink,
                                                                        dsPlatform});

            // handle database entry
            fileName = CodebaseProperties.getInstance().getApkPath(this.id);
            File file = new File(fileName);
            boolean fileExists = file.exists();

            if (databaseEntryExists != fileExists) {
                log.warn("Found either database entry or file, bot not both! database: {} - file: {}",
                         Boolean.valueOf(databaseEntryExists),
                         Boolean.valueOf(fileExists));
            }

            if (fileExists && newFileGiven && dsFileItem != null) {
                log.warn("Deleting apk file " + this.id);
                boolean deleted = file.delete();
                if ( !deleted)
                    log.error("Could not delete file " + fileName);

                // write the file!
                dsFileItem.write(file);
            }

            if (databaseEntryExists) {
                db.updateResource(this.id, dsName, dsDescription, dsImageLink, dsPlatform);
                rep = new StringRepresentation("Updated datasource (" + db.getResource(this.id) + ").",
                                               MediaType.TEXT_PLAIN);
            }
            else {
                db.addResource(this.id, dsName, dsDescription, dsImageLink, dsPlatform);
                rep = new StringRepresentation("Uploaded datasource (" + fileName + ").", MediaType.TEXT_PLAIN);
            }
        }

        return rep;
    }

    @Override
    protected void doInit() throws ResourceException {
        Reference reference = getReference();
        log.debug("Processing {}.", reference.toString());

        Map<String, Object> requestAttributes = getRequestAttributes();

        this.id = (String) requestAttributes.get(CodebaseApplication.PARAM_ID);
        log.debug("Id: {} {}", this.id);

        String token = getQueryValue(CodebaseApplication.PARAM_TOKEN);
        log.debug("Token: {}", token);

        if (token != null && token.equals(CodebaseProperties.getInstance().getUploadToken()))
            this.uploadAuthorized = true;
        log.debug("Upload authorized: {}", Boolean.valueOf(this.uploadAuthorized));

        if (token != null && token.equals(CodebaseProperties.getInstance().getDeleteToken()))
            this.deleteAuthorized = true;
        log.debug("Delete authorized: {}", Boolean.valueOf(this.uploadAuthorized));

        // if there is no id given, then return all! > simply return codebase/index.json file!
        if (this.id == null)
            this.resources = CodebaseDatabase.getInstance().getResources();
        else
            this.resources = CodebaseDatabase.getInstance().getResource(this.id);

        if ( !this.resources.isEmpty()) {
            // add links to the downloads
            for (Datasource ds : this.resources) {
                String downloadLink = generateDownloadLink(ds);
                ds.setDownloadLink(downloadLink);
            }
        }
    }

    private String generateDownloadLink(Datasource ds) {
        return this.downloadUrlPrefix + ds.getId() + this.downloadUrlPostfix;
    }

    @Get("html")
    public Representation toHtml() {
        Reference ref = getReference();
        StringBuilder sb = new StringBuilder();

        HtmlHelper.beforeResult(sb);

        if (this.resources.isEmpty()) {
            sb.append(MSG_NO_RESULT);
        }
        else {
            for (Datasource ds : this.resources) {
                StringBuilder link = new StringBuilder();
                link.append(ref.getHierarchicalPart());
                if ( !ref.getHierarchicalPart().endsWith(ds.getId())) {
                    if ( !ref.getHierarchicalPart().endsWith("/"))
                        link.append("/");
                    link.append(ds.getId());
                }
                if (ref.hasQuery()) {
                    link.append("?");
                    link.append(ref.getQuery());
                }
                HtmlHelper.appendDatasource(sb, ds, link.toString());
            }
        }

        if (this.uploadAuthorized) {
            StringBuilder action = new StringBuilder();
            action.append(ref.getHierarchicalPart());
            // if ( !ref.getHierarchicalPart().endsWith("/"))
            // action.append("/");
            // action.append(CodebaseApplication.ROUTE_UPLOAD);
            if (ref.hasQuery()) {
                action.append("?");
                action.append(ref.getQuery());
            }

            // Am I on a single view page?
            if (this.id != null && this.resources.size() == 1) {
                Datasource r = this.resources.iterator().next();
                log.debug("Only one datasource, filling upload form >>> id (from URL): {}, datasource: {}", this.id, r);
                HtmlHelper.appendUploadForm(sb, action.toString(), r);
            }
            else {
                HtmlHelper.appendUploadForm(sb, action.toString());
            }
        }

        // if is single datasource, offer delete option
        // FIXME this should not be done with POST, but using HTTP DELETE. However, I have no idea how that
        // can be done with a web form, so this would require jQuery - do later!
        if (this.deleteAuthorized && this.id != null && this.resources.size() == 1) {
            StringBuilder action = new StringBuilder();
            action.append(ref.getHierarchicalPart());
            // action.append("/");
            // action.append(this.resources.iterator().next().getId());
            // action.append("/");
            // action.append(CodebaseApplication.ROUTE_DELETE);
            if (ref.hasQuery()) {
                action.append("?");
                action.append(ref.getQuery());
            }

            HtmlHelper.appendDeleteForm(sb, action.toString());
        }

        HtmlHelper.afterResult(sb);

        StringRepresentation representation = new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
        return representation;
    }

    @Get("json")
    public Representation toJson() {
        Map<String, Object> response = new HashMap<String, Object>();

        // http://wiki.fasterxml.com/JacksonInFiveMinutes
        if (this.resources.isEmpty())
            response.put("datasources", MSG_NO_RESULT);
        else
            response.put("datasources", this.resources);
        response.put("service", this.serviceInfo);

        JacksonRepresentation<Map<String, Object>> representation = new JacksonRepresentation<Map<String, Object>>(response);
        return representation;
    }

    @Get("txt")
    public Representation toPlain() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.serviceInfo);
        sb.append("\n");
        sb.append("\n");
        if (this.resources.isEmpty()) {
            sb.append(MSG_NO_RESULT);
        }
        else {
            sb.append("\n");

            for (Datasource ds : this.resources) {
                sb.append(ds.getName());
                sb.append("\n");
                for (int i = 0; i < ds.getName().length(); i++) {
                    sb.append("=");
                }
                sb.append("\nId: ");
                sb.append(ds.getId());
                sb.append("\nDescription: ");
                sb.append(ds.getDescription());
                sb.append("\nDownload: ");
                sb.append(ds.getDownloadLink());

                // TODO instead of having the image as a link, use content negotiation @Get("image/*") and
                // return an image then!
                sb.append("\nImage: ");
                sb.append(ds.getImageLink());

                sb.append("\n");
                sb.append("\n");
            }
        }

        StringRepresentation representation = new StringRepresentation(sb.toString(), MediaType.TEXT_PLAIN);
        return representation;
    }

}
