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
package org.jboss.tyr.model;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.check.TemplateChecker;

import static org.jboss.tyr.TestUtils.TEST_CONFIG_PATH;

public class ConfigTest {

    private final String currentDir = System.getProperty("user.dir");

    private Properties properties = TyrProperties.loadProperties(
            TEST_CONFIG_PATH.getParent().toString(),
            TEST_CONFIG_PATH.getFileName().toString());


    @Test
    public void testValidTemplateConfig() {
        TemplateChecker templateChecker = new TemplateChecker(TestUtils.FORMAT_CONFIG);
        Assert.assertTrue(templateChecker.checkPR(TestUtils.TEST_PAYLOAD).isEmpty());
    }

    @Test
    public void readTokenTest() {
        Assert.assertEquals("351wa351d38aw4c97w98f7987ew98f987we97gs4",
                properties.getProperty(Utils.TOKEN_PROPERTY));
    }

    @Test
    public void readUrlTest() {
        Assert.assertEquals("https://www.someniceurlhere.org/",
                properties.getProperty(Utils.TEMPLATE_FORMAT_URL));
    }

    @Test
    public void testConfigPath() {
        Assert.assertEquals(currentDir, Utils.getConfigDirectory());
        System.setProperty(Utils.TYR_CONFIG_DIR, TestUtils.TARGET_DIR);
        Assert.assertEquals(TestUtils.TARGET_DIR, Utils.getConfigDirectory());
    }
}
