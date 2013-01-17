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

package org.n52.ar.geoarCodebase.resources;

import java.io.File;
import java.util.Map;

import org.n52.ar.geoarCodebase.CodebaseApplication;
import org.n52.ar.geoarCodebase.CodebaseDatabase;
import org.n52.ar.geoarCodebase.util.CodebaseProperties;
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
