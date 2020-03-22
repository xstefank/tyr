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
package org.jboss.tyr.ci;

import jakarta.json.JsonObject;
import org.jboss.logging.Logger;
import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.model.TeamCityProperties;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.json.BuildJson;
import org.jboss.tyr.model.json.Property;
import org.jboss.tyr.model.json.SnapshotDependencies;
import org.jboss.tyr.model.json.SnapshotDependency;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TeamCityCI implements ContinuousIntegration {

    public static final String NAME = "TeamCity";
    public static final String BUILD_PATH = "/app/rest/buildQueue";
    public static final String SNAPSHOTDEPENDENCIES_PATH_PREFIX = "app/rest/buildTypes/id:";
    public static final String SNAPSHOTDEPENDENCIES_PATH_SUFFIX = "/snapshot-dependencies";
    public static final String BUILD_WITH_SAME_REVISIONS_PROPERTY = "take-started-build-with-same-revisions";
    public static final String SUCCESSFUL_BUILDS_ONLY_PROPERTY = "take-successful-builds-only";

    private static final Logger log = Logger.getLogger(TeamCityCI.class);

    @Inject
    TeamCityProperties properties;

    private String baseUrl;
    private String encryptedCredentials;
    private Map<String, String> branchMappings;
    private Client client;
    private Jsonb jsonb;

    @Override
    public void init() {
        this.baseUrl = getBaseUrl(properties.getHost(), properties.getPort());
        this.encryptedCredentials = encryptCredentials(properties.getUsername(), properties.getPassword());
        this.branchMappings = parseBranchMapping(properties.getMapping());

        this.client = ClientBuilder.newClient();

        this.jsonb = JsonbBuilder.create();
    }

    @Override
    public void triggerBuild(JsonObject prPayload) throws InvalidPayloadException {
        addJsonToBuildQueue(prPayload, false);
    }

    private String encryptCredentials(String username, String password) {
        String authStr = username + ":" + password;
        return DatatypeConverter.printBase64Binary(authStr.getBytes(StandardCharsets.UTF_8));
    }

    private String getBaseUrl(String host, int port) {
        return String.format("http://%s:%d/httpAuth", host, port);
    }

    private Map<String, String> parseBranchMapping(String mappings) {
        Map<String, String> branchMap = new HashMap<>();

        for (String mapping : mappings.split(",")) {
            String[] parts = mapping.split("=>");
            branchMap.put(parts[0].trim(), parts[1].trim());
        }

        return branchMap;
    }

    @Override
    public void triggerFailedBuild(JsonObject prPayload) throws InvalidPayloadException {
        addJsonToBuildQueue(prPayload, true);
    }

    private void addJsonToBuildQueue(JsonObject prPayload, boolean retestOnlyFailed) throws InvalidPayloadException {
        String branch;
        int pull;
        String sha;
        String buildId;

        try {
            branch = prPayload.getJsonObject(Utils.BASE).getString(Utils.REF);
            if (!branchMappings.containsKey(branch)) {
                return;
            }

            pull = prPayload.getInt(Utils.NUMBER);
            sha = prPayload.getJsonObject(Utils.HEAD).getString(Utils.SHA);
            buildId = branchMappings.get(branch);
        } catch (NullPointerException e) {
            throw new InvalidPayloadException("Invalid payload, can't retrieve all elements. ", e);
        }

        setSnapshotDependencies(buildId, retestOnlyFailed);

        WebTarget target = getTeamCityTarget(BUILD_PATH);
        Entity<BuildJson> json = Entity.json(new BuildJson("pull/" + pull, buildId, sha, pull, branch));

        Response response = null;

        try {
            response = getRequestBuilder(target).post(json);
            log.info("Teamcity status update: " + response.getStatus());
        } catch (Throwable e) {
            log.error("Cannot run Team city build", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private void setSnapshotDependencies(String buildId, boolean retestOnlyFailed) {
        SnapshotDependencies snapshotDependencies = getSnapshotDependencies(buildId);

        if (snapshotDependencies.snapshotDependencyCount.intValue() < 1) {
            log.info("No snapshot dependency.");
            return;
        }

        for (SnapshotDependency dependency : snapshotDependencies.snapshotDependencies) {
            for (Property prop : dependency.properties.propertyList) {
                if (prop.name.equals(BUILD_WITH_SAME_REVISIONS_PROPERTY) || prop.name.equals(SUCCESSFUL_BUILDS_ONLY_PROPERTY)) {
                    prop.value = String.valueOf(retestOnlyFailed);
                }
            }
        }

        String snapshotDependenciesString = jsonb.toJson(snapshotDependencies);
        Entity<String> json = Entity.json(snapshotDependenciesString);
        Response response = null;

        try {
            response = getSnapshotDependenciesRequestBuilder(buildId).put(json);
            log.debug("Sent JSON with snapshot dependencies in Teamcity. Status: " + response.getStatus());
        } catch (Throwable e) {
            log.error("Cannot send JSON to targeted URI", e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private SnapshotDependencies getSnapshotDependencies(String buildId) {
        Response response = null;
        SnapshotDependencies snapshotDependencies;

        try {
            response = getSnapshotDependenciesRequestBuilder(buildId).get();
            log.debug("Got JSON with snapshot dependencies from Teamcity. Status: " + response.getStatus());
            String json = response.readEntity(String.class);
            snapshotDependencies = jsonb.fromJson(json, SnapshotDependencies.class);
        } catch (Throwable e) {
            log.error("Cannot retrieve JSON from targeted URI", e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return snapshotDependencies;
    }

    private Invocation.Builder getSnapshotDependenciesRequestBuilder(String buildId){
        String path = SNAPSHOTDEPENDENCIES_PATH_PREFIX + buildId + SNAPSHOTDEPENDENCIES_PATH_SUFFIX;
        WebTarget target = getTeamCityTarget(path);
        return getRequestBuilder(target);
    }

    private Invocation.Builder getRequestBuilder(WebTarget target) {
        return target.request()
                .header(HttpHeaders.ACCEPT_ENCODING, "UTF-8")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encryptedCredentials)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header("Origin", baseUrl);
    }

    private WebTarget getTeamCityTarget(String path) {
        URI statusUri = UriBuilder
                .fromUri(baseUrl)
                .path(path)
                .build();

        return client.target(statusUri);
    }
}
