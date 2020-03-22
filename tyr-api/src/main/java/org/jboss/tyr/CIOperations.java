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
package org.jboss.tyr;

import jakarta.json.JsonObject;

public interface CIOperations {

    /**
     * Triggers all CI defined in format.yaml.
     * @param prPayload json received from GitHub API.
     **/
    void triggerCI(JsonObject prPayload);

    /**
     * Triggers CI (defined in format.yaml) that failed
     * for any reason.
     * @param prPayload json received from GitHub API.
     */
    void triggerFailedCI(JsonObject prPayload);

    /**
     * Checks if user has permission to run CI
     * @param username username to check.
     */
    boolean isUserEligibleToRunCI(String username);

    /**
     * Checks if username is provided in a list of
     * administrators.
     * @param username username to check.
     */
    boolean isUserAdministrator(String username);

    /**
     * Checks if username is provided in a list of
     * users.
     * @param username username to work with.
     */
    boolean isUserAlreadyWhitelisted(String username);

    /**
     * Adds the user with specified username to a list of
     * users for performing defined operations.
     * @param username username to add.
     */
    boolean addUserToUserList(String username);
}
