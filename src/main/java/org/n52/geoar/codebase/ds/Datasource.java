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
package org.n52.geoar.codebase.ds;

public class Datasource {

	private String description;

	private String id;

	private String name;

	private Long version;

	private int revision;

	private String publisher;

	public String getDescription() {
		return this.description;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Long getVersion() {
		return this.version;
	}

	public int getRevision() {
		return this.revision;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Datasource [description=");
		builder.append(this.description);
		builder.append(", id=");
		builder.append(this.id);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", version=");
		builder.append(this.version);
		builder.append(", rev=");
		builder.append(this.revision);
		builder.append("]");
		return builder.toString();
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublisher() {
		return publisher;
	}
}
