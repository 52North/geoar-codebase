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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.n52.ar.geoarCodebase.CodebaseApplication;
import org.n52.ar.geoarCodebase.CodebaseDatabase;
import org.n52.ar.geoarCodebase.ds.Datasource;
import org.n52.ar.geoarCodebase.util.HtmlHelper;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoResource extends ServerResource {

    private static final String MSG_NO_LINK = "No download link available.";

    private static final String SERVICE_URL_CONTEXT_PARAM = "serviceUrl";

    private static final String CODEBASE_PATH_CONTEXT_PARAM = "codebase";

    private static final String URL_APK_PATH = "apk";

    private static final String SERVICE_INFO_CONTEXT_PARAM = "serviceInfo";

    private static final Object MSG_NO_RESULT = "No results matching your input!";

    private static Logger log = LoggerFactory.getLogger(InfoResource.class);

    private Collection<Datasource> resources;

    private String id = null;

    private String downloadUrlPrefix;

    private String downloadUrlPostfix;

    private String serviceInfo;

    public InfoResource() {
        log.info("NEW {}", this);

        Series<Parameter> parameters = getApplication().getContext().getParameters();
        this.downloadUrlPrefix = parameters.getFirstValue(SERVICE_URL_CONTEXT_PARAM, true) + "/"
                + CODEBASE_PATH_CONTEXT_PARAM + "/";
        this.downloadUrlPostfix = "/" + URL_APK_PATH;
        this.serviceInfo = parameters.getFirstValue(SERVICE_INFO_CONTEXT_PARAM, true);
    }

    @Override
    protected void doInit() throws ResourceException {
        Reference reference = getReference();
        log.debug("Processing {}.", reference.toString());

        Map<String, Object> requestAttributes = getRequestAttributes();

        this.id = (String) requestAttributes.get(CodebaseApplication.PARAM_ID);
        log.debug("Id: {} {}", this.id);

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

        return downloadUrlPrefix + ds.getId() + this.downloadUrlPostfix;
    }

    @Get("html")
    public Representation toHtml() {
        StringBuilder sb = new StringBuilder();

        String s = HtmlHelper.beforeResult();
        sb.append(s);

        if (this.resources.isEmpty()) {
            sb.append(MSG_NO_RESULT);
        }
        else {
            for (Datasource ds : this.resources) {
                sb.append("<div>");
                sb.append("<h2>");
                sb.append(ds.getName());
                sb.append("</h2>");
                sb.append("<p><em>Description</em>: ");
                sb.append(ds.getDescription());
                sb.append("</p>");
                sb.append("<p><em>Download</em>: ");
                if (ds.getDownloadLink() != null) {
                    if (ds.getDownloadLink().contains("http")) {
                        sb.append("<a href=\"");
                        sb.append(ds.getDownloadLink());
                        sb.append("\">");
                        sb.append(ds.getDownloadLink());
                        sb.append("</a>");
                    }
                    else
                        sb.append(ds.getDownloadLink());
                }
                else
                    sb.append(MSG_NO_LINK);
                sb.append("</p>");
                sb.append("<p>");
                sb.append("<img src=\"");
                sb.append(ds.getImageLink());
                sb.append("\" />");
                sb.append("</p>");
                sb.append("</div>");
            }
        }

        s = HtmlHelper.afterResult();
        sb.append(s);

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
