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
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.net.URI;
import java.util.Map;

/**
 * Implementation of TokenRequestHandler interface
 */
public class TokenRequestHandlerImpl implements TokenRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(TokenRequestHandlerImpl.class);
    private ClientDAO clientDAO;

    public TokenRequestHandlerImpl(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public AccessTokenContext generateToken(String authorization, Map<String, String> queryParameters) {
        AccessTokenContext context;

        try {
            AuthorizationCodeGrant request = AuthorizationCodeGrant.parse(queryParameters);
            context = processAuthCodeGrantRequest(authorization, request);
        } catch (ParseException e) {
            log.error("Error while parsing AuthorizationCodeGrant request", e);
            context = new AccessTokenContext();
            context.setErrorObject(e.getErrorObject());
        }
        return context;
    }

    private AccessTokenContext processAuthCodeGrantRequest(String authorization, AuthorizationCodeGrant request) {
        AccessTokenContext context = new AccessTokenContext();

        MutableBoolean haltExecution = new MutableBoolean(false);

        String clientId = getClientId(authorization, context, haltExecution);

        if (haltExecution.isTrue()) {
            return context;
        }

        Scope scope = getScope(clientId, request, context, haltExecution);

        if (haltExecution.isTrue()) {
            return context;
        }

        generateAccessToken(request, scope, context);

        return context;
    }

    private String getClientId(String authorization, AccessTokenContext context, MutableBoolean haltExecution) {
        if (!StringUtils.isEmpty(authorization)) {
            try {
                ClientSecretBasic clientCredentials = ClientSecretBasic.parse(authorization);

                ClientID clientId = clientCredentials.getClientID();
                Secret clientSecret = clientCredentials.getClientSecret();
                boolean isValid = clientDAO.isClientCredentialsValid(clientId.getValue(), clientSecret.getValue());

                if (!isValid) {
                    ErrorObject error = new ErrorObject(OAuth2Error.INVALID_CLIENT.getCode());
                    context.setErrorObject(error);
                    haltExecution.setTrue();
                }

                return clientId.getValue();
            } catch (ParseException e) {
                log.error("Error while parsing client credentials", e);
                context.setErrorObject(e.getErrorObject());
                haltExecution.setTrue();
            } catch (ClientDAOException e) {
                log.error("Error while validating client credentials", e);
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            }
        } else {
            log.error("Authorization header is missing");
            ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
            context.setErrorObject(error);
            haltExecution.setTrue();
        }

        return "";
    }


    private Scope getScope(String clientId, AuthorizationCodeGrant request, AccessTokenContext context,
                          MutableBoolean haltExecution) {
        String authCode = request.getAuthorizationCode().getValue();
        URI redirectionURI = request.getRedirectionURI();

        try {
            String scope = clientDAO.getScopeForAuthCode(authCode, clientId, redirectionURI);

            if (scope != null) {
                return new Scope(scope);
            } else {
                ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            }
        } catch (ClientDAOException e) {
            log.error("Error while validating query parameters", e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
            context.setErrorObject(error);
            haltExecution.setTrue();
        }

        return null;
    }

    private void generateAccessToken(AuthorizationCodeGrant request, Scope scope, AccessTokenContext context) {
        BearerAccessToken accessToken = new BearerAccessToken(3600, scope);

        RefreshToken refreshToken = new RefreshToken();

        Tokens tokens = new Tokens(accessToken, refreshToken);

        context.setAccessTokenResponse(new AccessTokenResponse(tokens));
        context.setSuccessful(true);
    }
}
