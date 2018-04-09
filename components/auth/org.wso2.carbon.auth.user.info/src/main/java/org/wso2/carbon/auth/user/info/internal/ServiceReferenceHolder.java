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

import org.wso2.carbon.auth.user.info.configuration.UserInfoConfigurationService;
import org.wso2.carbon.config.provider.ConfigProvider;

/**
 * Class used to hold the OAuth configuration
 */
public class ServiceReferenceHolder {

    private static ServiceReferenceHolder instance = new ServiceReferenceHolder();
    private UserInfoConfigurationService userInfoConfigurationService = null;
    private ConfigProvider configProvider;

    private ServiceReferenceHolder() {
    }

    public static ServiceReferenceHolder getInstance() {
        return instance;
    }

    public UserInfoConfigurationService getUserInfoConfigurationService() {
        return userInfoConfigurationService;
    }

    void setUserInfoConfigurationService(UserInfoConfigurationService userInfoConfigurationService) {
        this.userInfoConfigurationService = userInfoConfigurationService;
    }

    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    ConfigProvider getConfigProvider() {
        return configProvider;
    }

}
