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

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Map;
import java.util.Optional;

/**
 * Grant Type Handler interface
 */
public interface GrantHandler {

    /**
     * Process grant type request to generate access token
     * 
     * @param authorization  Authorization header
     * @param context AccessTokenContext object that stores context information during request processing
     * @param queryParameters Map of query parameters sent
     *@throws AuthException When an grantHandler Error Occurred
     */
    void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws AuthException;

    /**
     * Initialise the grant type implementations with DAO
     *
     * @param userNameMapper   Username mapper object
     * @param oauthDAO         OAuthDAO instance
     * @param userStoreManager user store manager instance
     * @param applicationDAO   ApplicationDAO instance
     * @param tokenDAO         TokenDAO instance
     */
    void init(UserNameMapper userNameMapper, OAuthDAO oauthDAO, UserStoreManager userStoreManager,
            ApplicationDAO applicationDAO, TokenDAO tokenDAO);

    default boolean isAuthorizedClient(Application application, String grantType) {
        if (application == null || StringUtils.isEmpty(application.getGrantTypes())) {
            return false;
        }
        return application.getGrantTypes().contains(grantType);
    }

    /**
     * Check the previous generated token state
     *
     * @param oauthDAO  oauth DAO instance
     * @param authUser  authenticated user
     * @param grantType requested grant type
     * @param clientId  requested consumer key
     * @param scopes  requested scopes
     * @return return access token information if present
     */
    default Optional<AccessTokenResponse> checkTokens(OAuthDAO oauthDAO, String authUser, String grantType,
            String clientId, String scopes) {
        AccessTokenDTO accessTokenDTO;
        try {
            accessTokenDTO = oauthDAO.getTokenInfo(authUser, grantType, clientId, scopes);
        } catch (OAuthDAOException e) {
            return Optional.empty();
        }

        if (accessTokenDTO == null) {
            return Optional.empty();
        }
        boolean isExpired = Utils.hasAccessTokenExpired(accessTokenDTO);
        if (isExpired) {
            return Optional.empty();
        } else {
            BearerAccessToken accessToken = new BearerAccessToken(accessTokenDTO.getAccessToken(),
                    accessTokenDTO.getRefreshTokenValidityPeriod(), new Scope());
            RefreshToken refreshToken = null;
            if (!GrantType.CLIENT_CREDENTIALS.getValue().equals(grantType)) {
                refreshToken = new RefreshToken(accessTokenDTO.getRefreshToken());
            }
            Tokens tokens = new Tokens(accessToken, refreshToken);
            return Optional.of(new AccessTokenResponse(tokens));
        }
    }
}
