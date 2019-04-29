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
package org.jboss.tyr.model.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class Format {

    private RegexDefinition title;
    private RegexDefinition commit;
    private SkipPatterns skipPatterns;
    private Description description;
    private List<String> additionalChecks;
    private Map<String, String> commands;

    @JsonProperty("CI")
    private List<String> CI;

    public SkipPatterns getSkipPatterns() {
        return skipPatterns;
    }

    public void setSkipPatterns(SkipPatterns skipPatterns) {
        this.skipPatterns = skipPatterns;
    }

    public RegexDefinition getTitle() {
        return title;
    }

    public void setTitle(RegexDefinition title) {
        this.title = title;
    }

    public RegexDefinition getCommit() {
        return commit;
    }

    public void setCommit(RegexDefinition commit) {
        this.commit = commit;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public List<String> getAdditionalChecks() {
        return additionalChecks;
    }

    public void setAdditionalChecks(List<String> additionalChecks) {
        this.additionalChecks = additionalChecks;
    }

    public Map<String, String> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, String> commands) {
        this.commands = commands;
    }

    public List<String> getCI() {
        return CI;
    }

    public void setCI(List<String> CI) {
        this.CI = CI;
    }
}
