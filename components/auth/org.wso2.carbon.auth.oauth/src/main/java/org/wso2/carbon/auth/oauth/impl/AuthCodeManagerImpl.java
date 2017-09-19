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
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.AuthCodeManager;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dto.AuthResponse;
import org.wso2.carbon.auth.oauth.dto.ClientPublicInfo;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

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
    public AuthResponse generateCode(Map<String, String> queryParameters) {
        AuthResponse response;

        try {
            AuthorizationRequest request = AuthorizationRequest.parse(queryParameters);
            response = processAuthRequest(request);
        } catch (ParseException e) {
            log.error("Error while parsing AuthorizationRequest", e);
            response = new AuthResponse();
            response.setRedirectUri(e.getRedirectionURI());
            response.setErrorObject(e.getErrorObject());
            response.setState(e.getState().getValue());
        }

        return response;
    }

    @Override
    public boolean isCodeValid(String code, String sentClientId) {
        return false;
    }

    private AuthResponse processAuthRequest(AuthorizationRequest request) {
        AuthResponse response = new AuthResponse();
        response.setState(request.getState().getValue());


        MutableBoolean haltExecution = new MutableBoolean(false);

        ClientPublicInfo publicInfo = getClientInfoForClientId(response, request, haltExecution);

        if (haltExecution.isTrue()) {
            return response;
        }

        updateRedirectUriIfNotSent(response, request, publicInfo, haltExecution);

        if (haltExecution.isTrue()) {
            return response;
        }

        generateAuthCode(response, request, publicInfo);

        return response;
    }

    private ClientPublicInfo getClientInfoForClientId(AuthResponse response, AuthorizationRequest request,
                                                      MutableBoolean haltExecution) {
        try {
            Optional<ClientPublicInfo> publicInfo = clientDAO.getClientPublicInfo(request.getClientID().getValue());

            if (publicInfo.isPresent()) {
                return publicInfo.get();
            } else {
                log.error("Client Id: " + request.getClientID().getValue() + ", does not exist ");
                ErrorObject error = new ErrorObject(OAuth2Error.UNAUTHORIZED_CLIENT.getCode());
                response.setErrorObject(error);
                haltExecution.setTrue();
            }
        } catch (ClientDAOException e) {
            String clientId = request.getClientID().getValue();
            log.error("Error while getting public client information for client Id: " + clientId, e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
            response.setErrorObject(error);
            haltExecution.setTrue();
        }

        return null;
    }

    private void updateRedirectUriIfNotSent(AuthResponse response, AuthorizationRequest request,
                                            ClientPublicInfo publicInfo, MutableBoolean haltExecution) {
        URI redirectUri = request.getRedirectionURI();

        // If redirectUri is not specified in request try to lookup pre registered redirectUri for this clientId
        if (redirectUri == null) {
            String registeredRedirectUri = publicInfo.getRedirectUri();

            if (registeredRedirectUri != null) {
                try {
                    response.setRedirectUri(new URI(registeredRedirectUri));
                } catch (URISyntaxException e) {
                    log.error("Pre-registered Client Redirect Uri syntax is invalid", e);
                    ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                    response.setErrorObject(error);
                    haltExecution.setTrue();
                }
            } else {
                log.error("Pre-registered Client Redirect Uri was not found");
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                response.setErrorObject(error);
                haltExecution.setTrue();
            }
        } else {
            response.setRedirectUri(redirectUri);
        }
    }

    private void generateAuthCode(AuthResponse response, AuthorizationRequest request, ClientPublicInfo publicInfo) {
        if (request.getResponseType().equals(new ResponseType(ResponseType.Value.CODE))) {
            try {
                String code = new AuthorizationCode().getValue();
                clientDAO.addAuthCodeInfo(code, publicInfo.getClientId(), response.getRedirectUri());

                response.setAuthCode(code);
                response.setSuccessful(true);
            } catch (ClientDAOException e) {
                String clientId = request.getClientID().getValue();
                log.error("Error while saving auth code information for client Id: " + clientId, e);
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                response.setErrorObject(error);
            }
        } else { // response_type is not equal to "code"
            String responseType = request.getResponseType().toString();
            String clientId = request.getClientID().getValue();
            log.error("Value of response_type: " + responseType + " != 'code' client Id: " + clientId);
            ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
            response.setErrorObject(error);
        }
    }
}
