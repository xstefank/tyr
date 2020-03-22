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
import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.FormatConfig;
import org.jboss.tyr.model.yaml.RegexDefinition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkipCheck {

    public static boolean shouldSkip(JsonObject payload, FormatConfig config) throws InvalidPayloadException {
        if (payload == null || config == null) {
            throw new IllegalArgumentException("Input arguments cannot be null");
        }
        return skipByTitle(payload, config) || skipByCommit(payload, config) || skipByDescriptionFirstRow(payload, config);
    }

    private static boolean skipByTitle(JsonObject payload, FormatConfig config) {
        Pattern titlePattern = config.getFormat().getSkipPatterns().getTitle();
        if (titlePattern != null) {
            Matcher titleMatcher = titlePattern.matcher(payload.getJsonObject(Utils.PULL_REQUEST).getString(Utils.TITLE));
            return titleMatcher.matches();
        }
        return false;
    }


    private static boolean skipByCommit(JsonObject payload, FormatConfig config) throws InvalidPayloadException {
        Pattern commitPattern = config.getFormat().getSkipPatterns().getCommit();
        if (commitPattern != null) {
            RegexDefinition commitRegexDefinition = new RegexDefinition();
            commitRegexDefinition.setPattern(commitPattern);
            CommitMessagesCheck commitMessagesCheck = new CommitMessagesCheck(commitRegexDefinition);
            return commitMessagesCheck.check(payload) == null;
        }
        return false;
    }

    private static boolean skipByDescriptionFirstRow(JsonObject payload, FormatConfig config) {
        Pattern descriptionPattern = config.getFormat().getSkipPatterns().getDescription();
        if (descriptionPattern != null) {
            String description = payload.getJsonObject(Utils.PULL_REQUEST).getString(Utils.BODY);
            String firstRow = description.split(Utils.GITHUB_LINE_SEPARATOR, 2)[0];
            Matcher descriptionMatcher = descriptionPattern.matcher(firstRow);
            return descriptionMatcher.matches();
        }
        return false;
    }
}
