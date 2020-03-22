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
package org.jboss.tyr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.jboss.tyr.model.yaml.FormatConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    public static final String YAML_DIR = "yaml";
    public static final String JSON_DIR = "json";
    public static final String TARGET_DIR = "target";

    public static final JsonObject TEST_PAYLOAD = loadJson(JSON_DIR + "/testPayload.json");
    public static final JsonObject ISSUE_PAYLOAD = loadJson(JSON_DIR + "/issuePayload.json");
    public static final JsonObject PULL_REQUEST_PAYLOAD = loadJson(JSON_DIR + "/testPullRequestPayload.json");
    public static final JsonObject EMPTY_PAYLOAD = createEmptyJsonPayload();

    public static final JsonArray TEST_COMMITS_PAYLOAD = loadJsonArray(JSON_DIR + "/testCommitsPayload.json");
    public static final JsonArray MULTIPLE_COMMIT_MESSAGES_PAYLOAD = loadJsonArray(JSON_DIR + "/multipleCommitMessagesPayload.json");

    public static final String READ_TOKEN = "readToken";
    public static final String GET_JSON_WITH_COMMITS = "getCommitsJSON";
    public static final FormatConfig FORMAT_CONFIG = loadFormatFromYamlFile(YAML_DIR + "/testTemplate.yaml");
    public static final Path TEST_CONFIG_PATH = getFilePath("testConfig.properties");

    public static FormatConfig loadFormatFromYamlFile(String fileName) {
        try {
            File file = getFile(fileName);
            return new ObjectMapper(new YAMLFactory()).readValue(file, FormatConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load file " + fileName);
        }
    }

    public static void deleteFileIfExists(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean fileContainsLine(File file, String line) {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader br = new BufferedReader(fileReader)) {
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                if (fileLine.equals(line)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read file located in " + file.getPath(), e);
        }
        return false;
    }

    public static void writeLineToFile(String line, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(line);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot write username to file", e);
        }
    }

    private static JsonObject loadJson(String fileName) {
        try {
            return Json.createReader(new FileReader(getFile(fileName))).readObject();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot load json", e);
        }
    }

    private static JsonArray loadJsonArray(String fileName) {
        try {
            return Json.createReader(new FileReader(getFile(fileName))).readArray();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot load json", e);
        }
    }

    private static JsonObject createEmptyJsonPayload() {
        return Json.createObjectBuilder().build();
    }

    private static Path getFilePath(String fileName) {
        try {
            return Paths.get(TestUtils.class.getClassLoader().getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot get path of file: " + fileName, e);
        }
    }

    private static File getFile(String fileName) {
        try {
            String path = TestUtils.class.getClassLoader().getResource(fileName).getFile();
            return new File(URLDecoder.decode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot get file " + fileName, e);
        }
    }
}
