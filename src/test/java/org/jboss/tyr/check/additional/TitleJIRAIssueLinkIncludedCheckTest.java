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
package org.jboss.tyr.check.additional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jboss.tyr.TestUtils;

public class TitleJIRAIssueLinkIncludedCheckTest {

    private TitleJIRAIssueLinkIncludedCheck titleJIRAIssueLinkIncludedCheck;

    @Before
    public void before() {
        titleJIRAIssueLinkIncludedCheck = new TitleJIRAIssueLinkIncludedCheck();
    }

    @Test
    public void testIfTitleHasRightFormat() {
        Assert.assertNull("Cannot match link to the issue",
                titleJIRAIssueLinkIncludedCheck.check(TestUtils.TEST_PAYLOAD));
    }

    @Test
    public void testIfTitleHasBadFormat() {
        Assert.assertNotNull("Matched invalid title issue link",
                titleJIRAIssueLinkIncludedCheck.check(TestUtils.BAD_TEST_PAYLOAD));
    }

    @Test(expected = NullPointerException.class)
    public void testIfNullParameterThrowsException() {
        titleJIRAIssueLinkIncludedCheck.check(null);
    }

    @Test(expected = NullPointerException.class)
    public void testIfEmptyPayloadThrowsException() {
        titleJIRAIssueLinkIncludedCheck.check(TestUtils.EMPTY_PAYLOAD);
    }
}
