package org.wso2.carbon.auth.oauth.internal;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.secvault.SecureVault;

import java.util.Map;

/**
 * Class used to hold the OAuth configuration
 */
public class ServiceReferenceHolder {
    private static final Logger log = LoggerFactory.getLogger(ServiceReferenceHolder.class);
    private static ServiceReferenceHolder instance = new ServiceReferenceHolder();
    private ConfigProvider configProvider;
    private OAuthConfiguration config;
    private SecureVault secureVault;

    private ServiceReferenceHolder() {}

    public static ServiceReferenceHolder getInstance() {
        return instance;
    }

    /**
     * Sets the configProvider instance
     *
     * @param configProvider configProvider instance to set
     */
    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    /**
     * Gives the AuthConfigurations explicitly set in the deployment yaml or the default configurations
     *
     * @return AuthConfigurations
     */
    public OAuthConfiguration getAuthConfigurations() {
        try {
            if (configProvider != null) {
                config = configProvider.getConfigurationObject(OAuthConfiguration.class);
            } else {
                log.error("Configuration provider is null");
            }
        } catch (ConfigurationException e) {
            log.error("error getting config : org.wso2.carbon.auth.core.internal.AuthConfiguration", e);
        }

        if (config == null) {
            config = new OAuthConfiguration();
            log.info("Setting default configurations...");
        }

        return config;
    }

    /**
    * This method is to get configuration map of a given namespace
    *
    * @param namespace namespace defined in deployment.yaml
    * @return resource path to scope mapping
    */
    public Map<String, String> getRestAPIConfigurationMap(String namespace) {
        try {
            if (configProvider != null) {
                return (Map<String, String>) configProvider.getConfigurationObject(namespace);
            } else {
                log.error("Configuration provider is null");
            }
        } catch (ConfigurationException e) {
            log.error("Error while reading the configurations map of namespace : " +
                    "org.wso2.carbon.auth.core.internal.AuthConfiguration", e);
        }
        return null;
    }

    /**
     * Gives the secure vault instance if already set
     *
     * @return secureVault instance
     */
    public SecureVault getSecureVault() {
        return secureVault;
    }

    /**
     * Sets the secure vault instance
     *
     * @param secureVault secureVault instance to set
     */
    public void setSecureVault(SecureVault secureVault) {
        this.secureVault = secureVault;
    }
}
