/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.user.info.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.user.info.configuration.UserInfoConfigurationService;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;
import org.wso2.carbon.auth.user.store.configuration.models.AttributeConfiguration;
import org.wso2.carbon.auth.user.store.configuration.models.Uniqueness;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.config.provider.ConfigProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class used to hold the OAuth configuration
 */
public class ServiceReferenceHolder {

    private static ServiceReferenceHolder instance = new ServiceReferenceHolder();
    private UserInfoConfigurationService userInfoConfigurationService = null;
    private ConfigProvider configProvider;
    private static final Logger log = LoggerFactory.getLogger(ServiceReferenceHolder.class);
    private UserStoreConfigurationService userStoreConfigurationService;

    private ServiceReferenceHolder() {
    }

    /**
     * Retrieve ServiceReferenceHolder instance
     *
     * @return ServiceReferenceHolder instance
     */
    public static ServiceReferenceHolder getInstance() {
        return instance;
    }

    /**
     * Retrieve UserInfoConfigurationService instance
     *
     * @return UserInfoConfigurationService instance
     */
    public UserInfoConfigurationService getUserInfoConfigurationService() {
        return userInfoConfigurationService;
    }

    /**
     * Set UserInfoConfigurationService
     */
    void setUserInfoConfigurationService(UserInfoConfigurationService userInfoConfigurationService) {
        this.userInfoConfigurationService = userInfoConfigurationService;
    }

    /**
     * Set ConfigProvider
     */
    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    /**
     * Retrieve ConfigProvider instance
     *
     * @return ConfigProvider instance
     */
    ConfigProvider getConfigProvider() {
        return configProvider;
    }

    /**
     * Retrieve list of attribute configurations
     *
     * @return List of attribute configuration
     */
    public List<AttributeConfiguration> getUserAttributeConfiguration() {

        Map configs = null;
        try {
            if (configProvider != null) {
                configs = (Map) configProvider.getConfigurationObject(UserInfoConstants
                        .USER_STORE_CONFIGURATION_NAMESPACE);
            } else {
                log.error("Configuration provider is null");
            }
        } catch (ConfigurationException e) {
            log.error("Error getting configuration for namespace " + UserInfoConstants
                    .USER_STORE_CONFIGURATION_NAMESPACE, e);
        }

        if (configs == null) {
            UserStoreConfiguration userStoreConfiguration = new UserStoreConfiguration();
            log.info("UserStoreConfiguration: Setting default configurations...");
            return userStoreConfiguration.getAttributes();
        }

        ArrayList<Object> attributes = (ArrayList) configs.get("attributes");
        List<AttributeConfiguration> mappedAttributes = new ArrayList<>();

        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                Map attribute = (Map) attributes.get(i);
                String attributeName = (String) attribute.get(UserInfoConstants.ATTRIBUTE_NAME);
                String attributeUri = (String) attribute.get(UserInfoConstants.ATTRIBUTE_URI);
                String displayName = (String) attribute.get(UserInfoConstants.DISPLAY_NAME);
                Boolean required = (Boolean) attribute.get(UserInfoConstants.REQUIRED);
                String regex = (String) attribute.get(UserInfoConstants.REGEX);
                String uniqueness = (String) attribute.get(UserInfoConstants.UNIQUENESS);

                AttributeConfiguration attributeConfiguration = new AttributeConfiguration(attributeName, attributeUri,
                        displayName, required, regex, Uniqueness.valueOf(uniqueness));
                attributeConfiguration.setAttributeName(attributeName);
                mappedAttributes.add(attributeConfiguration);
            }
        }

        return mappedAttributes;
    }

    public void setUserStoreConfigurationService(UserStoreConfigurationService userStoreConfigurationService) {

        this.userStoreConfigurationService = userStoreConfigurationService;
    }
}
