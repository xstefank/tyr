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
package org.jboss.tyr.check;

import jakarta.json.JsonObject;
import org.jboss.logging.Logger;
import org.jboss.tyr.Check;
import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.model.AdditionalResourcesLoader;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.Format;
import org.jboss.tyr.model.yaml.FormatConfig;

import java.util.ArrayList;
import java.util.List;

public class TemplateChecker {

    private static final Logger log = Logger.getLogger(TemplateChecker.class);

    private List<Check> checks;

    public TemplateChecker(FormatConfig config) {
        if (config == null || config.getFormat() == null) {
            throw new IllegalArgumentException("Input argument cannot be null");
        }
        checks = registerChecks(config.getFormat());
    }

    /**
     * Verifies the pull request payload against a set of defined checks
     *
     * @param payload the PR paylaod JSON received from GitHub
     * @return error message or empty string if there is no failure found
     */
    public String checkPR(JsonObject payload) throws InvalidPayloadException {
        log.debug("checking PR" + Utils.LINE_SEPARATOR + payload);
        String errorMessage = "";

        if (checks.isEmpty()) {
            log.warn("No checks were requested in the configuration");
            return "";
        }

        for (Check check : checks) {
            String message = check.check(payload);
            if (message != null) {
                errorMessage = message;
                break;
            }
        }

        return errorMessage;
    }

    private static List<Check> registerChecks(Format format) {
        List<Check> checks = new ArrayList<>();

        if (format.getTitle() != null) {
            checks.add(new TitleCheck(format.getTitle()));
        }

        if (format.getDescription() != null) {
            checks.add(new RequiredRowsCheck(format.getDescription().getRequiredRows()));
        }

        if (format.getCommit() != null) {
            checks.add(new CommitMessagesCheck(format.getCommit()));
        }

        checks.addAll(AdditionalResourcesLoader.loadAdditionalChecks());

        return checks;
    }
}
