/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.core.configuration.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.config.ConfigProviderFactory;
import org.wso2.carbon.config.provider.ConfigProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.wso2.carbon.auth.core.AuthCoreTestConstants.CONFIG_FILE_NAME;
import static org.wso2.carbon.auth.core.AuthCoreTestConstants.DEFAULT_TOKEN_VALIDITY_PERIOD;
import static org.wso2.carbon.auth.core.AuthCoreTestConstants.TEST_FOLDER_RELATIVE;
import static org.wso2.carbon.auth.core.AuthCoreTestConstants.TEST_FOLDER_RESOURCES;

public class ConfigModelsTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigModelsTest.class);

    @Test
    public void testConfigBuildFromYamlTest() throws Exception {
        Path deploymentConfigPath = Paths.get(TEST_FOLDER_RELATIVE, TEST_FOLDER_RESOURCES, CONFIG_FILE_NAME);

        // Get configuration
        try {
            ConfigProvider configProvider = ConfigProviderFactory.getConfigProvider(deploymentConfigPath);
            AuthConfiguration parentConfiguration = configProvider
                    .getConfigurationObject(AuthConfiguration.class);
            log.info("AuthConfiguration loaded from " + CONFIG_FILE_NAME, parentConfiguration);

            KeyManagerConfiguration keyManagerConfiguration = parentConfiguration.getKeyManagerConfigs();
            Assert.assertNotNull(keyManagerConfiguration);
            Assert.assertEquals(keyManagerConfiguration.getDefaultTokenValidityPeriod(), DEFAULT_TOKEN_VALIDITY_PERIOD);
        } catch (Exception e) {
            String errorMessage = "Error in building model from configuration - " + CONFIG_FILE_NAME;
            log.error(errorMessage, e);
            Assert.fail(errorMessage);
        }
    }
}
