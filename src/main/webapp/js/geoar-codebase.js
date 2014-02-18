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
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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