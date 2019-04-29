/*
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tyr.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.jboss.tyr.model.Utils.CONFIG_FILE;

public class TyrProperties {

    private static final Properties properties =
            loadProperties(Utils.getConfigDirectory(), CONFIG_FILE);

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static int getIntProperty(String key) {
        String stringProp = properties.getProperty(key);

        try {
            return Integer.parseInt(stringProp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property cannot be converted to integer", e);
        }
    }

    static Properties loadProperties(String dirName, String fileName) {
        Properties properties = new Properties();
        if (dirName != null && fileName != null) {
            File dir = new File(dirName);
            File fileProp = new File(dir, fileName);
            if (fileProp.exists()) {
                try (InputStream is = new FileInputStream(fileProp)) {
                    properties.load(is);
                } catch (IOException e) {
                    throw new IllegalArgumentException("There was a problem with reading properties", e);
                }
            }
        }
        return properties;
    }
}
