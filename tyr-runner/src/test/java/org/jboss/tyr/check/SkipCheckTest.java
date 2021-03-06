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

import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.api.GitHubAPI;
import org.jboss.tyr.model.yaml.Format;
import org.jboss.tyr.model.yaml.FormatYaml;
import org.jboss.tyr.model.yaml.SkipPatterns;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.json.JsonObject;
import java.util.regex.Pattern;

import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GitHubAPI.class)
public class SkipCheckTest {

    private static FormatYaml formatYaml;
    private SkipPatterns skipPatterns;

    @Before
    public void before() {
        skipPatterns = new SkipPatterns();
        PowerMockito.suppress(method(GitHubAPI.class, TestUtils.READ_TOKEN));
        PowerMockito.stub(method(GitHubAPI.class, TestUtils.GET_JSON_WITH_COMMITS, JsonObject.class)).toReturn(TestUtils.TEST_COMMITS_PAYLOAD);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNullConfigParameter() throws InvalidPayloadException {
        SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, null);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNullPayloadParameter() throws InvalidPayloadException {
        SkipCheck.shouldSkip(null, formatYaml);
    }

    @Test
    public void testSkipByTitleRegexMatch() throws InvalidPayloadException {
        skipPatterns.setTitle(Pattern.compile("Test PR"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertTrue("Method cannot match valid title regex", SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    @Test
    public void testSkipByTitleRegexNonMatch() throws InvalidPayloadException {
        skipPatterns.setTitle(Pattern.compile("can't.*match.*this"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Method matched invalid title regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    @Test
    public void testSkipByCommitRegexMatch() throws InvalidPayloadException {
        skipPatterns.setCommit(Pattern.compile("Test commit"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertTrue("Method cannot match valid commit regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    @Test
    public void testSkipByCommitRegexNonMatch() throws InvalidPayloadException {
        skipPatterns.setCommit(Pattern.compile("can't.*match.*this"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Method matched invalid commit regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    @Test
    public void testSkipByPullRequestDescriptionRegexMatch() throws InvalidPayloadException {
        skipPatterns.setDescription(Pattern.compile("Test description"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertTrue("Method cannot match valid description regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    @Test
    public void testSkipByPullRequestDescriptionRegexNonMatch() throws InvalidPayloadException {
        skipPatterns.setDescription(Pattern.compile("can't.*match.*this"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Method matched invalid description regex",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    @Test
    public void testShouldSkipEmptySkipPatterns() throws InvalidPayloadException {
        formatYaml = setUpFormatConfig(skipPatterns);

        Assert.assertFalse("Invalid result after empty skipping patterns",SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml));
    }

    private static FormatYaml setUpFormatConfig(SkipPatterns testSkipPatterns) {
        Format testFormat = new Format();
        testFormat.setSkipPatterns(testSkipPatterns);
        FormatYaml testFormatYaml = new FormatYaml();
        testFormatYaml.setFormat(testFormat);
        return testFormatYaml;
    }
}
