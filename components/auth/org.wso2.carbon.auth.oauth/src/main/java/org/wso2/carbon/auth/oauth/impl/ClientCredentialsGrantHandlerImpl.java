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
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Client Credentials grant handler
 */
public class ClientCredentialsGrantHandlerImpl implements GrantHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsGrantHandlerImpl.class);
    private OAuthDAO oauthDAO;
    private ApplicationDAO applicationDAO;
    private ClientLookup clientLookup;
    private UserNameMapper userNameMapper;

    ClientCredentialsGrantHandlerImpl() {
    }

    @Override
    public void init(UserNameMapper userNameMapper, OAuthDAO oauthDAO, UserStoreManager userStoreManager,
            ApplicationDAO applicationDAO, TokenDAO tokenDAO) {
        this.userNameMapper = userNameMapper;
        this.oauthDAO = oauthDAO;
        this.applicationDAO = applicationDAO;
        clientLookup = new ClientLookupImpl(oauthDAO);
    }

    @Override
    public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws OAuthDAOException {
        log.debug("Calling ClientCredentialsGrantHandlerImpl:process");
        try {
            ClientCredentialsGrant request = ClientCredentialsGrant.parse(queryParameters);
            String scope = queryParameters.get(OAuthConstants.SCOPE_QUERY_PARAM);
            processClientCredentialsGrantRequest(authorization, context, scope, request);
        } catch (ParseException e) {
            log.error("Error while parsing Client Credentials Grant request: ", e.getMessage());
            context.setErrorObject(e.getErrorObject());
        } catch (ClientRegistrationDAOException e) {
            log.error("Error while parsing retrieving Client information: ", e.getMessage());
            context.setErrorObject(OAuth2Error.INVALID_REQUEST);
        } catch (AuthException e) {
            log.error("Error while parsing Client Credentials Grant request: ", e.getMessage());
            context.setErrorObject(OAuth2Error.INVALID_REQUEST);
        }
    }

    private void processClientCredentialsGrantRequest(String authorization, AccessTokenContext context,
            @Nullable String scopeValue, ClientCredentialsGrant request) throws AuthException {
        log.debug("Calling processClientCredentialsGrantRequest");

        String clientId = (String) context.getParams().get(OAuthConstants.CLIENT_ID);
        String appOwner = (String) context.getParams().get(OAuthConstants.APPLICATION_OWNER);
        String grantType = (String) context.getParams().get(OAuthConstants.GRANT_TYPE);
        Scope scope;
        if (scopeValue != null) {
            scope = new Scope(scopeValue);
        } else {
            scope = new Scope(OAuthConstants.SCOPE_DEFAULT);
        }

        Optional<AccessTokenResponse> tokenResponse = checkTokens(oauthDAO, appOwner, grantType, clientId,
                scope.toString());
        if (tokenResponse.isPresent()) {
            AccessTokenResponse accessTokenResponse = tokenResponse.get();
            context.setAccessTokenResponse(accessTokenResponse);
            context.setSuccessful(true);
            return;
        }

        TokenGenerator.generateAccessToken(scope, context);
        AccessTokenData accessTokenData = TokenDataUtil.generateTokenData(context);
        accessTokenData.setClientId(clientId);
        accessTokenData.setAuthUser(appOwner);
        oauthDAO.addAccessTokenInfo(accessTokenData);
        accessTokenData.setAuthUser(userNameMapper.getLoggedInUserIDFromPseudoName(accessTokenData.getAuthUser()));
    }
}
