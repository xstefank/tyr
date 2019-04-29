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
package org.jboss.tyr.check.additional;

import javax.json.JsonObject;
import org.jboss.tyr.check.Check;
import org.jboss.tyr.model.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleJIRAIssueLinkIncludedCheck implements Check {

    private static final String JIRA_PREFIX = "https://issues.jboss.org/browse/";
    private static Pattern titlePatter = Pattern.compile("WFLY-\\d+");

    @Override
    public String check(JsonObject payload) {
        //expecting title in format [WFLY-XYZ] subject or WFLY-XYZ subject
        Matcher matcher = titlePatter.matcher(payload.getJsonObject(Utils.PULL_REQUEST).getString(Utils.TITLE));
        if (matcher.find()) {
            String titleIssue = matcher.group();
            String description = payload.getJsonObject(Utils.PULL_REQUEST).getString(Utils.BODY);

            if (!description.contains(JIRA_PREFIX + titleIssue)) {
                return "The description does not contain the link to issue in the PR title";
            }
        }

        return null;
    }
}
