<%--

    ﻿Copyright (C) 2012
    by 52 North Initiative for Geospatial Open Source Software GmbH

    Contact: Andreas Wytzisk
    52 North Initiative for Geospatial Open Source Software GmbH
    Martin-Luther-King-Weg 24
    48155 Muenster, Germany
    info@52north.org

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<h1>GeoAR Datasource Codebase</h1>

<p>GeoAR is an Open Source Augmented Reality Browser, see <a href="https://wiki.52north.org/bin/view/Projects/GeoAR" title="GeoAR">GeoAR Website</a>.</p>

<!--
<p>Download GeoAR:<br /><span id="geoar-qrcode"></span></p>
-->

<h2>Datasources</h2>
<div id="datasources">
<p>Datasources are listed in <b><span id="indexJson"></span></b>.</p>
</div>

<h2>Service Interface</h2>

<p>The REST(ish) service interface of 52°North Elevatar Service uses
	the following URL pattern:</p>

<pre><%=getServletContext().getInitParameter("serviceUrl")%>/codebase/&lt;id&gt;/{apk}</pre>

<p>
	This service supports content negotiation using <a
		href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">HTTP
		Accept</a> headers. Supported formats:
</p>
<ul>
	<li>HTML (text/html)</li>
	<li>JSON (application/json)</li>
	<li>plain text (text/plain)</li>
</ul>

<p class="infotext">${project.name} (${project.artifactId})
	${version}-r${buildNumber} as of ${buildTimestamp}<br /><span id="serviceUrl"><%=getServletContext().getInitParameter("serviceUrl")%></span></p>

<div id="resultLog"></div>

<div id="logos" style="">
	<ul style="list-style-type: none;">
		<li style="display: inline;"><a href="http://geoviqua.org"
			title="GeoViQua - QUAlity aware VIsualisation for the Global Earth Observation system of systems"><img
				src="images/geoviqua.png" height="74" alt="GEOVIQUA logo" /></a></li>
		<li style="display: inline; padding: 0 0 0 60px;"><a
			href="http://cordis.europa.eu/fp7/home_en.html"
			title="European Commission - Seventh Framework Programme (FP7)"><img
				src="images/fp7.png" height="67" alt="FP 7 logo" /></a></li>
	</ul>
</div>