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

package org.n52.geoar.codebase;

import org.n52.geoar.codebase.resources.ApkFileResource;
import org.n52.geoar.codebase.resources.ImageFileResource;
import org.n52.geoar.codebase.resources.InfoResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodebaseApplication extends Application {

	private static Logger log = LoggerFactory
			.getLogger(CodebaseApplication.class);

	public static final String PARAM_ID = "id";

	public static final String PARAM_TOKEN = "token";

	private static final String ROUTE__BASE = "/";

	private static final String ROUTE_APK = ROUTE__BASE + "{" + PARAM_ID
			+ "}/apk";
	
	private static final String ROUTE_IMAGE = ROUTE__BASE + "{" + PARAM_ID
			+ "}/image";

	private static final String ROUTE_INFO = ROUTE__BASE + "{" + PARAM_ID + "}";

	public static final String FORM_ACTION = "formAction";

	// FIXME move constants out of application class
	public static final String FORM_ACTION_DELETE = "delete";

	@Override
	public synchronized Restlet createInboundRoot() {
		log.debug("Creating new inbound root: " + this);

		Router router = new Router(getContext());

		// Defines a route for the resource elevation
		router.attach("", InfoResource.class);
		router.attach(ROUTE__BASE, InfoResource.class);
		router.attach(ROUTE_INFO, InfoResource.class);
		router.attach(ROUTE_INFO + "/", InfoResource.class);
		// router.attach(ROUTE_INFO + "/" + ROUTE_UPLOAD, InfoResource.class);
		router.attach(ROUTE_INFO + "/" + FORM_ACTION, InfoResource.class);
		router.attach(ROUTE_APK, ApkFileResource.class);
		router.attach(ROUTE_APK + "/", ApkFileResource.class);
		router.attach(ROUTE_IMAGE, ImageFileResource.class);
		router.attach(ROUTE_IMAGE + "/", ImageFileResource.class);
		

		return router;
	}

}
