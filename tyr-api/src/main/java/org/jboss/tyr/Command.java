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

/**
 * Command that can process issue payload received from GitHub API
 */
public interface Command {

    /**
     * This method is used for command processing.
     * You can define your own functionality of new command
     * that you want to implement. Method is triggered automatically
     * when the command regex is matched.
     *
     * @param payload json received from GitHub API.
     * @param operations class that offers some useful functionality
     *                            for working with Continuous Integration.
     */
    void process(JsonObject payload, CIOperations operations) throws InvalidPayloadException;

    /**
     * Method for getting regex for matching comment content.
     * @return a command regex
     */
    String getRegex();
}
