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

package org.wso2.carbon.auth.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.configuration.AuthConfigurationService;
import org.wso2.carbon.auth.core.configuration.models.AuthConfiguration;
import org.wso2.carbon.auth.core.configuration.models.ConfigModelsTest;
import org.wso2.carbon.auth.core.util.TestUtil;
import org.wso2.carbon.config.ConfigProviderFactory;
import org.wso2.carbon.config.provider.ConfigProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.wso2.carbon.auth.core.AuthCoreTestConstants.CONFIG_FILE_NAME;
import static org.wso2.carbon.auth.core.AuthCoreTestConstants.TEST_FOLDER_RELATIVE;
import static org.wso2.carbon.auth.core.AuthCoreTestConstants.TEST_FOLDER_RESOURCES;
import static org.wso2.carbon.auth.core.AuthCoreTestConstants.USER_STORE_TEAM_ATTR;

public class ServiceReferenceHolderTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigModelsTest.class);

    @Test(priority = 1)
    public void testGetAuthConfigurationWithNullConfigProvider() throws Exception {
        try {
            ServiceReferenceHolder.getInstance().setConfigProvider(null);
            AuthConfiguration authConfiguration = ServiceReferenceHolder.getInstance().getAuthConfiguration();

            //Even after providing a null ConfigProvider, we should not get a null AuthConfiguration
            //It should give a AuthConfiguration object with default values
            Assert.assertNotNull(authConfiguration);
            Assert.assertNotNull(authConfiguration.getUserStoreConfiguration());
            Assert.assertFalse(authConfiguration.getUserStoreConfiguration().isReadOnly());
        } catch (Exception e) {
            String errorMessage = "Error while getting AuthConfiguration";
            log.error(errorMessage, e);
            Assert.fail(errorMessage);
        }
    }

    @Test(priority = 2)
    public void testGetAuthConfiguration() throws Exception {
        Path deploymentConfigPath = Paths.get(TEST_FOLDER_RELATIVE, TEST_FOLDER_RESOURCES, CONFIG_FILE_NAME);

        try {
            ConfigProvider configProvider = ConfigProviderFactory.getConfigProvider(deploymentConfigPath);
            ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
            AuthConfiguration authConfiguration = ServiceReferenceHolder.getInstance().getAuthConfiguration();
            AuthConfiguration authConfigurationFromService = AuthConfigurationService.getInstance()
                    .getAuthConfiguration();

            log.info("AuthConfiguration loaded from " + CONFIG_FILE_NAME, authConfiguration);
            Assert.assertNotNull(authConfiguration);
            Assert.assertNotNull(authConfiguration.getUserStoreConfiguration());
            Assert.assertTrue(authConfiguration.getUserStoreConfiguration().isReadOnly());

            Assert.assertNotNull(authConfigurationFromService);
            Assert.assertNotNull(authConfigurationFromService.getUserStoreConfiguration());
            Assert.assertTrue(authConfigurationFromService.getUserStoreConfiguration().isReadOnly());
            if (TestUtil.isAttributeExists(authConfiguration.getUserStoreConfiguration(), USER_STORE_TEAM_ATTR)
                    && TestUtil.isAttributeExists(authConfigurationFromService.getUserStoreConfiguration(),
                    USER_STORE_TEAM_ATTR)) {
                return;
            }
            //this line must not reach
            Assert.fail("Unable to find \"" + USER_STORE_TEAM_ATTR + "\" attribute in the UserStoreConfiguration");
        } catch (Exception e) {
            String errorMessage = "Error while getting AuthConfiguration";
            log.error(errorMessage, e);
            Assert.fail(errorMessage);
        }
    }
}
