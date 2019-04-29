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

import javax.json.JsonObject;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.RegexDefinition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleCheck implements Check {

    static final String DEFAULT_MESSAGE = "Invalid title content";

    private Pattern pattern;
    private String message;

    public TitleCheck(RegexDefinition title) {
        if (title == null || title.getPattern() == null) {
            throw new IllegalArgumentException("Input argument cannot be null");
        }
        pattern = title.getPattern();
        message = (title.getMessage() != null) ? title.getMessage() : DEFAULT_MESSAGE;
    }

    @Override
    public String check(JsonObject payload) {
        Matcher matcher = pattern.matcher(payload.getJsonObject(Utils.PULL_REQUEST).getString(Utils.TITLE));
        if (!matcher.matches()) {
            return message;
        }

        return null;
    }
}
