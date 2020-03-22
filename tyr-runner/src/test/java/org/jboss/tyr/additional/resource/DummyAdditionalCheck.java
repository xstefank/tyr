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
package org.jboss.tyr.additional.resource;

import jakarta.json.JsonObject;
import org.jboss.tyr.Check;

import java.util.concurrent.atomic.AtomicInteger;

public class DummyAdditionalCheck implements Check {

    private static final String MESSAGE = "Dummy check failure";
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String check(JsonObject payload) {
        counter.incrementAndGet();
        return MESSAGE;
    }

    public static int getCounterValue() {
        return counter.intValue();
    }

    public static String getMessage() {
        return MESSAGE;
    }
}
