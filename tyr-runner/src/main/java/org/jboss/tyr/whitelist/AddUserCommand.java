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
import org.jboss.tyr.CIOperations;
import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.api.GitHubAPI;

import javax.inject.Inject;

public class AddUserCommand extends AbstractCommand {

    @Inject
    GitHubAPI git;

    @Override
    public void process(JsonObject payload, CIOperations operations) throws InvalidPayloadException {
        String pullRequestAuthor = WhitelistProcessing.getPRAuthor(payload);
        String commentAuthor = WhitelistProcessing.getCommentAuthor(payload);

        if (operations.isUserAdministrator(commentAuthor) &&
                !operations.isUserAlreadyWhitelisted(pullRequestAuthor) &&
                operations.addUserToUserList(pullRequestAuthor)) {

            JsonObject prPayload = git.getPullRequestJSON(payload);
            operations.triggerCI(prPayload);
        }
    }
}
