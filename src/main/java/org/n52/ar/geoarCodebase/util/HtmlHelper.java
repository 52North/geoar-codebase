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

public class HtmlHelper {

    public static String afterResult() {
        StringBuilder sb = new StringBuilder();

        sb.append("<p style=\"color: grey\"><br />");
        sb.append("Please see the resource reference for copyright information.");
        sb.append("</p>");

        sb.append("</div>"); // opened in beforeResult()
        sb.append("</body></html>");

        return (sb.toString());
    }

    public static String beforeResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<head>");
        sb.append("<title>");
        sb.append("GeoAR Codebase Information");
        sb.append("</title>");
        sb.append("<link href=\"http://52north.org/templates/52n/favicon.ico\" rel=\"shortcut icon\" type=\"image/x-icon\" />");
        sb.append("<link rel=\"stylesheet\" href=\"http://52north.org/templates/52n/css/external-sites-template/external-site_52n-template-2011_site.css\" type=\"text/css\" media=\"all\" />");
        sb.append("<link rel=\"stylesheet\" href=\"http://52north.org/templates/52n/52n_menus/52n_cssmenu/52n.cssmenu.css\" type=\"text/css\" />");
        sb.append("<meta http-equiv=\"Content-Language\" content=\"en\" />");
        sb.append("</head>");
        sb.append("<div style=\"margin: 10px;\" id=\"content\">"); // closed in afterResult()
        sb.append("<h1>52&deg;North GeoAR Codebase</h1>");
        sb.append("</p>");

        return (sb.toString());
    }

}
