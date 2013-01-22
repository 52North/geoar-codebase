/*
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

var debug = true;
var urlBasisString = null;
var indexJson = null;

var beginsWith = function(string, pattern) {
	return (string.indexOf(pattern) === 0);
}

var endsWith = function(string, pattern) {
	var d = string.length - pattern.length;
	return (d >= 0 && string.lastIndexOf(pattern) === d);
}

var htmlLog = function(text) {
	var now = new Date();

	$("#resultLog").append("<p><em>" + now + "</em> " + text + "</p>")
}

var requestJson = function() {

	if (debug) {
		htmlLog("Requesting " + indexJson);
	}

	$.ajax({
		url : indexJson,
		contentType : "application/json",
		dataType : "json",
		success : handleResponse,
		error : function(result, status, err) {
			htmlLog("Error loading data: " + status + " >>> " + err + "\n");
			return;
		}

	});

}

var handleResponse = function(data, status) {
	// TODO implement
	htmlLog(data);
	
	// http://api.jquery.com/jQuery.getJSON/
	var items = [];

	  $.each(data, function(key, val) {
	    items.push('<li id="' + key + '">' + val + '</li>');
	  });

	  $('<ul/>', {
	    'class': 'my-new-list',
	    html: items.join('')
	  }).appendTo('body');
}

$(document).ready(function() {
	//qrlink = "http://geoviqua.dev.52north.org/geoar/GeoAR.apk"
	//jQuery("#geoar-qrcode").qrcode({
	//	width : 64,
	//	height : 64,
	//	text : qrlink
	//});

	urlBasisString = $("#serviceUrl").text();
	indexJson = urlBasisString + "/codebase";
	$("#indexJson").html("<a href=\"" + indexJson + "\">" + indexJson + "</a>");

//	requestJson();
});