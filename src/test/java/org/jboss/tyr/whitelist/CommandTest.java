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

import java.io.File;
import javax.json.JsonObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.api.GitHubAPI;
import org.jboss.tyr.ci.CILoader;
import org.jboss.tyr.ci.TestCI;
import org.jboss.tyr.model.Utils;

import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CILoader.class, GitHubAPI.class})
public abstract class CommandTest {

    public static final String PR_AUTHOR = "prUser";
    public static final String COMMENT_USER = "commentUser";

    static TestCI testCI;
    static File userListFile;
    private static File adminListFile;

    WhitelistProcessing whitelistProcessing;

    @BeforeClass
    public static void beforeClass() {
        userListFile = new File(System.getProperty(Utils.TYR_CONFIG_DIR), Utils.USERLIST_FILE_NAME);
        adminListFile = new File(System.getProperty(Utils.TYR_CONFIG_DIR), Utils.ADMINLIST_FILE_NAME);

        TestUtils.deleteFileIfExists(userListFile);
        TestUtils.deleteFileIfExists(adminListFile);
        TestUtils.writeLineToFile(COMMENT_USER, adminListFile);

        testCI = new TestCI();
    }

    @Before
    public void before() {
        // It is required to stub each method again for each invocation
        PowerMockito.stub(method(GitHubAPI.class, "getPullRequestJSON", JsonObject.class))
                .toReturn(TestUtils.TEST_PAYLOAD);

        whitelistProcessing = new WhitelistProcessing(TestUtils.FORMAT_CONFIG);
        testCI.init();
    }

    @After
    public void after() {
        TestUtils.deleteFileIfExists(userListFile);
        // Admin list is being reused
    }

    @AfterClass
    public static void afterClass() {
        TestUtils.deleteFileIfExists(adminListFile);
    }
}
