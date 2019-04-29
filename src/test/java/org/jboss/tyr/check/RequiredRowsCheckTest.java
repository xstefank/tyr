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

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.model.yaml.RegexDefinition;

import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

public class RequiredRowsCheckTest {

    private static RegexDefinition row;
    private RequiredRowsCheck requiredRowsCheck;

    @BeforeClass
    public static void beforeClass() {
        row = new RegexDefinition();
        row.setPattern(Pattern.compile("^Test.*description$"));
        row.setMessage("Does not match");
    }

    @Before
    public void before() {
        requiredRowsCheck = new RequiredRowsCheck(singletonList(row));
    }

    @Test
    public void checkSimpleRegexMatch() {
        Assert.assertNull("Cannot match valid description", requiredRowsCheck.check(TestUtils.TEST_PAYLOAD));
    }

    @Test
    public void checkSimpleRegexNonMatch() {
        row.setPattern(Pattern.compile("can't.*match.*this"));
        Assert.assertNotNull("Matched invalid description", requiredRowsCheck.check(TestUtils.TEST_PAYLOAD));
    }

    @Test(expected = NullPointerException.class)
    public void testIfNullParameterThrowsException() {
        RequiredRowsCheck requiredRowsCheck = new RequiredRowsCheck(null);
        requiredRowsCheck.check(TestUtils.TEST_PAYLOAD);
    }

    @Test(expected = NullPointerException.class)
    public void testIfEmptyPayloadThrowsException() {
        requiredRowsCheck.check(TestUtils.EMPTY_PAYLOAD);
    }

    @Test
    public void testMultipleFailedLines() {
        RegexDefinition secondRow = new RegexDefinition();
        String expectedErrorMessage = "Second error message";

        secondRow.setPattern(Pattern.compile("(?!.*)"));
        secondRow.setMessage(expectedErrorMessage);

        requiredRowsCheck = new RequiredRowsCheck(Arrays.asList(row, secondRow));
        String errorMessage = requiredRowsCheck.check(TestUtils.TEST_PAYLOAD);

        Assert.assertNotNull(errorMessage);
        Assert.assertEquals(expectedErrorMessage, errorMessage);
    }
}
