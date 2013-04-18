<?xml version="1.0" encoding="utf-8"?>
<%--

    ﻿Copyright (C) 2012
    by 52 North Initiative for Geospatial Open Source Software GmbH

    Contact: Andreas Wytzisk
    52 North Initiative for Geospatial Open Source Software GmbH
    Martin-Luther-King-Weg 24
    48155 Muenster, Germany
    info@52north.org

    This program is free software; you can redistribute and/or modify it under
    the terms of the GNU General Public License version 2 as published by the
    Free Software Foundation.

    This program is distributed WITHOUT ANY WARRANTY; even without the implied
    WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License along with
    this program (see gnu-gpl v2.txt). If not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
    visit the Free Software Foundation web page, http://www.fsf.org.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>

<jsp:include page="head.jsp" />

</head>

<body class="composite">
	<div id="bg_h">
	</div>
	<div id="navigation_h">

		<div id="wopper" class="wopper">
			<div id="ja-mainnavwrap">
				<div id="ja-mainnav">
					<ul id="ja-cssmenu" class="clearfix">
						<!-- Durch die Klasse "active" fü Element <li> und <a> wird diese Menu hervorgehoben angezeigt -->
						<li><a href="http://geoviqua.dev.52north.org/"
							class="menu-item0 first-item" id="menu1"
							title="GeoViQua Test Server"><span class="menu-title">GeoViQua
									Test Server</span></a></li>
						<li class="menu-item4"><a href="http://52north.org/"
							class="menu-item4" id="menu5" title="52&deg;North Initiative"><span
								class="menu-title">52&deg;North Initiative</span></a></li>
					</ul>
					<div class="published-date_h">
						<span id="publishDate">Last Published: ${buildTimestamp}</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="wrapper">
		<%
		    /*
											<!-- LeftColumn kann deaktiviert werden, aber dann sollte in dieser Datei die Eigenschaft padding der Klasse #bodyColumn angepasst werden. Z.B. padding: 0 10px 0 10px; -->
					 */
		%>
		<a
			title="52&deg;North Initiative for Geospatial Open Source Software GmbH"
			href="http://52north.org/>"> <span><img border="0"
				src="http://52north.org/templates/52n/images/52n-logo.gif"
				alt="Logo of
				52&deg;North" /></span>
		</a> <br /> <br />
		<div id="bodyColumn" style="padding: 0 10px 0 10px;">
			<div id="contentBox">
				<div class="section">
					<!-- hier kommt der eigentliche Inhalt der Seite -->
					<jsp:include page="content.jsp" />
				</div>
				<!-- End of div "section" -->
			</div>
			<!-- End of div "contentBox" -->
		</div>
	</div>
	<div id="footer">
		<div id="f_top">
			<div id="f_navigation">
				<div class="fn_box">
					<h3>Communities</h3>
					<ul class="fn_list">
						<li><a href="http://52north.org/communities/sensorweb/">Sensor
								Web</a></li>
						<li><a href="http://52north.org/communities/geoprocessing">Geoprocessing</a></li>
						<li><a href="http://52north.org/communities/ilwis/overview">ILWIS</a></li>
						<li><a
							href="http://52north.org/communities/earth-observation/overview">Earth
								Observation</a></li>
						<li><a href="http://52north.org/communities/security/">Security
								&amp; Geo-RM</a></li>
						<li><a href="http://52north.org/communities/semantics/">Semantics</a></li>
						<li><a
							href="http://52north.org/communities/geostatistics/overview">Geostatistics</a></li>
						<li><a href="http://52north.org/communities/3d-community">3D
								Community</a></li>
						<li><a
							href="http://52north.org/communities/metadata-management-community/">Metadata
								Management</a></li>
					</ul>
				</div>
				<div class="fn_box">
					<h3>Get Involved</h3>
					<ul class="fn_list">
						<li><a
							href="http://52north.org/about/get-involved/partnership-levels">Partnership
								Levels</a></li>
						<li><a
							href="http://52north.org/about/get-involved/license-agreement">License
								Agreement</a></li>
					</ul>
				</div>
				<div class="fn_box">
					<h3>Affiliations</h3>
					<p>The 52&deg;North affiliations:</p>
					<a href="http://www.opengeospatial.org/" target="_blank"
						title="OGC Assiciate Members"><img border="0" alt=""
						src="http://52north.org/images/logos/ogc.gif" /></a> <br /> <a
						href="http://www.sensorweb-alliance.org/" target="_blank"
						title="Sensor Web Alliance"><img border="0" alt=""
						src="http://52north.org/images/logos/swa.gif" /></a>
				</div>
				<div class="fn_box">
					<h3>Cooperation partners</h3>
					<p>The 52&deg;North principal cooperation partners</p>
					<table cellpadding="0" border="0">
						<tbody>
							<tr>
								<td><a href="http://ifgi.uni-muenster.de/" target="_blank"
									title="Institute for Geoinformatics"><img height="40"
										border="0" width="117" alt=""
										src="http://52north.org/images/logos/ifgi.gif" /></a></td>
								<td><a href="http://www.conterra.de/" target="_blank"
									title="con terra GmbH"><img height="40" border="0"
										width="81" alt=""
										src="http://52north.org/images/logos/conterra_new.png" /></a></td>
							</tr>
							<tr>
								<td><a href="http://www.esri.com/" target="_blank"
									title="ESRI"><img height="40" border="0" width="110" alt=""
										src="http://52north.org/images/logos/esri_new.gif" /></a></td>
								<td><a href="http://www.itc.nl/" target="_blank"
									title="International Institute for Geo-Information  Science and Earth    Observation"><img
										height="40" border="0" width="34" alt=""
										src="http://52north.org/images/logos/itc.gif" /></a></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div id="f_bottom">
			<ul>
				<li><a href="http://52north.org/about/contact">Contact</a></li>
				<li><a href="http://52north.org/about/52north/disclaimer">Disclaimer</a></li>
			</ul>
			<small>Copyright &copy; </small>
			<script type="text/javascript">
			<!--
				var now = new Date();
				document.write("<small>" + now.getFullYear() + "</small>");
			//-->
			</script>
			<noscript>
				<small>2012</small>
			</noscript>
			<small>52&deg;North Initiative for Geospatial Open Source
				Software GmbH. All Rights Reserved.</small>
		</div>
	</div>
</body>

</html>