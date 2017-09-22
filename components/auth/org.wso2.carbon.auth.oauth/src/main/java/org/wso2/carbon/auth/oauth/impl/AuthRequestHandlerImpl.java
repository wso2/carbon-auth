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

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.AuthRequestHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dto.AuthResponseContext;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of AuthRequestHandler interface
 */
public class AuthRequestHandlerImpl implements AuthRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthRequestHandlerImpl.class);
    private ClientDAO clientDAO;

    public AuthRequestHandlerImpl(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public AuthResponseContext generateCode(Map<String, String> queryParameters) {
        log.debug("Calling generateCode");
        AuthResponseContext context;

        try {
            AuthorizationRequest request = AuthorizationRequest.parse(queryParameters);
            context = processAuthRequest(request);
        } catch (ParseException e) {
            log.info("Error while parsing AuthorizationRequest: ", e.getMessage());
            context = new AuthResponseContext();
            context.setRedirectUri(e.getRedirectionURI());
            context.setErrorObject(e.getErrorObject());
            context.setState(e.getState());
        }

        return context;
    }

    private AuthResponseContext processAuthRequest(AuthorizationRequest request) {
        log.debug("Calling processAuthRequest");
        AuthResponseContext context = new AuthResponseContext();
        context.setState(request.getState());

        MutableBoolean haltExecution = new MutableBoolean(false);

        updateRedirectUriIfNotSent(context, request, haltExecution);

        if (haltExecution.isTrue()) {
            return context;
        }

        if (request.getResponseType().equals(new ResponseType(ResponseType.Value.CODE))) { // Auth Code grant
            generateAuthCode(context, request);
        } else if (request.getResponseType().equals(new ResponseType(ResponseType.Value.TOKEN))) { // Implicit grant
            generateAccessToken(context, request);
        } else {
            String responseType = request.getResponseType().toString();
            String clientId = request.getClientID().getValue();
            log.info("Value of response_type: " + responseType + " is invalid(client Id: " + clientId);
            ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
            context.setErrorObject(error);
        }

        return context;
    }

    private void updateRedirectUriIfNotSent(AuthResponseContext context, AuthorizationRequest request,
                                            MutableBoolean haltExecution) {
        log.debug("Calling updateRedirectUriIfNotSent");
        URI redirectUri = request.getRedirectionURI();

        // If redirectUri is not specified in request try to lookup pre registered redirectUri for this clientId
        if (redirectUri == null) {
            try {
                Optional<Optional<String>> result = clientDAO.getRedirectUri(request.getClientID().getValue());
                if (result.isPresent()) {
                    Optional<String> uri = result.get();

                    if (uri.isPresent()) {
                        context.setRedirectUri(new URI(uri.get()));
                    } else {
                        log.error("Pre-registered Client Redirect Uri was not found");
                        ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                        context.setErrorObject(error);
                        haltExecution.setTrue();
                    }
                } else {
                    log.info("Client Id: " + request.getClientID().getValue() + ", does not exist ");
                    ErrorObject error = new ErrorObject(OAuth2Error.UNAUTHORIZED_CLIENT.getCode());
                    context.setErrorObject(error);
                    haltExecution.setTrue();
                }
            } catch (URISyntaxException e) {
                log.error("Pre-registered Client Redirect Uri syntax is invalid", e);
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            } catch (ClientDAOException e) {
                String clientId = request.getClientID().getValue();
                log.error("Error while getting public client information for client Id: " + clientId, e);
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            }
        } else { // Redirect Uri sent in request
            context.setRedirectUri(redirectUri);
        }
    }

    private void generateAuthCode(AuthResponseContext context, AuthorizationRequest request) {
        log.debug("Calling generateAuthCode");
        try {
            String code = new AuthorizationCode().getValue();
            String scope = getScope(request);

            clientDAO.addAuthCodeInfo(code, request.getClientID().getValue(),
                    scope, request.getRedirectionURI());

            context.setAuthCode(code);
        } catch (ClientDAOException e) {
            String clientId = request.getClientID().getValue();
            log.error("Error while saving auth code information for client Id: " + clientId, e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
            context.setErrorObject(error);
        }
    }

    private void generateAccessToken(AuthResponseContext context, AuthorizationRequest request) {
        log.debug("Calling generateAccessToken");
        Scope scope = request.getScope();

        if (scope == null) {
            scope = new Scope(OAuthConstants.SCOPE_DEFAULT);
        }

        BearerAccessToken accessToken = new BearerAccessToken(3600, scope);
        context.setAccessToken(accessToken);

        context.setTokenType(AccessTokenType.BEARER);
        context.setExpiresIn(3600L);
        context.setScope(scope);
        context.setState(request.getState());
    }

    private String getScope(AuthorizationRequest request) {
        log.debug("Calling getScope");
        Scope scope = request.getScope();
        String scopeString;

        if (scope != null) {
            scopeString = scope.toString();
        } else {
            scopeString = OAuthConstants.SCOPE_DEFAULT;
        }

        return scopeString;
    }
}
