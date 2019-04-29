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
package org.jboss.tyr.whitelist;

import java.util.HashMap;
import java.util.Map;

public class CommandsLoader {

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put(AddUserCommand.class.getSimpleName(), new AddUserCommand());
        commands.put(RetestCommand.class.getSimpleName(), new RetestCommand());
        commands.put(RetestFailedCommand.class.getSimpleName(), new RetestFailedCommand());
    }

    public static Command getCommand(String key) {
        return commands.get(key);
    }
}
