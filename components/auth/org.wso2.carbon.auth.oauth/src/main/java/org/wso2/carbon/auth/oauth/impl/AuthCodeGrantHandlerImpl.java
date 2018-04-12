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

import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.net.URI;
import java.util.Map;

/**
 * Authorization code grant handler
 */
public class AuthCodeGrantHandlerImpl implements GrantHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthCodeGrantHandlerImpl.class);
    private OAuthDAO oauthDAO;
    private ClientLookup clientLookup;
    private UserNameMapper userNameMapper;

    AuthCodeGrantHandlerImpl() {
    }

    @Override
    public void init(UserNameMapper userNameMapper, OAuthDAO oauthDAO, UserStoreManager userStoreManager,
            ApplicationDAO applicationDAO) {
        this.userNameMapper = userNameMapper;
        this.oauthDAO = oauthDAO;
        clientLookup = new ClientLookupImpl(oauthDAO);
    }

    @Override
    public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws OAuthDAOException {
        log.debug("Calling AuthCodeGrantHandlerImpl:process");
        try {
            AuthorizationCodeGrant request = AuthorizationCodeGrant.parse(queryParameters);
            processAuthCodeGrantRequest(authorization, context, request);
        } catch (ParseException e) {
            log.info("Error while parsing AuthorizationCode Grant request: ", e.getMessage());
            context.setErrorObject(e.getErrorObject());
        }
    }

    private void processAuthCodeGrantRequest(String authorization, AccessTokenContext context,
                                             AuthorizationCodeGrant request) throws OAuthDAOException {
        log.debug("Calling processAuthCodeGrantRequest");
        MutableBoolean haltExecution = new MutableBoolean(false);

        String clientId = (String) context.getParams().get(OAuthConstants.CLIENT_ID);

        if (haltExecution.isTrue()) {
            return;
        }

        Scope scope = getScope(clientId, request, context, haltExecution);

        if (haltExecution.isTrue()) {
            return;
        }

        TokenGenerator.generateAccessToken(scope, context);

        AccessTokenData accessTokenData = TokenDataUtil.generateTokenData(context);

        oauthDAO.addAccessTokenInfo(accessTokenData);
    }

    private Scope getScope(String clientId, AuthorizationCodeGrant request, AccessTokenContext context,
                           MutableBoolean haltExecution) {
        String authCode = request.getAuthorizationCode().getValue();
        URI redirectionURI = request.getRedirectionURI();

        try {
            String scope = oauthDAO.getScopeForAuthCode(authCode, clientId, redirectionURI);

            if (scope != null) {
                return new Scope(scope);
            } else {
                ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            }
        } catch (OAuthDAOException e) {
            log.error("Error while validating query parameters", e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
            context.setErrorObject(error);
            haltExecution.setTrue();
        }

        return null;
    }
}
