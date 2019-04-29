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
package org.jboss.tyr.verification;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jboss.tyr.model.yaml.FormatConfig;

import static org.jboss.tyr.TestUtils.loadFormatFromYamlFile;
import static org.jboss.tyr.TestUtils.YAML_DIR;
import static org.jboss.tyr.TestUtils.FORMAT_CONFIG;

public class VerificationsTest {

    private static FormatConfig badFormatConfig;

    @BeforeClass
    public static void beforeClass() {
        badFormatConfig = loadFormatFromYamlFile(YAML_DIR + "/wrongTemplate.yaml");
    }

    @Test
    public void testReadValidFormatConfiguration() throws InvalidConfigurationException {
        VerificationHandler.verifyConfiguration(FORMAT_CONFIG);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testReadInvalidFormatConfiguration() throws InvalidConfigurationException {
        VerificationHandler.verifyConfiguration(badFormatConfig);
    }

    @Test(expected = NullPointerException.class)
    public void testNullParameterToVerify() throws InvalidConfigurationException {
        VerificationHandler.verifyConfiguration(null);
    }
}
