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

package org.n52.geoar.codebase.util;

import org.n52.geoar.codebase.CodebaseApplication;
import org.n52.geoar.codebase.ds.Datasource;
import org.n52.geoar.codebase.resources.InfoResource;

public class HtmlHelper {

	private static final String MSG_NO_LINK = "No download link available.";

	private static final String ID_FILE = "idFile";

	private static final String ID_INPUT = "idInput";

	private static final String ID_DESCR = "idDescr";

	private static final String ID_IMG_LINK = "idImg";

	private static final String ID_PLATFORM = "idPlatform";

	private static final Object IMG_ALT_TEXT_PRE = "Image for datasource ";

	public static void afterResult(StringBuilder sb, String serviceInfo) {
		sb.append("<p style=\"color: grey;\"><br />");
		sb.append("Please see the resource reference for copyright information.");
		sb.append("</p>");
		sb.append("\n");

		sb.append("\n");
		sb.append("<p style=\"color: grey;\">Source: ");
		sb.append(serviceInfo);
		sb.append("</p>");
		sb.append("\n");

		sb.append("</div>"); // opened in beforeResult()
		sb.append("</body></html>");
	}

	public static void appendDatasource(StringBuilder sb, Datasource ds,
			String link) {
		sb.append("<div style=\"margin: 3em 0 3em 0;\">");
		sb.append("\n");
		sb.append("<h2>");
		sb.append("<a href=\"");
		sb.append(link);
		sb.append("\">");
		sb.append(ds.getName());
		sb.append("</a>");
		sb.append("</h2>");
		sb.append("\n");
		sb.append("<p><em>Description</em>: ");
		sb.append(ds.getDescription());
		sb.append("</p>");
		sb.append("<p><em>Download</em>: ");
		String apkPath = CodebaseProperties.getInstance()
				.getApkServiceUrl(ds.getId());
		if (apkPath.contains("http")) {
			sb.append("<a href=\"");
			sb.append(apkPath);
			sb.append("\">");
			sb.append(apkPath);
			sb.append("</a>");
		} else
			sb.append(apkPath);

		sb.append("</p>");
		sb.append("\n");

		sb.append("<p class=\"infotext\">");
		sb.append("Version: ");
		sb.append(ds.getVersion());
		sb.append(" | Publisher: ");
		sb.append(ds.getPublisher());
		sb.append("</p>");
		sb.append("\n");

		sb.append("<p>");
		sb.append("<img src=\"");
		sb.append(CodebaseProperties.getInstance().getImageServiceUrl(ds.getId()));
		sb.append("\" alt=\"");
		sb.append(IMG_ALT_TEXT_PRE);
		sb.append(ds.getId());
		sb.append("\" />");
		sb.append("</p>");
		sb.append("\n");

		sb.append("</div>");
		sb.append("\n");
	}

	public static void appendDeleteForm(StringBuilder sb, String action) {
		sb.append("\n");
		sb.append("<h1>");
		sb.append("Delete");
		sb.append("</h1>");
		sb.append("\n");

		sb.append("<form method=\"post\" ");
		sb.append("action=\"");
		sb.append(action);
		sb.append("\" enctype=\"application/x-www-form-urlencoded\">");
		// sb.append("\n");
		// sb.append("<label for=\"");
		// sb.append(InfoResource.FORM_INPUT_IDENTIFIER);
		// sb.append("\">Identifier:  </label>");
		// sb.append("<select name=\"");
		// sb.append(InfoResource.FORM_INPUT_IDENTIFIER);
		// sb.append("\" />");
		// for (String id : resourceIdentifiers) {
		// sb.append("<option value=\"");
		// sb.append(id);
		// sb.append("\">");
		// sb.append(id);
		// sb.append("</option>");
		// }
		// sb.append("</select>");

		sb.append("<input type=\"hidden\" name=\"");
		sb.append(CodebaseApplication.FORM_ACTION);
		sb.append("\" value=\"");
		sb.append(CodebaseApplication.FORM_ACTION_DELETE);
		sb.append("\" />");
		sb.append("\n");
		sb.append("<input type=\"submit\" value=\"Delete\" />");
		sb.append("\n");

		sb.append("</form>");
		sb.append("\n");

		sb.append("<p>Deleted files will not be backed up.</p>");
		sb.append("\n");
	}

	public static void beforeResult(StringBuilder sb, String serviceInfo) {
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("\n");
		sb.append("<head>");
		sb.append("\n");
		sb.append("<title>");
		sb.append("GeoAR Codebase Information");
		sb.append("</title>");
		sb.append("\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("\n");
		sb.append("<link href=\"http://52north.org/templates/52n/favicon.ico\" rel=\"shortcut icon\" type=\"image/x-icon\" />");
		sb.append("\n");
		sb.append("<link rel=\"stylesheet\" href=\"http://52north.org/templates/52n/css/external-sites-template/external-site_52n-template-2011_site.css\" type=\"text/css\" media=\"all\" />");
		sb.append("\n");
		sb.append("<link rel=\"stylesheet\" href=\"http://52north.org/templates/52n/52n_menus/52n_cssmenu/52n.cssmenu.css\" type=\"text/css\" />");
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
		sb.append("<div style=\"margin: 10px;\" id=\"content\">"); // closed in
																	// afterResult()
		sb.append("\n");
		sb.append("<h1>");
		sb.append(serviceInfo);
		sb.append("</h1>");
		sb.append("\n");
	}

	public static void appendUploadForm(StringBuilder sb, String action) {
		sb.append("\n");
		sb.append("<h1>");
		sb.append("Upload");
		sb.append("</h1>");
		sb.append("\n");

		sb.append("<form method=\"post\" ");
		sb.append("action=\"");
		sb.append(action);
		sb.append("\" ");
		sb.append("enctype=\"multipart/form-data\">");
		sb.append("\n");

		sb.append("<label for=\"");
		sb.append(ID_FILE);
		sb.append("\">File:  </label>");
		sb.append("<input name=\"");
		sb.append(InfoResource.FORM_INPUT_FILE);
		sb.append("\" type=\"file\" id=\"");
		sb.append(ID_FILE);
		sb.append("\" />");
		sb.append("\n");

		sb.append("<br />");
		sb.append("<input type=\"submit\" /> <input type=\"reset\" />");
		sb.append("\n");

		sb.append("</form>");
		sb.append("\n");

		sb.append("<p>Upload .apk plugin files here. Plugins need a valid plugin "
				+ "descriptor with at least an identifier parameter. If there is an "
				+ "existing plugin with the same identfier, it will get replaced.</p>");
		sb.append("\n");
	}
}
