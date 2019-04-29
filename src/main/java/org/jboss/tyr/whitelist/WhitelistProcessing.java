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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import org.jboss.logging.Logger;
import org.jboss.tyr.ci.CILoader;
import org.jboss.tyr.ci.ContinuousIntegration;
import org.jboss.tyr.model.PersistentList;
import org.jboss.tyr.model.TyrProperties;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.FormatConfig;

public class WhitelistProcessing {

    public static final boolean IS_WHITELISTING_ENABLED =
            TyrProperties.getBooleanProperty(Utils.WHITELIST_ENABLED);

    private static final Logger log = Logger.getLogger(WhitelistProcessing.class);

    private final List<String> userList;
    private final List<String> adminList;

    private final List<Command> commands;
    private final List<ContinuousIntegration> continuousIntegrations;

    public WhitelistProcessing(FormatConfig config) {
        String dirName = Utils.getConfigDirectory();
        userList = new PersistentList(dirName, Utils.USERLIST_FILE_NAME);
        adminList = new PersistentList(dirName, Utils.ADMINLIST_FILE_NAME);
        commands = getCommands(config);
        continuousIntegrations = loadCIs(config);
    }

    public void processPRComment(JsonObject issuePayload) {
        if (!commands.isEmpty() &&
                issuePayload.getJsonObject(Utils.ISSUE).getJsonObject(Utils.PULL_REQUEST) != null &&
                issuePayload.getString(Utils.ACTION).matches("created")) {
            String message = issuePayload.getJsonObject(Utils.COMMENT).getString(Utils.BODY);
            for (Command command : commands) {
                if (message.matches(command.getCommandRegex())) {
                    command.process(issuePayload, this);
                }
            }
        }
    }

    public void triggerCI(JsonObject prPayload) {
        continuousIntegrations.forEach(CI -> CI.triggerBuild(prPayload));
    }

    public void triggerFailedCI(JsonObject prPayload) {
        continuousIntegrations.forEach(CI -> CI.triggerFailedBuild(prPayload));
    }

    public boolean isUserEligibleToRunCI(String username) {
        return userList.contains(username) || adminList.contains(username);
    }

    String getCommentAuthor(JsonObject issuePayload) {
        return issuePayload.getJsonObject(Utils.COMMENT).getJsonObject(Utils.USER).getString(Utils.LOGIN);
    }

    String getPRAuthor(JsonObject issuePayload) {
        return issuePayload.getJsonObject(Utils.ISSUE).getJsonObject(Utils.USER).getString(Utils.LOGIN);
    }

    boolean isUserOnAdminList(String username) {
        return adminList.contains(username);
    }

    boolean isUserOnUserList(String username) {
        return userList.contains(username);
    }

    boolean addUserToUserList(String username) {
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
            Command command = CommandsLoader.getCommand(key);
            if (command != null) {
                command.setCommandRegex(regexMap.get(key));
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
