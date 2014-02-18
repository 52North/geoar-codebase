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
package org.n52.geoar.codebase.resources;

import java.io.File;
import java.util.Map;

import org.n52.geoar.codebase.CodebaseApplication;
import org.n52.geoar.codebase.CodebaseDatabase;
import org.n52.geoar.codebase.util.CodebaseProperties;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApkFileResource extends ServerResource {

    private static final String APK_MEDIA_TYPE_NAME = "application/vnd.android.package-archive";

    private static MediaType APPLICATION_APK = new MediaType(APK_MEDIA_TYPE_NAME,
                                                             "Android Package management system, file archive");

    private static Logger log = LoggerFactory.getLogger(ApkFileResource.class);

    private static final String MSG_ID_NOT_FOUND = "No resource with the given ID in this codebase!";

    private String id;

    public ApkFileResource() {
        log.info("NEW {}", this);
    }

    @Override
    protected void doInit() throws ResourceException {
        Reference reference = getReference();
        log.debug("Processing {}.", reference.toString());

        Map<String, Object> requestAttributes = getRequestAttributes();

        this.id = (String) requestAttributes.get(CodebaseApplication.PARAM_ID);
        log.debug("Id: {} {}", this.id);

        CodebaseProperties.getInstance(getApplication());
    }

    @Override
    protected Representation get() throws ResourceException {
        if ( !CodebaseDatabase.getInstance().containsResource(this.id))
            throw new RuntimeException(MSG_ID_NOT_FOUND);

        String path = CodebaseProperties.getInstance().getApkPath(this.id);
        File apkFile = new File(path);

        if (!apkFile.exists()) {
            log.error("Could not load apk file from " + apkFile);
            throw new RuntimeException("Could not load file " + apkFile.getName());
        }

        Representation representation = new FileRepresentation(apkFile, APPLICATION_APK);
        Disposition disposition = representation.getDisposition();
        disposition.setFilename(CodebaseProperties.getApkFilename(this.id));
        disposition.setType(Disposition.TYPE_ATTACHMENT);

        return representation;
    }

}
