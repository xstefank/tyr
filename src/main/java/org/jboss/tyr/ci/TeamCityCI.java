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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;
import org.jboss.logging.Logger;
import org.jboss.tyr.model.TyrProperties;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.json.BuildJson;

public class TeamCityCI implements ContinuousIntegration {

    public static final String NAME = "TeamCity";

    public static final String HOST_PROPERTY = "teamcity.host";
    public static final String PORT_PROPERTY = "teamcity.port";
    public static final String USER_PROPERTY = "teamcity.user";
    public static final String PASSWORD_PROPERTY = "teamcity.password";
    public static final String BUILD_CONFIG = "teamcity.branch.mapping";

    private static final Logger log = Logger.getLogger(TeamCityCI.class);

    private String baseUrl;
    private String encryptedCredentials;
    private Map<String, String> branchMappings;

    @Override
    public void init() {
        this.baseUrl = getBaseUrl(TyrProperties.getProperty(HOST_PROPERTY),
                TyrProperties.getIntProperty(PORT_PROPERTY));

        this.encryptedCredentials = encryptCredentials(TyrProperties.getProperty(USER_PROPERTY),
                TyrProperties.getProperty(PASSWORD_PROPERTY));

        this.branchMappings = parseBranchMapping(TyrProperties.getProperty(BUILD_CONFIG));
    }

    @Override
    public void triggerBuild(JsonObject prPayload) {
        String branch = prPayload.getJsonObject(Utils.BASE).getString(Utils.REF);
        if (!branchMappings.containsKey(branch)) {
            return;
        }

        int pull = prPayload.getInt(Utils.NUMBER);
        String sha = prPayload.getJsonObject(Utils.HEAD).getString(Utils.SHA);
        String buildId = branchMappings.get(branch);

        Client client = ClientBuilder.newClient();
        URI statusUri = UriBuilder
                .fromUri(baseUrl)
                .path("/app/rest/buildQueue")
                .build();

        WebTarget target = client.target(statusUri);
        Entity<BuildJson> json = Entity.json(new BuildJson("pull/" + pull, buildId, sha, pull, branch));

        Response response = null;

        try {
            response = target.request()
                .header(HttpHeaders.ACCEPT_ENCODING, "UTF-8")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encryptedCredentials)
                .post(json);

            log.info("Teamcity status update: " + response.getStatus());
        } catch (Throwable e) {
            log.error("Cannot run Team city build", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
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
    public void triggerFailedBuild(JsonObject prPayload) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
