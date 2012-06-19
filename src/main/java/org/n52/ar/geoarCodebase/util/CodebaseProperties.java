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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.restlet.Application;
import org.restlet.data.Parameter;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodebaseProperties {
    public static final String APK_FILE_EXTENSION = "apk";

    private static final String CODEBASE_PATH_CONTEXT_PARAM = "codebasePath";

    private static CodebaseProperties instance;

    private static Logger log = LoggerFactory.getLogger(CodebaseProperties.class);

    private static final String PROPERTIES_FILE = "/codebase.properties";

    public static final String UPLOAD_TOKEN = "uploadToken";

    private static final String DELETE_TOKEN = "deleteToken";

    public static String getFilename(String id) {
        return id + "." + APK_FILE_EXTENSION;
    }

    public static CodebaseProperties getInstance() {
        if (instance == null)
            throw new RuntimeException("CodebaseProperties must be instantiated with Application first!");
        return instance;
    }

    public static CodebaseProperties getInstance(Application application) {
        if (instance == null)
            instance = new CodebaseProperties(application);

        return instance;
    }

    private String codebasePath;

    private Properties p;

    private ServletContext sc;

    private CodebaseProperties(Application application) {
        // private constructor for singleton pattern

        this.p = new Properties();

        InputStream inStream = CodebaseProperties.class.getResourceAsStream(PROPERTIES_FILE);
        try {
            this.p.load(inStream);
        }
        catch (IOException e) {
            log.error("Cannot read properties file.", e);
        }

        //
        Series<Parameter> parameters = application.getContext().getParameters();
        this.codebasePath = parameters.getFirstValue(CODEBASE_PATH_CONTEXT_PARAM, true);
        this.sc = (ServletContext) application.getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");

    }

    public String getApkPath(String id) {
        String realPath = this.sc.getRealPath(this.codebasePath + "/" + getFilename(id));

        return realPath;
    }

    public String getUploadToken() {
        return this.p.getProperty(UPLOAD_TOKEN);
    }

    public Object getDeleteToken() {
        return this.p.getProperty(DELETE_TOKEN);
    }

}
