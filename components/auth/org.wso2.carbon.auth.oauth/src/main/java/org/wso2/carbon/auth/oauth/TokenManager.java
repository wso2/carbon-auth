/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.auth.oauth;

import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;

/**
 * Manage token related functions
 */
public interface TokenManager {
    /**
     * Persist token information
     *
     * @param accessToken
     * @param refreshToken
     * @param clientID
     * @param authUser
     * @param userDomain
     * @param timeCreated
     * @param refreshTokenCreatedTime
     * @param validityPeriod
     * @param refreshTokenValidityPeriod
     * @param tokenScopeHash
     * @param tokenState
     * @param userType
     * @param grantType
     */
    void storeToken(String accessToken, String refreshToken, String clientID, String authUser, String userDomain,
            long timeCreated, long refreshTokenCreatedTime, int validityPeriod, int refreshTokenValidityPeriod,
            String tokenScopeHash, String tokenState, String userType, String grantType);

    /**
     * Retrieve token information
     *
     * @param accessToken
     * @return
     */
    AccessTokenDTO getTokenInfo(String accessToken);
}
