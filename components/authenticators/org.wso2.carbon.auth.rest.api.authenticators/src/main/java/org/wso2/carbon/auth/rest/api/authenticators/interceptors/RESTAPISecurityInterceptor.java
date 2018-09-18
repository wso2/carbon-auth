/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.rest.api.authenticators.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.api.RESTAPIAuthenticator;
import org.wso2.carbon.auth.rest.api.authenticators.dto.ErrorDTO;
import org.wso2.carbon.auth.rest.api.authenticators.dto.RestAPIInfo;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.ErrorHandler;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.ExceptionCodes;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.carbon.auth.rest.api.authenticators.internal.ServiceReferenceHolder;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.interceptor.RequestInterceptor;
import org.wso2.msf4j.internal.MSF4JConstants;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.MediaType;

/**
 * Security Interceptor that does basic authentication for REST ApI requests.
 */

public class RESTAPISecurityInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RESTAPISecurityInterceptor.class);

    /**
     * preCall is run before a handler method call is made. If any of the preCalls throw exception or return false then
     * no other subsequent preCalls will be called and the request processing will be terminated,
     * also no postCall interceptors will be called.
     *
     * @param request  HttpRequest being processed.
     * @param response HttpResponder to send response.
     * @return true if the request processing can continue, otherwise the hook should send response and return false
     * to stop further processing.
     */
    @Override
    public boolean interceptRequest(Request request, Response response) {

        Method method = (Method) request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME);

        boolean isAuthenticated;
        String requestURI = request.getUri().toLowerCase(Locale.ENGLISH);
        RestAPIInfo restAPIInfo = RestApiUtil.getElectedRestApiInfo(request);

        if (requestURI.contains("swagger.yaml")) {
            if (restAPIInfo != null) {
                response.setStatus(javax.ws.rs.core.Response.Status.OK.getStatusCode()).setEntity(restAPIInfo.getYaml
                        ()).setMediaType("text/x-yaml").send();
                return false;
            } else {
                String msg = "Couldn't find the swagger";
                ErrorDTO errorDTO = RestApiUtil.getInternalServerErrorDTO();
                log.error(msg);
                response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .setEntity(errorDTO).send();
                return false;
            }
        }
        try {
            if (restAPIInfo != null && ServiceReferenceHolder.getInstance().getSecurityConfiguration()
                    .getAuthenticator().get(restAPIInfo.getBasePath()) != null) {
                String authenticationType = getAuthenticationType(request);
                Map<String, String> authenticatorMap = ServiceReferenceHolder.getInstance()
                        .getSecurityConfiguration().getAuthenticator().get(restAPIInfo.getBasePath());
                if (StringUtils.isNotEmpty(authenticationType)) {
                    String restapiAuthenticatorName = authenticatorMap.get(authenticationType);
                    if (restapiAuthenticatorName != null) {
                        RESTAPIAuthenticator restapiAuthenticator = (RESTAPIAuthenticator) Class.forName
                                (restapiAuthenticatorName).newInstance();
                        isAuthenticated = restapiAuthenticator.authenticate(request, response, method);
                        if (!isAuthenticated) {
                            return handleSecurityError(ExceptionCodes.AUTHENTICATION_FAILURE, response,
                                    authenticationType);
                        }
                    }
                } else if (authenticatorMap != null && authenticatorMap.size() > 0) {
                    return handleSecurityError(ExceptionCodes.AUTHENTICATION_FAILURE, response, authenticatorMap
                            .keySet().iterator().next());
                }
            }
            return true;
        } catch (RestAPIAuthSecurityException e) {
            log.error(e.getMessage() + " Requested Path: " + request.getUri());
            ErrorDTO errorDTO = RestApiUtil.getInternalServerErrorDTO();
            response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).setEntity
                    (errorDTO).send();
            return false;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("Error while loading RestAPIAuthenticator", e);
            ErrorDTO errorDTO = RestApiUtil.getInternalServerErrorDTO();
            response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).setEntity
                    (errorDTO).send();
            return false;
        }
    }

    /**
     * Handles error condition
     *
     * @param errorHandler       Security error code
     * @param responder          HttpResponder instance which is used send error messages back to the client
     * @param authenticationType
     */
    private boolean handleSecurityError(ErrorHandler errorHandler, Response responder, String authenticationType) {

        ErrorDTO errorDTO = RestApiUtil.getErrorDTO(errorHandler);
        responder.setStatus(errorHandler.getHttpStatusCode());
        responder.setHeader(javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE, authenticationType);
        responder.setEntity(errorDTO);
        responder.setMediaType(MediaType.APPLICATION_JSON);
        responder.send();
        return false;
    }

    private String getAuthenticationType(Request request) {

        String header = request.getHeader(RestAPIConstants.AUTHORIZATION);
        if (StringUtils.isNotEmpty(header)) {
            if (header.contains("Basic")) {
                return RestAPIConstants.AUTH_TYPE_BASIC;
            } else if (header.contains("Bearer")) {
                return RestAPIConstants.AUTH_TYPE_OAUTH2;
            }
        }
        return null;
    }

}
