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
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

/**
 * Contains token generation logic
 */
public class TokenGenerator {
    static void generateAccessToken(Scope scope, AccessTokenContext context) {
        OAuthConfiguration configuration = ServiceReferenceHolder.getInstance().getAuthConfigurations();

        long defaultValidityPeriod = (long) context.getParams().get(OAuthConstants.VALIDITY_PERIOD);
        int accessTokenLength = configuration.getAccessTokenLength();
        int refreshTokenLength = configuration.getRefreshTokenLength();
        Identifier accessTokenIdentifier = new Identifier(accessTokenLength);
        Identifier refreshTokenIdentifier = new Identifier(refreshTokenLength);
        String accessTokenPrefix = configuration.getAccessTokenPrefix();
        String refreshTokenPrefix = configuration.getRefreshTokenPrefix();
        String accessTokenValue = accessTokenPrefix + accessTokenIdentifier.getValue();
        String refreshTokenValue = refreshTokenPrefix + refreshTokenIdentifier.getValue();

        BearerAccessToken accessToken = new BearerAccessToken(accessTokenValue, defaultValidityPeriod, scope);
        String grantTypeValue = (String) context.getParams().get(OAuthConstants.GRANT_TYPE);
        RefreshToken refreshToken = null;
        if (!GrantType.CLIENT_CREDENTIALS.getValue().equals(grantTypeValue)) {
            refreshToken = new RefreshToken(refreshTokenValue);
        }
        Tokens tokens = new Tokens(accessToken, refreshToken);
        context.setAccessTokenResponse(new AccessTokenResponse(tokens));
        context.setSuccessful(true);
    }
}
