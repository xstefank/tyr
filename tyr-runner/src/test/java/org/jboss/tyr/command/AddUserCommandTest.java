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
package org.jboss.tyr.command;

import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.TestUtils;
import org.junit.Assert;

// Temporary disable before the move to Junit 5 and CDI model

public class AddUserCommandTest extends CommandTest {

    private final AddUserCommand addUserCommand = new AddUserCommand();

//    @Test
    public void testAddUserCommand() throws InvalidPayloadException {
        addUserCommand.process(TestUtils.ISSUE_PAYLOAD, whitelistProcessing);
        Assert.assertTrue(TestUtils.fileContainsLine(userListFile, PR_AUTHOR));
        Assert.assertTrue(testCI.isTriggered());
    }

//    @Test(expected = NullPointerException.class)
    public void testAddUserCommandNullParams() throws InvalidPayloadException {
        addUserCommand.process(null, null);
    }
}
