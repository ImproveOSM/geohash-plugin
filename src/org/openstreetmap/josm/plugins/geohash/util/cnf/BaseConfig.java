/*
 *  Copyright 2016 Telenav, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openstreetmap.josm.plugins.geohash.util.cnf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * Defines basic methods for loading runtime properties.
 *
 * @author ioanao
 * @version $Revision$
 */
public class BaseConfig {

    private static final String SEPARATOR = ";";
    private final Properties properties;


    /**
     * Builds a new configuration object for the given configuration file.
     *
     * @param fileName the configuration file name
     */
    public BaseConfig(final String fileName) {
        final URL url = BaseConfig.class.getResource("/" + fileName);
        if (url == null) {
            // no need to catch this error, it is handled by JOSM error
            // mechanism
            throw new ExceptionInInitializerError("Could not find configuration file:" + fileName);
        }
        properties = new Properties();
        try (InputStream stream = url.openStream()) {
            properties.load(stream);
        } catch (final IOException e) {
            // no need to catch this error, it is handled by JOSM error
            // mechanism
            throw new ExceptionInInitializerError(e);
        }
    }


    /**
     * Reads the property with the given key from the given properties.
     *
     * @param key the key of a property
     * @return the value of the property
     */
    public String readProperty(final String key) {
        return properties.getProperty(key);
    }

    /**
     * Reads an array of properties identified by the given key.
     *
     * @param key the key of the property list
     * @return an array of values
     */
    public String[] readPropertiesArray(final String key) {
        final String values = properties.getProperty(key);
        return (values != null && !values.isEmpty()) ? values.split(SEPARATOR) : null;
    }

    /**
     * Reads a list of properties identifier by the given key.
     *
     * @param key the key of the property list
     * @return a list of values
     */
    public List<String> readPropertiesList(final String key) {
        final String[] values = readPropertiesArray(key);
        return values != null ? Arrays.asList(values) : new ArrayList<String>();
    }

    /**
     * Returns a set of keys contained in the properties map.
     *
     * @return a set of objects
     */
    public Set<Object> keySet() {
        return properties.keySet();
    }

}