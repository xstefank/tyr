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
package org.jboss.tyr.check;

import javax.json.JsonObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.api.GitHubAPI;
import org.jboss.tyr.model.yaml.Format;
import org.jboss.tyr.model.yaml.FormatConfig;
import org.jboss.tyr.model.yaml.SkipPatterns;
import java.util.regex.Pattern;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GitHubAPI.class)
public class SkipCheckTest {

    private static FormatConfig formatConfig;
    private SkipPatterns skipPatterns;

    @Before
    public void before() {
        skipPatterns = new SkipPatterns();
        PowerMockito.suppress(method(GitHubAPI.class, TestUtils.READ_TOKEN));
        PowerMockito.stub(method(GitHubAPI.class, TestUtils.GET_JSON_WITH_COMMITS, JsonObject.class)).toReturn(TestUtils.TEST_COMMITS_PAYLOAD);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNullConfigParameter() {
        SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, null);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNullPayloadParameter() {
        SkipCheck.shouldSkip(null, formatConfig);
    }

    @Test
    public void testSkipByTitleRegexMatch(){
        skipPatterns.setTitle(Pattern.compile("Test PR"));
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertTrue("Method cannot match valid title regex", SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    @Test
    public void testSkipByTitleRegexNonMatch(){
        skipPatterns.setTitle(Pattern.compile("can't.*match.*this"));
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Method matched invalid title regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    @Test
    public void testSkipByCommitRegexMatch(){
        skipPatterns.setCommit(Pattern.compile("Test commit"));
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertTrue("Method cannot match valid commit regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    @Test
    public void testSkipByCommitRegexNonMatch(){
        skipPatterns.setCommit(Pattern.compile("can't.*match.*this"));
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Method matched invalid commit regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    @Test
    public void testSkipByPullRequestDescriptionRegexMatch(){
        skipPatterns.setDescription(Pattern.compile("Test description"));
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertTrue("Method cannot match valid description regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    @Test
    public void testSkipByPullRequestDescriptionRegexNonMatch(){
        skipPatterns.setDescription(Pattern.compile("can't.*match.*this"));
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Method matched invalid description regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    @Test
    public void testShouldSkipEmptySkipPatterns() {
        formatConfig = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Invalid result after empty skipping patterns",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatConfig));
    }

    private static FormatConfig setUpFormatConfig(SkipPatterns testSkipPatterns) {
        Format testFormat = new Format();
        testFormat.setSkipPatterns(testSkipPatterns);
        FormatConfig testFormatConfig = new FormatConfig();
        testFormatConfig.setFormat(testFormat);
        return testFormatConfig;
    }
}
