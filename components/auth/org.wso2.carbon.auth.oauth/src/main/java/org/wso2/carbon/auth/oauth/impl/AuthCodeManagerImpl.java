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
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.AuthCodeManager;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dto.AuthResponseContext;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;
import org.wso2.carbon.auth.oauth.exception.NoDataFoundException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Implementation of AuthCodeManager interface
 */
public class AuthCodeManagerImpl implements AuthCodeManager {
    private static final Logger log = LoggerFactory.getLogger(AuthCodeManagerImpl.class);
    private ClientDAO clientDAO;

    public AuthCodeManagerImpl(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public AuthResponseContext generateCode(Map<String, String> queryParameters) {
        AuthResponseContext context;

        try {
            AuthorizationRequest request = AuthorizationRequest.parse(queryParameters);
            context = processAuthRequest(request);
        } catch (ParseException e) {
            log.error("Error while parsing AuthorizationRequest", e);
            context = new AuthResponseContext();
            context.setRedirectUri(e.getRedirectionURI());
            context.setErrorObject(e.getErrorObject());
            context.setState(e.getState().getValue());
        }

        return context;
    }

    @Override
    public boolean isCodeValid(String code, String sentClientId) {
        return false;
    }

    private AuthResponseContext processAuthRequest(AuthorizationRequest request) {
        AuthResponseContext context = new AuthResponseContext();
        context.setState(request.getState().getValue());

        MutableBoolean haltExecution = new MutableBoolean(false);

        Optional<String> registeredRedirectUri = getRegisteredRedirectUri(context, request, haltExecution);

        if (haltExecution.isTrue()) {
            return context;
        }

        updateRedirectUriIfNotSent(context, request, registeredRedirectUri.get(), haltExecution);

        if (haltExecution.isTrue()) {
            return context;
        }

        generateAuthCode(context, request);

        return context;
    }

    private Optional<String> getRegisteredRedirectUri(AuthResponseContext context, AuthorizationRequest request,
                                                      MutableBoolean haltExecution) {
        try {
            return clientDAO.getRedirectUri(request.getClientID().getValue());
        } catch (ClientDAOException e) {
            String clientId = request.getClientID().getValue();
            log.error("Error while getting public client information for client Id: " + clientId, e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
            context.setErrorObject(error);
            haltExecution.setTrue();
        } catch (NoDataFoundException e) {
            log.error("Client Id: " + request.getClientID().getValue() + ", does not exist ", e);
            ErrorObject error = new ErrorObject(OAuth2Error.UNAUTHORIZED_CLIENT.getCode());
            context.setErrorObject(error);
            haltExecution.setTrue();
        }

        return Optional.empty();
    }

    private void updateRedirectUriIfNotSent(AuthResponseContext context, AuthorizationRequest request,
                                            @Nullable String registeredRedirectUri, MutableBoolean haltExecution) {
        URI redirectUri = request.getRedirectionURI();

        // If redirectUri is not specified in request try to lookup pre registered redirectUri for this clientId
        if (redirectUri == null) {
            if (registeredRedirectUri != null) {
                try {
                    context.setRedirectUri(new URI(registeredRedirectUri));
                } catch (URISyntaxException e) {
                    log.error("Pre-registered Client Redirect Uri syntax is invalid", e);
                    ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                    context.setErrorObject(error);
                    haltExecution.setTrue();
                }
            } else {
                log.error("Pre-registered Client Redirect Uri was not found");
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            }
        } else {
            context.setRedirectUri(redirectUri);
        }
    }

    private void generateAuthCode(AuthResponseContext context, AuthorizationRequest request) {
        if (request.getResponseType().equals(new ResponseType(ResponseType.Value.CODE))) {
            try {
                String code = new AuthorizationCode().getValue();
                Scope scope = request.getScope();
                String scopeString;

                if (scope != null) {
                    scopeString = scope.toString();
                } else {
                    scopeString = OAuthConstants.SCOPE_DEFAULT;
                }

                clientDAO.addAuthCodeInfo(code, request.getClientID().getValue(),
                        scopeString, context.getRedirectUri());

                context.setAuthCode(code);
                context.setSuccessful(true);
            } catch (ClientDAOException e) {
                String clientId = request.getClientID().getValue();
                log.error("Error while saving auth code information for client Id: " + clientId, e);
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                context.setErrorObject(error);
            }
        } else { // response_type is not equal to "code"
            String responseType = request.getResponseType().toString();
            String clientId = request.getClientID().getValue();
            log.error("Value of response_type: " + responseType + " != 'code' client Id: " + clientId);
            ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
            context.setErrorObject(error);
        }
    }
}
