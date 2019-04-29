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
package org.jboss.tyr.api;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.jboss.logging.Logger;
import org.jboss.tyr.model.CommitStatus;
import org.jboss.tyr.model.StatusPayload;
import org.jboss.tyr.model.TyrProperties;
import org.jboss.tyr.model.Utils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.jboss.tyr.model.Utils.TOKEN_PROPERTY;

public class GitHubAPI {

    private static final String oauthToken = readToken();
    private static final Logger log = Logger.getLogger(GitHubAPI.class);

    public static void updateCommitStatus(String repository, String sha, CommitStatus status,
                                          String targetUrl, String description, String context) {

        Client client = ClientBuilder.newClient();
        URI statusUri = UriBuilder
                .fromUri(Utils.GITHUB_BASE)
                .path("/repos")
                .path("/" + repository)
                .path("/statuses")
                .path("/" + sha)
                .build();

        WebTarget target = client.target(statusUri);

        Entity<StatusPayload> json = Entity.json(new StatusPayload(status.toString(),
                targetUrl, description, context));

        log.debug("Sending status: " + json);
        Response response = null;

        try {
            response = target.request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "token " + oauthToken)
                .post(json);

            log.info("Github status update: " + response.getStatus());
            log.debug("Github response: " + response.readEntity(String.class));
        } catch (Throwable e) {
            log.error("Cannot update GitHub status", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static JsonArray getCommitsJSON(JsonObject prPayload) {
        return getJSONReader(getCommitsUri(prPayload)).readArray();
    }

    public static JsonObject getPullRequestJSON(JsonObject issuePayload) {
        return getJSONReader(getPullRequestUri(issuePayload)).readObject();
    }

    static JsonReader getJSONReader(URI uri) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);

        Response response = null;

        try {
            response = target.request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "token " + oauthToken)
                .get();

            if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new IllegalArgumentException("Can not get json from URI. Authentication with invalid github token");
            }
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new IllegalArgumentException("Can not get json from URI. Response status " + response.getStatus());
            }

            String responseEntity = response.readEntity(String.class);
            return Json.createReader(new StringReader(responseEntity));
        } catch (Throwable e) {
            log.error("Cannot retrieve JSON from " + uri.toString(), e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private static URI getCommitsUri(JsonObject prPayload) {
        String url = prPayload.getJsonObject(Utils.PULL_REQUEST).getString(Utils.COMMITS_URL);
        return URI.create(url);
    }

    private static URI getPullRequestUri(JsonObject issuePayload) {
        String url = issuePayload.getJsonObject(Utils.ISSUE).getJsonObject(Utils.PULL_REQUEST).getString(Utils.URL);
        return URI.create(url);
    }

    private static String readToken() {
        String token = TyrProperties.getProperty(TOKEN_PROPERTY);
        return token == null ? System.getenv(Utils.TOKEN_ENV) : token;
    }
}
