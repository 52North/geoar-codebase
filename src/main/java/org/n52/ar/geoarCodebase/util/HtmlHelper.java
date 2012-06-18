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

package org.n52.ar.geoarCodebase.util;

import org.n52.ar.geoarCodebase.ds.Datasource;
import org.n52.ar.geoarCodebase.resources.InfoResource;

public class HtmlHelper {

    private static final String MSG_NO_LINK = "No download link available.";

    public static void afterResult(StringBuilder sb) {
        sb.append("<p style=\"color: grey\"><br />");
        sb.append("Please see the resource reference for copyright information.");
        sb.append("</p>");
        sb.append("\n");

        sb.append("</div>"); // opened in beforeResult()
        sb.append("\n");
        sb.append("</body></html>");
    }

    public static void appendDatasource(StringBuilder sb, Datasource ds) {
        sb.append("<div>");
        sb.append("\n");
        sb.append("<h2>");
        sb.append(ds.getName());
        sb.append("</h2>");
        sb.append("\n");
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
        sb.append("\n");

        sb.append("<p class=\"infotext\">");
        sb.append("Version: ");
        sb.append(ds.getVersion());
        sb.append(" | Platform: ");
        sb.append(ds.getPlatform());
        sb.append("</p>");
        sb.append("\n");

        sb.append("<p>");
        sb.append("<img src=\"");
        sb.append(ds.getImageLink());
        sb.append("\" />");
        sb.append("</p>");
        sb.append("\n");

        sb.append("</div>");
        sb.append("\n");
    }

    public static void appendUploadForm(StringBuilder sb, String reference) {
        sb.append("\n");
        sb.append("<h1>");
        sb.append("Upload");
        sb.append("</h1>");
        sb.append("\n");

        sb.append("<form method=\"post\" ");
        sb.append("action=\"");
        sb.append(reference);
        sb.append("\" ");
        sb.append("enctype=\"multipart/form-data\">");
        sb.append("\n");

        sb.append("<label for=\"");
        sb.append(InfoResource.FORM_INPUT_FILE);
        sb.append("\">File:  </label>");
        sb.append("<input name=\"");
        sb.append(InfoResource.FORM_INPUT_FILE);
        sb.append("\" type=\"file\"/>");
        sb.append("\n");

        sb.append("<br />");
        sb.append("<label for=\"");
        sb.append(InfoResource.FORM_INPUT_DESCRIPTION);
        sb.append("\">Description:  </label>");
        sb.append("<input name=\"");
        sb.append(InfoResource.FORM_INPUT_DESCRIPTION);
        sb.append("\" type=\"text\"/>");
        sb.append("\n");

        sb.append("<br />");
        sb.append("<label for=\"");
        sb.append(InfoResource.FORM_INPUT_NAME);
        sb.append("\">Name:  </label>");
        sb.append("<input name=\"");
        sb.append(InfoResource.FORM_INPUT_NAME);
        sb.append("\" type=\"text\"/>");
        sb.append("\n");

        sb.append("<br />");
        sb.append("<label for=\"");
        sb.append(InfoResource.FORM_INPUT_IMAGE_LINK);
        sb.append("\">Image URL:  </label>");
        sb.append("<input name=\"");
        sb.append(InfoResource.FORM_INPUT_IMAGE_LINK);
        sb.append("\" type=\"text\"/>");
        sb.append("\n");

        sb.append("<br />");
        sb.append("<label for=\"");
        sb.append(InfoResource.FORM_INPUT_PLATFORM);
        sb.append("\">Platform:  </label>");
        sb.append("<input name=\"");
        sb.append(InfoResource.FORM_INPUT_PLATFORM);
        sb.append("\" type=\"text\"/>");
        sb.append("\n");

        sb.append("<br />");
        sb.append("<input type=\"submit\" />");
        sb.append("\n");

        sb.append("</form>");
        sb.append("\n");

        sb.append("<p>Upload .apk files here. New files (i.e. files with a name that does not exist as an id already) "
                + "will be added, if the file is named _&lt;id&gt;.apk then the existing file is replaced "
                + "and metadata is updated; missing metadata is taken from existing datasource if applicable.</p>");
        sb.append("\n");
    }

    public static void beforeResult(StringBuilder sb) {
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("\n");
        sb.append("<head>");
        sb.append("\n");
        sb.append("<title>");
        sb.append("GeoAR Codebase Information");
        sb.append("</title>");
        sb.append("\n");
        sb.append("<link href=\"http://52north.org/templates/52n/favicon.ico\" rel=\"shortcut icon\" type=\"image/x-icon\" />");
        sb.append("\n");
        sb.append("<link rel=\"stylesheet\" href=\"http://52north.org/templates/52n/css/external-sites-template/external-site_52n-template-2011_site.css\" type=\"text/css\" media=\"all\" />");
        sb.append("\n");
        sb.append("<link rel=\"stylesheet\" href=\"http://52north.org/templates/52n/52n_menus/52n_cssmenu/52n.cssmenu.css\" type=\"text/css\" />");
        sb.append("\n");
        sb.append("<meta http-equiv=\"Content-Language\" content=\"en\" />");
        sb.append("\n");

        sb.append("<style type=\"text/css\">");
        sb.append("img {");
        sb.append("            border: 0;");
        sb.append("        }");
        sb.append("        .infotext {");
        sb.append("            color: #999;");
        sb.append("            font-size: 10pt;");
        sb.append("        }");
        sb.append("        .infotext,.infotext a:link {");
        sb.append("            color: #999;");
        sb.append("            font-size: 10pt;");
        sb.append("        }");
        sb.append("label {");
        sb.append("            display:block;");
        sb.append("            float:left;");
        sb.append("            width:100px;");
        sb.append("            text-align:right;");
        sb.append("            margin:0px 5px 0px 0px;");
        sb.append("        }");
        sb.append("</style>");
        sb.append("\n");

        sb.append("</head>");
        sb.append("\n");
        sb.append("<body>");
        sb.append("\n");
        sb.append("<div style=\"margin: 10px;\" id=\"content\">"); // closed in afterResult()
        sb.append("\n");
        sb.append("<h1>52&deg;North GeoAR Codebase</h1>");
        sb.append("\n");
    }
}
