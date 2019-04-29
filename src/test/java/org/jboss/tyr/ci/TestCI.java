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
package org.jboss.tyr.ci;

import javax.json.JsonObject;

public class TestCI implements ContinuousIntegration {

    public static final String NAME = "TestCI";
    private boolean triggered;
    private boolean triggeredFailed;

    public TestCI() {
        CILoader.addCI(NAME, this);
    }

    @Override
    public void triggerBuild(JsonObject prPayload) {
        triggered = true;
    }

    @Override
    public void triggerFailedBuild(JsonObject prPayload) {
        triggeredFailed = true;
    }

    @Override
    public void init() {
        triggered = false;
        triggeredFailed = false;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public boolean isTriggeredFailed() {
        return triggeredFailed;
    }
}
