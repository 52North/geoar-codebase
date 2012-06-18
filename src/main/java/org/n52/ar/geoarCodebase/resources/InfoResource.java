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
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FilenameUtils;
import org.n52.ar.geoarCodebase.CodebaseApplication;
import org.n52.ar.geoarCodebase.CodebaseDatabase;
import org.n52.ar.geoarCodebase.ds.Datasource;
import org.n52.ar.geoarCodebase.util.CodebaseProperties;
import org.n52.ar.geoarCodebase.util.HtmlHelper;
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

    public static final String FORM_INPUT_DESCRIPTION = "descriptionOfApp";

    public static final String FORM_INPUT_FILE = "fileOfApp";

    public static final String FORM_INPUT_IMAGE_LINK = "imageOfApp";

    public static final String FORM_INPUT_NAME = "nameOfApp";

    public static final String FORM_INPUT_PLATFORM = "targetPlatformOfApp";

    private static Logger log = LoggerFactory.getLogger(InfoResource.class);

    private static final String MSG_NO_RESULT = "No results matching your input!";

    private static final String SERVICE_INFO_CONTEXT_PARAM = "serviceInfo";

    private static final String SERVICE_URL_CONTEXT_PARAM = "serviceUrl";

    private static final String URL_APK_PATH = "apk";

    private String downloadUrlPostfix;

    private String downloadUrlPrefix;

    private String id = null;

    private Collection<Datasource> resources;

    private String serviceInfo;

    private boolean uploadAuthorized = false;

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
        if (entity != null) {
            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
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

                for (FileItem fi : items) {
                    if (fi.getFieldName().equals(FORM_INPUT_FILE)) {
                        String name = fi.getName();

                        if ( !FilenameUtils.getExtension(name).equals(CodebaseProperties.APK_FILE_EXTENSION)) {
                            rep = new StringRepresentation("Required .apk file missing!", MediaType.TEXT_PLAIN);
                            return rep;
                        }

                        this.id = FilenameUtils.getBaseName(name);
                        dsFileItem = fi;
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
                if (dsFileItem == null) {
                    rep = new StringRepresentation("Missing file!", MediaType.TEXT_PLAIN);
                    return rep;
                }
                
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
                else if (dsName.isEmpty() || dsDescription.isEmpty() || dsImageLink.isEmpty()
                        || dsPlatform.isEmpty())
                    rep = new StringRepresentation("Not all required information is provided!", MediaType.TEXT_PLAIN);
                
                // all info given, or taken from db
                log.debug("Read form: {}, {}, {}, {} >>> {}", new Object[] {dsName,
                                                                            dsDescription,
                                                                            dsImageLink,
                                                                            dsPlatform,
                                                                            dsFileItem.getName()});

                // handle database entry
                fileName = CodebaseProperties.getInstance().getApkPath(this.id);
                File file = new File(fileName);
                boolean fileExists = file.exists();

                if (databaseEntryExists != fileExists) {
                    log.warn("Found either database entry or file, bot not both! database: {} - file: {}",
                             Boolean.valueOf(databaseEntryExists),
                             Boolean.valueOf(fileExists));
                }

                if (fileExists) {
                    log.warn("Deleting apk file " + this.id);
                    boolean deleted = file.delete();
                    if ( !deleted)
                        log.error("Could not delete file " + fileName);
                }

                if (databaseEntryExists) {
                    db.updateResource(this.id, dsName, dsDescription, dsImageLink, dsPlatform);
                    rep = new StringRepresentation("Updated datasource (" + fileName + ").", MediaType.TEXT_PLAIN);
                }
                else {
                    db.addResource(this.id, dsName, dsDescription, dsImageLink, dsPlatform);
                    rep = new StringRepresentation("Uploaded datasource (" + fileName + ").", MediaType.TEXT_PLAIN);
                }

                // write the file!
                dsFileItem.write(file);
            }
        }
        else {
            // POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);

            log.error("POST without multipart content.");
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
        StringBuilder sb = new StringBuilder();

        HtmlHelper.beforeResult(sb);

        if (this.resources.isEmpty()) {
            sb.append(MSG_NO_RESULT);
        }
        else {
            for (Datasource ds : this.resources) {
                HtmlHelper.appendDatasource(sb, ds);
            }
        }

        if (this.uploadAuthorized) {
            HtmlHelper.appendUploadForm(sb, getReference().toString());
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
