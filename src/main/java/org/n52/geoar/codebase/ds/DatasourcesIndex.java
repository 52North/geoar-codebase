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

import java.util.Collection;

public class DatasourcesIndex {

    private Collection<Datasource> datasources;

    private String id;

    public DatasourcesIndex() {
        // required by Jackson
    }

    public DatasourcesIndex(String id, Collection<Datasource> datasources) {
        this.id = id;
        this.datasources = datasources;
    }

    public Collection<Datasource> getDatasources() {
        return this.datasources;
    }

    public String getId() {
        return this.id;
    }

    public void setDatasources(Collection<Datasource> datasources) {
        this.datasources = datasources;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DatasourcesIndex [id=");
        builder.append(this.id);
        builder.append(", datasources=");
        builder.append(this.datasources);
        builder.append("]");
        return builder.toString();
    }

}
