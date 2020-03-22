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

import jakarta.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.tyr.CIOperations;
import org.jboss.tyr.Command;
import org.jboss.logging.Logger;
import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.ci.CILoader;
import org.jboss.tyr.ci.ContinuousIntegration;
import org.jboss.tyr.model.AdditionalResourcesLoader;
import org.jboss.tyr.model.PersistentList;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.FormatConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhitelistProcessing implements CIOperations {

    private static final Logger log = Logger.getLogger(WhitelistProcessing.class);

    @ConfigProperty(name = "user.names.dir", defaultValue = ".")
    String dirName;

    private final List<String> userList;
    private final List<String> adminList;

    private final List<Command> commands;
    private final List<ContinuousIntegration> continuousIntegrations;

    public WhitelistProcessing(FormatConfig config) {
        userList = new PersistentList(dirName, Utils.USERLIST_FILE_NAME);
        adminList = new PersistentList(dirName, Utils.ADMINLIST_FILE_NAME);
        commands = getCommands(config);
        commands.addAll(AdditionalResourcesLoader.loadAdditionalCommands());
        continuousIntegrations = loadCIs(config);
    }

    static String getCommentAuthor(JsonObject issuePayload) {
        return issuePayload.getJsonObject(Utils.COMMENT).getJsonObject(Utils.USER).getString(Utils.LOGIN);
    }

    static String getPRAuthor(JsonObject issuePayload) {
        return issuePayload.getJsonObject(Utils.ISSUE).getJsonObject(Utils.USER).getString(Utils.LOGIN);
    }

    public void processPRComment(JsonObject issuePayload) throws InvalidPayloadException {
        if (!commands.isEmpty() &&
                issuePayload.getJsonObject(Utils.ISSUE).getJsonObject(Utils.PULL_REQUEST) != null &&
                issuePayload.getString(Utils.ACTION).matches("created")) {
            String message = issuePayload.getJsonObject(Utils.COMMENT).getString(Utils.BODY);
            for (Command command : commands) {
                if (message.matches(command.getRegex())) {
                    command.process(issuePayload, this);
                }
            }
        }
    }

    @Override
    public void triggerCI(JsonObject prPayload) {
        continuousIntegrations.forEach(CI -> {
            try {
                CI.triggerBuild(prPayload);
            } catch (InvalidPayloadException e) {
                throw new IllegalArgumentException("Cannot load PR payload", e);
            }
        });
    }

    @Override
    public void triggerFailedCI(JsonObject prPayload) {
        continuousIntegrations.forEach(CI -> {
            try {
                CI.triggerFailedBuild(prPayload);
            } catch (InvalidPayloadException e) {
                throw new IllegalArgumentException("Cannot load PR payload", e);
            }
        });
    }

    @Override
    public boolean isUserEligibleToRunCI(String username) {
        return userList.contains(username) || adminList.contains(username);
    }

    @Override
    public boolean isUserAdministrator(String username) {
        return adminList.contains(username);
    }

    @Override
    public boolean isUserAlreadyWhitelisted(String username) {
        return userList.contains(username);
    }

    @Override
    public boolean addUserToUserList(String username) {
        if (userList.contains(username)) {
            return false;
        }
        return userList.add(username);
    }

    private List<Command> getCommands(FormatConfig config) {
        List<Command> commands = new ArrayList<>();

        Map<String, String> regexMap = config.getFormat().getCommands();
        if (regexMap == null || regexMap.isEmpty()) {
            return commands;
        }

        for (String key : regexMap.keySet()) {
            AbstractCommand command = CommandsLoader.getCommand(key);
            if (command != null) {
                command.setRegex(regexMap.get(key));
                commands.add(command);
            } else {
                log.warnf("Command identified with \"%s\" does not exists", key);
            }
        }

        return commands;
    }

    private List<ContinuousIntegration> loadCIs(FormatConfig config) {
        List<ContinuousIntegration> continuousIntegrations = new ArrayList<>();
        List<String> CIConfigList = config.getFormat().getCI();

        if (CIConfigList == null || CIConfigList.isEmpty()) {
            return continuousIntegrations;
        }

        for (String key : CIConfigList) {
            ContinuousIntegration CI = CILoader.getCI(key);
            if (CI != null) {
                CI.init();
                continuousIntegrations.add(CI);
            } else {
                log.warnf("CI identified with \"%s\" does not exists", key);
            }
        }
        return continuousIntegrations;
    }
}
