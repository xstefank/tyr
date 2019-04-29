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

import org.jboss.tyr.model.yaml.FormatConfig;

public class RepositoryFormatVerification implements Verification {

    @Override
    public void verify(FormatConfig formatConfig) throws InvalidConfigurationException {
        if (!formatConfig.getRepository().matches("^[a-zA-Z0-9_]*/[a-zA-Z0-9_-]*$"))
            throw new InvalidConfigurationException("Wrong repository format in configuration file");
    }
}
