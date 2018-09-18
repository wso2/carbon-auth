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

package org.wso2.carbon.auth.rest.api.authenticators;

import org.wso2.carbon.auth.rest.api.authenticators.dto.RestAPIInfo;
import org.wso2.carbon.auth.rest.api.authenticators.dto.SecurityConfiguration;
import org.wso2.carbon.auth.rest.api.authenticators.internal.ServiceReferenceHolder;

import java.util.Map;

/**
 * Configuration Service Implementation
 */
public class SecurityConfigurationService {

    private static SecurityConfigurationService securityConfigurationService = new SecurityConfigurationService();

    private SecurityConfigurationService() {

    }

    public static SecurityConfigurationService getInstance() {

        return securityConfigurationService;
    }

    public SecurityConfiguration getSecurityConfiguration() {

        return ServiceReferenceHolder.getInstance().getSecurityConfiguration();
    }

    public Map<String, RestAPIInfo> getRestAPIInfoMap() {

        return ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap();
    }
}
