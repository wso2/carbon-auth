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

package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.wso2.carbon.auth.oauth.TokenManager;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;

/**
 * Contains token generation logic
 */
public class TokenGenerator {
    static void generateAccessToken(String clientID, Scope scope, AccessTokenContext context) {
        BearerAccessToken accessToken = new BearerAccessToken(3600, scope);

        RefreshToken refreshToken = new RefreshToken();

        Tokens tokens = new Tokens(accessToken, refreshToken);

        context.setAccessTokenResponse(new AccessTokenResponse(tokens));
        context.setSuccessful(true);

        TokenManager tokenManager = new TokenManagerImpl();
        //todo: populate param values accordingly
        tokenManager.storeToken(accessToken.getValue(), refreshToken.getValue(), clientID,
                (String) context.getParams().get("AUTH_USER"), "primary", System.currentTimeMillis(),
                System.currentTimeMillis(), 3600, 3600, "hash", "ative", "application & user", "password");
    }
}
