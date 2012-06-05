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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.n52.ar.geoarCodebase.ds.Datasource;
import org.n52.ar.geoarCodebase.ds.DatasourcesIndex;
import org.n52.ar.geoarCodebase.util.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodebaseDatabase {

    private static final String INDEX_FILE = "/datasources.json";

    private static CodebaseDatabase instance;

    private static Logger log = LoggerFactory.getLogger(CodebaseDatabase.class);

    public static CodebaseDatabase getInstance() {
        if (instance == null)
            instance = new CodebaseDatabase();

        return instance;
    }

    private boolean backupIndexFile = true;

    private URL indexFile;

    private String indexId;

    private HashMap<String, Datasource> resources = new HashMap<String, Datasource>();

    private CodebaseDatabase() {
        // private constructor for singleton pattern
        log.info("NEW " + this);

        init();
    }

    public void addResource(String id, String name, String description, String imageLink) {
        Datasource ds = new Datasource();
        ds.setId(id);
        ds.setName(name);
        ds.setDescription(description);
        ds.setImageLink(imageLink);

        this.resources.put(id, ds);

        saveResources();
    }

    public boolean containsResource(String id) {
        return this.resources.containsKey(id);
    }

    public Collection<Datasource> getResource(String id) {
        Collection<Datasource> coll = new ArrayList<Datasource>();
        if (this.resources.containsKey(id))
            coll.add(this.resources.get(id));
        return coll;
    }

    public Collection<Datasource> getResources() {
        return this.resources.values();
    }

    private void init() {
        // get index file
        this.indexFile = getClass().getResource(INDEX_FILE);

        if (this.indexFile == null) {
            log.error("Could not load index file from " + INDEX_FILE);
            return;
        }
        log.info("Loaded index file: " + this.indexFile);

        // parse index file
        ObjectMapper mapper = MapperFactory.getMapper();
        try {
            DatasourcesIndex index = mapper.readValue(this.indexFile, DatasourcesIndex.class);

            Collection<Datasource> datasources = index.getDatasources();
            for (Datasource datasource : datasources) {
                this.resources.put(datasource.getId(), datasource);
            }

            this.indexId = index.getId();

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

    private void saveResources() {
        ObjectMapper mapper = MapperFactory.getMapper();

        if (this.backupIndexFile) {
            File old = new File(this.indexFile.getFile());
            File backup = new File(old.getAbsolutePath() + "_backup" + System.currentTimeMillis());
            try {
                FileUtils.copyFile(old, backup);
            }
            catch (IOException e) {
                log.error("Could not save backup file.", e);
            }

            log.debug("Backup up index file to " + backup.getAbsolutePath());
        }

        File f = new File(this.indexFile.getFile());
        f.delete();

        DatasourcesIndex newDI = new DatasourcesIndex(this.indexId, this.resources.values());

        try {
            mapper.writeValue(f, newDI);
        }
        catch (JsonGenerationException e) {
            log.error("Could not save index file.", e);
        }
        catch (JsonMappingException e) {
            log.error("Could not save index file.", e);
        }
        catch (IOException e) {
            log.error("Could not save index file.", e);
        }
    }

}
