/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.rest.api.commons.internal;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.rest.api.authenticators.SecurityConfigurationService;
import org.wso2.carbon.auth.rest.api.authenticators.dto.SecurityConfiguration;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

public class BundleActivatorTest {

    @Test
    public void testInitializeBasePaths() throws Exception {

        SecurityConfigurationService securityConfigurationService = Mockito.mock(SecurityConfigurationService.class);
        SecurityConfiguration securityConfiguration = new SecurityConfiguration();
        Mockito.when(securityConfigurationService.getSecurityConfiguration()).thenReturn(securityConfiguration);
        BundleActivator bundleActivator = new BundleActivator();
        bundleActivator.setSecurityConfigurationService(securityConfigurationService);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        bundleActivator.initializeBasePaths();
    }
}
