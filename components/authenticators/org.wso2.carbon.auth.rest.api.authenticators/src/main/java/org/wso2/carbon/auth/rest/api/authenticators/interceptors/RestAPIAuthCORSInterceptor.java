/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.rest.api.authenticators.interceptors;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.dto.ErrorDTO;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.interceptor.RequestInterceptor;
import org.wso2.msf4j.internal.MSF4JConstants;

import java.lang.reflect.Method;

/**
 * Security Interceptor that does basic authentication for REST ApI requests.
 */
@Component(
        name = "org.wso2.carbon.auth.rest.api.authenticators.interceptors.RestAPIAuthCORSInterceptor",
        service = RequestInterceptor.class,
        immediate = true
)
public class RestAPIAuthCORSInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RestAPIAuthCORSInterceptor.class);

    @Override
    public boolean interceptRequest(Request request, Response response) {

        Method method = (Method) request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME);

        //CORS for Environments - Add allowed Origin when User-Agent sent 'Origin' header.
        String origin = request.getHeader(RestAPIConstants.ORIGIN_HEADER);
        String allowedOrigin = RestApiUtil.getAllowedOrigin(origin);
        if (allowedOrigin != null) {
            response.setHeader(RestAPIConstants.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, allowedOrigin)
                    .setHeader(RestAPIConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
        }

        //CORS for Environments - Add allowed Methods and Headers when 'OPTIONS' method is called.
        if (request.getHttpMethod().equalsIgnoreCase(RestAPIConstants.HTTP_OPTIONS)) {
            try {
                String definedHttpMethods = RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method);
                if (definedHttpMethods != null) {
                    response.setHeader(RestAPIConstants.ACCESS_CONTROL_ALLOW_METHODS_HEADER, definedHttpMethods)
                            .setHeader(RestAPIConstants.ACCESS_CONTROL_ALLOW_HEADERS_HEADER,
                                    RestAPIConstants.ACCESS_CONTROL_ALLOW_HEADERS_LIST)
                            .setStatus(javax.ws.rs.core.Response.Status.OK.getStatusCode()).send();
                    return false;
                } else {
                    String msg = "Couldn't find declared HTTP methods in swagger.yaml";
                    ErrorDTO errorDTO = RestApiUtil.getInternalServerErrorDTO();
                    log.error(msg);
                    response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                            .setEntity(errorDTO).send();
                    return false;
                }
            } catch (RestAPIAuthSecurityException e) {
                String msg = "Couldn't find declared HTTP methods in swagger.yaml";
                ErrorDTO errorDTO = RestApiUtil.getErrorDTO(e.getErrorHandler());
                log.error(msg, e);
                response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .setEntity(errorDTO).send();
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onRequestInterceptionError(Request request, Response response, Exception e) {

        return false;
    }
}
