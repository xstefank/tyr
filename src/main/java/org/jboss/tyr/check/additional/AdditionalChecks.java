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

import org.jboss.tyr.check.Check;

import java.util.HashMap;
import java.util.Map;

public class AdditionalChecks {

    private static final Map<String, Check> additionalChecks = new HashMap<>();

    static {
        additionalChecks.put(TitleJIRAIssueLinkIncludedCheck.class.getSimpleName(), new TitleJIRAIssueLinkIncludedCheck());
        additionalChecks.put(OneCommitOnlyCheck.class.getSimpleName(), new OneCommitOnlyCheck());
    }

    public static Check findCheck(String name) {
        return additionalChecks.get(name);
    }

}
