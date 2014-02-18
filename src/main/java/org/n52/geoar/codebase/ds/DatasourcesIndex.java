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
