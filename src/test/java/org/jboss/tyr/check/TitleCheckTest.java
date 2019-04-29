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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.model.yaml.RegexDefinition;
import java.util.regex.Pattern;

public class TitleCheckTest {

    private RegexDefinition titleRegexDefinition;
    private TitleCheck titleCheck;

    @Before
    public void before() {
        titleRegexDefinition = new RegexDefinition();
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNullTitleParameter() {
        new TitleCheck(null);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testNullCommitPatternParameter() {
        titleRegexDefinition.setPattern(null);
        new TitleCheck(titleRegexDefinition);
    }

    @Test
    public void testCheckSimpleRegexMatch() {
        titleRegexDefinition.setPattern(Pattern.compile("^Test.*PR$"));
        titleCheck = new TitleCheck(titleRegexDefinition);

        Assert.assertNull("Cannot match valid regex", titleCheck.check(TestUtils.TEST_PAYLOAD));
    }

    @Test
    public void testCheckSimpleRegexNonMatchReturnsExpectedMessage() {
        titleRegexDefinition.setPattern(Pattern.compile("can't.*match.*this"));
        titleRegexDefinition.setMessage("This is titleRegexDefinition message");
        titleCheck = new TitleCheck(titleRegexDefinition);

        Assert.assertNotNull("Matched invalid regex", titleCheck.check(TestUtils.TEST_PAYLOAD));
        Assert.assertEquals("Unexpected message returned",
                titleRegexDefinition.getMessage(), titleCheck.check(TestUtils.TEST_PAYLOAD));
    }

    @Test
    public void testCheckSimpleRegexNonMatchReturnsDefaultMessage() {
        titleRegexDefinition.setPattern(Pattern.compile("can't.*match.*this"));
        titleCheck = new TitleCheck(titleRegexDefinition);

        Assert.assertEquals("Unexpected message returned",
                TitleCheck.DEFAULT_MESSAGE, titleCheck.check(TestUtils.TEST_PAYLOAD));
    }
}
