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
package org.jboss.tyr.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.net.URL;
import javax.json.JsonObject;
import org.jboss.tyr.api.GitHubAPI;
import org.jboss.tyr.check.SkipCheck;
import org.jboss.tyr.check.TemplateChecker;
import org.jboss.tyr.whitelist.WhitelistProcessing;
import org.jboss.tyr.verification.InvalidConfigurationException;
import org.jboss.tyr.verification.VerificationHandler;
import org.jboss.tyr.model.CommitStatus;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.FormatConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;

import static org.jboss.tyr.model.Utils.TEMPLATE_FORMAT_FILE;

@Path("/")
public class WebHookEndpoint {

    private static final FormatConfig config = readConfig();
    private static final WhitelistProcessing whitelistProcessing =
            WhitelistProcessing.IS_WHITELISTING_ENABLED ? new WhitelistProcessing(config) : null;

    private TemplateChecker templateChecker = new TemplateChecker(config);

    @POST
    @Path("/pull-request")
    @Consumes(MediaType.APPLICATION_JSON)
    public void processRequest(JsonObject payload) {
        if (whitelistProcessing != null) {
            processPRWithWhitelisting(payload);
        } else if (payload.getJsonObject(Utils.PULL_REQUEST) != null) {
            processPullRequest(payload);
        }
    }

    private static FormatConfig readConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        FormatConfig formatConfig;
        try {
            URL formatFileUrl = Utils.getFormatUrl();
            if (formatFileUrl != null)
                formatConfig = mapper.readValue(formatFileUrl.openStream(), FormatConfig.class);
            else {
                String configFileName = System.getProperty(TEMPLATE_FORMAT_FILE);
                if (configFileName == null) {
                    configFileName = Utils.getConfigDirectory() + "/format.yaml";
                }
                File configFile = new File(configFileName);
                formatConfig = mapper.readValue(configFile, FormatConfig.class);
            }
            VerificationHandler.verifyConfiguration(formatConfig);
            return formatConfig;
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalArgumentException("Cannot load configuration file", e);
        }
    }

    private void processPullRequest(JsonObject prPayload) {
        if (!SkipCheck.shouldSkip(prPayload, config)) {
            String errorMessage = templateChecker.checkPR(prPayload);
            if (errorMessage != null) {
                GitHubAPI.updateCommitStatus(config.getRepository(),
                        prPayload.getJsonObject(Utils.PULL_REQUEST).getJsonObject(Utils.HEAD).getString(Utils.SHA),
                        errorMessage.isEmpty() ? CommitStatus.SUCCESS : CommitStatus.ERROR,
                        config.getStatusUrl(),
                        errorMessage.isEmpty() ? "valid" : errorMessage, "PR format");
            }
        }
    }
    private void processPRWithWhitelisting(JsonObject payload) {
        if (payload.getJsonObject(Utils.ISSUE) != null) {
            whitelistProcessing.processPRComment(payload);
        } else if (payload.getJsonObject(Utils.PULL_REQUEST) != null) {
            processPullRequest(payload);
            if (!payload.getString(Utils.ACTION).matches("opened")) {
                return;
            }
            String username = payload.getJsonObject(Utils.PULL_REQUEST)
                    .getJsonObject(Utils.USER)
                    .getString(Utils.LOGIN);
            if (whitelistProcessing.isUserEligibleToRunCI(username)) {
                whitelistProcessing.triggerCI(payload.getJsonObject(Utils.PULL_REQUEST));
            }
        }
    }
}
