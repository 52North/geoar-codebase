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
