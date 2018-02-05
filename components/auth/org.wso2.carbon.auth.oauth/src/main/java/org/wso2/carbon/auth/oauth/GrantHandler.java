/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.oauth;

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.util.Map;

/**
 * Grant Type Handler interface
 */
public interface GrantHandler {
    /**
     * Process grant type request to generate access token
     * @param authorization  Authorization header
     * @param context AccessTokenContext object that stores context information during request processing
     * @param queryParameters Map of query parameters sent
     */
    void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws OAuthDAOException;

    default boolean isAuthorizedClient(Application application, String grantType) {
        if (application == null || StringUtils.isEmpty(application.getGrantTypes())) {
            return false;
        }
        return application.getGrantTypes().contains(grantType);
    }
}
