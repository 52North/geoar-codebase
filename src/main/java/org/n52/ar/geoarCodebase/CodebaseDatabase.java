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

package org.n52.ar.geoarCodebase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.n52.ar.geoarCodebase.ds.Datasource;
import org.n52.ar.geoarCodebase.ds.DatasourcesIndex;
import org.n52.ar.geoarCodebase.util.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodebaseDatabase {

    private static Logger log = LoggerFactory.getLogger(CodebaseDatabase.class);

    private static final String INDEX_FILE = "/datasources.json";

    private static CodebaseDatabase instance;

    private HashMap<String, Datasource> resources = new HashMap<String, Datasource>();

    private CodebaseDatabase() {
        // private constructor for singleton pattern
        log.info("NEW " + this);

        init();
    }

    private void init() {
        // get index file
        InputStream indexFile = getClass().getResourceAsStream(INDEX_FILE);

        if (indexFile == null) {
            log.error("Could not load index file from " + INDEX_FILE);
            return;
        }

        // parse index file
        ObjectMapper mapper = MapperFactory.getMapper();
        try {
            DatasourcesIndex index = mapper.readValue(indexFile, DatasourcesIndex.class);

            Collection<Datasource> datasources = index.getDatasources();
            for (Datasource datasource : datasources) {
                this.resources.put(datasource.getId(), datasource);
            }

            log.info("Loaded " + index);
        }
        catch (JsonParseException e) {
            log.error("Could not parse index file.", e);
        }
        catch (JsonMappingException e) {
            log.error("Could not parse index file.", e);
        }
        catch (IOException e) {
            log.error("Could not parse index file.", e);
        }
    }

    public static CodebaseDatabase getInstance() {
        if (instance == null)
            instance = new CodebaseDatabase();

        return instance;
    }

    public Collection<Datasource> getResources() {
        return this.resources.values();
    }

    public Collection<Datasource> getResource(String id) {
        Collection<Datasource> coll = new ArrayList<Datasource>();
        if (this.resources.containsKey(id))
            coll.add(this.resources.get(id));
        return coll;
    }

    public boolean containsResource(String id) {
        return this.resources.containsKey(id);
    }

}
