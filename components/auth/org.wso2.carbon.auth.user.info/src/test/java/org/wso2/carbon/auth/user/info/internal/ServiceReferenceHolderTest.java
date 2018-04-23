/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.user.info.internal;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.user.info.configuration.UserInfoConfigurationService;
import org.wso2.carbon.auth.user.info.configuration.models.UserInfoConfiguration;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.store.configuration.models.AttributeConfiguration;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.config.provider.ConfigProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceReferenceHolderTest {


    @Test
    public void testGetUserInfoConfigurationService() {

        UserInfoConfiguration userInfoConfiguration = Mockito.mock(UserInfoConfiguration.class);
        UserInfoConfigurationService userInfoConfigurationService = new UserInfoConfigurationService
                (userInfoConfiguration);
        ServiceReferenceHolder.getInstance().setUserInfoConfigurationService(userInfoConfigurationService);
        Assert.assertNotNull(ServiceReferenceHolder.getInstance().getUserInfoConfigurationService());
        Assert.assertEquals(ServiceReferenceHolder.getInstance().getUserInfoConfigurationService()
                .getUserInfoConfiguration(), userInfoConfiguration);

        ServiceReferenceHolder.getInstance().setUserInfoConfigurationService(null);
        Assert.assertNull(ServiceReferenceHolder.getInstance().getUserInfoConfigurationService());
    }

    @Test
    public void testGetUserInfoConfigurationWithNullConfigProvider() {

        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
        Assert.assertEquals(ServiceReferenceHolder.getInstance().getConfigProvider(), configProvider);

        ServiceReferenceHolder.getInstance().setConfigProvider(null);
        Assert.assertNull(ServiceReferenceHolder.getInstance().getConfigProvider());
    }


    @Test
    public void testGetUserAttributeConfiguration() throws Exception {

        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
        Map<String, Object> configMap = new HashMap<>();
        ArrayList<Object> attributeConfigurations = new ArrayList<>();

        Map<String, Object> attribute1 = new HashMap<>();
        attribute1.put(UserInfoConstants.ATTRIBUTE_NAME, "name1");
        attribute1.put(UserInfoConstants.ATTRIBUTE_URI, "uri");
        attribute1.put(UserInfoConstants.DISPLAY_NAME, "display1");
        attribute1.put(UserInfoConstants.REGEX, "*");
        attribute1.put(UserInfoConstants.REQUIRED, true);
        attribute1.put(UserInfoConstants.UNIQUENESS, "SERVER");
        attributeConfigurations.add(attribute1);

        configMap.put(UserInfoConstants.ATTRIBUTES, attributeConfigurations);

        Mockito.when(configProvider.getConfigurationObject(UserInfoConstants.USER_STORE_CONFIGURATION_NAMESPACE))
                .thenReturn(configMap);

        List<AttributeConfiguration> result = ServiceReferenceHolder.getInstance().getUserAttributeConfiguration();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    public void testGetUserAttributeConfigurationForNullAndException() throws Exception {

        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
        Map<String, Object> configMap = new HashMap<>();
        Mockito.when(configProvider.getConfigurationObject(UserInfoConstants.USER_STORE_CONFIGURATION_NAMESPACE))
                .thenThrow(ConfigurationException.class).thenReturn(configMap);

        Assert.assertNotNull(ServiceReferenceHolder.getInstance().getUserAttributeConfiguration());
        List<AttributeConfiguration> result = ServiceReferenceHolder.getInstance().getUserAttributeConfiguration();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 0);
    }
}

