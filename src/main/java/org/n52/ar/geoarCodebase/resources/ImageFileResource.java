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

public class ImageFileResource extends ServerResource {

	private static Logger log = LoggerFactory
			.getLogger(ImageFileResource.class);

	private static final String MSG_ID_NOT_FOUND = "No resource with the given ID in this codebase!";

	private String id;

	public ImageFileResource() {
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
		if (!CodebaseDatabase.getInstance().containsResource(this.id))
			throw new RuntimeException(MSG_ID_NOT_FOUND);

		File imageFile = new File(CodebaseProperties.getInstance()
				.getImagePath(this.id));

		if (!imageFile.exists()) {
			log.error("Could not load image file from " + imageFile.getPath());
			throw new RuntimeException(this.id + " has no image");
		}

		Representation representation = new FileRepresentation(imageFile,
				MediaType.IMAGE_PNG);
		Disposition disposition = representation.getDisposition();
		disposition.setFilename(imageFile.getName());
		disposition.setType(Disposition.TYPE_ATTACHMENT);

		return representation;
	}

}
