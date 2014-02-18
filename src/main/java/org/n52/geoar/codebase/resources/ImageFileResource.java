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
