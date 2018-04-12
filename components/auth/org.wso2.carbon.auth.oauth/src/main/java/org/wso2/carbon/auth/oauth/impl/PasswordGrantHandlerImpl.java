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
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.Secret;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Password grant handler
 */
public class PasswordGrantHandlerImpl implements GrantHandler {
    private static final Logger log = LoggerFactory.getLogger(PasswordGrantHandlerImpl.class);
    private OAuthDAO oauthDAO;
    private ClientLookup clientLookup;
    private UserNameMapper userNameMapper;
    private UserStoreManager userStoreManager;

    PasswordGrantHandlerImpl() {
    }

    @Override
    public void init(UserNameMapper userNameMapper, OAuthDAO oauthDAO, UserStoreManager userStoreManager,
            ApplicationDAO applicationDAO) {
        this.userNameMapper = userNameMapper;
        this.oauthDAO = oauthDAO;
        this.userStoreManager = userStoreManager;
        clientLookup = new ClientLookupImpl(oauthDAO);
    }

    @Override
    public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws AuthException {
        log.debug("Calling PasswordGrantHandlerImpl:process");
        try {
            ResourceOwnerPasswordCredentialsGrant request =
                    ResourceOwnerPasswordCredentialsGrant.parse(queryParameters);
            String scope = queryParameters.get(OAuthConstants.SCOPE_QUERY_PARAM);
            processPasswordGrantRequest(authorization, context, scope, request);
        } catch (ParseException e) {
            log.error("Error while parsing Password Grant request: ", e);
            context.setErrorObject(e.getErrorObject());
        }
    }

    private void processPasswordGrantRequest(String authorization, AccessTokenContext context,
                                             @Nullable String scopeValue,
                                             ResourceOwnerPasswordCredentialsGrant request)
            throws AuthException {
        log.debug("calling processPasswordGrantRequest");
        MutableBoolean haltExecution = new MutableBoolean(false);

        boolean authenticated = validateGrant(request);
        if (authenticated) {
            context.getParams().put(OAuthConstants.AUTH_USER, request.getUsername());
        } else {
            return;
        }
        //check CK empty
        //check CK state
        if (haltExecution.isTrue()) {
            return;
        }

        //TODO: Validate username and password sent in request
        Scope scope;

        if (scopeValue != null) {
            scope = new Scope(scopeValue);
        } else {
            scope = new Scope(OAuthConstants.SCOPE_DEFAULT);
        }

        String user = (String) context.getParams().get(OAuthConstants.AUTH_USER);
        String clientId = (String) context.getParams().get(OAuthConstants.CLIENT_ID);
        String grantType = (String) context.getParams().get(OAuthConstants.GRANT_TYPE);

        Optional<AccessTokenResponse> tokenResponse = checkTokens(oauthDAO, user, grantType, clientId,
                scope.toString());
        if (tokenResponse.isPresent()) {
            AccessTokenResponse accessTokenResponse = tokenResponse.get();
            context.setAccessTokenResponse(accessTokenResponse);
            context.setSuccessful(true);
            return;
        }

        TokenGenerator.generateAccessToken(scope, context);
        AccessTokenData accessTokenData = TokenDataUtil.generateTokenData(context);
        accessTokenData.setAuthUser(userNameMapper.getLoggedInPseudoNameFromUserID(user));
        accessTokenData.setClientId(clientId);
        oauthDAO.addAccessTokenInfo(accessTokenData);
        accessTokenData.setAuthUser(user);
    }

    private boolean validateGrant(ResourceOwnerPasswordCredentialsGrant request) throws UserStoreException {
        String username = request.getUsername();
        Secret password = request.getPassword();
        return userStoreManager.doAuthenticate(username, password.getValue());
}
}
