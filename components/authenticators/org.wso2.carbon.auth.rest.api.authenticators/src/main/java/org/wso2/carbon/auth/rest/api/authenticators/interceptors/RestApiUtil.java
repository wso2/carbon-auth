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

import io.swagger.models.Path;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.SecurityConfigurationService;
import org.wso2.carbon.auth.rest.api.authenticators.dto.ErrorDTO;
import org.wso2.carbon.auth.rest.api.authenticators.dto.RestAPIInfo;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.ErrorHandler;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.ExceptionCodes;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.carbon.auth.rest.api.authenticators.internal.ServiceReferenceHolder;
import org.wso2.msf4j.Request;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Utility class for all REST APIs.
 */
public class RestApiUtil {

    public static final String WEB_PROTOCOL_SUFFIX = "://";
    public static final String ALLOW_ALL_ORIGINS = "*";

    /**
     * Returns an Internal Server Error DTO
     *
     * @return an Internal Server Error DTO
     */
    public static ErrorDTO getInternalServerErrorDTO() {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(ExceptionCodes.INTERNAL_ERROR.getErrorCode());
        errorDTO.setMessage(ExceptionCodes.INTERNAL_ERROR.getErrorMessage());
        errorDTO.setDescription(ExceptionCodes.INTERNAL_ERROR.getErrorDescription());
        return errorDTO;
    }

    /**
     * Returns an errorDTO based on a provided ExceptionCodeHandler
     *
     * @param errorHandler with mapped HTTP error details
     * @return An errorDTO with the specified details
     */
    public static ErrorDTO getErrorDTO(ErrorHandler errorHandler) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(errorHandler.getErrorCode());
        errorDTO.setMessage(errorHandler.getErrorMessage());
        errorDTO.setDescription(errorHandler.getErrorDescription());
        return errorDTO;
    }

    public static String getAllowedOrigin(String origin) {

        if (origin == null) {
            return null;
        }

        String host = origin.split(WEB_PROTOCOL_SUFFIX)[1];
        List<String> allowedOrigins = SecurityConfigurationService.getInstance().getSecurityConfiguration()
                .getAllowedHosts();
        if (allowedOrigins.contains(ALLOW_ALL_ORIGINS) || allowedOrigins.contains(host)) {
            return origin;
        }

        //origin is not within the allowed origin list
        return null;
    }

    /**
     * Get defined HTTP methods in the swagger definition as a comma separated string
     *
     * @param request Request
     * @param method  Method information for the request
     * @return Http Methods as a comma separated string
     * @throws RestAPIAuthSecurityException if failed to get defined http methods
     */
    public static String getDefinedMethodHeadersInSwaggerContent(
            Request request, Method method) throws RestAPIAuthSecurityException {

        RestAPIInfo electedSwagger = getElectedRestApiInfo(request);
        if (electedSwagger != null) {
            Path swaggerAPIPath = electedSwagger.getSwagger().getPath(getApiPath(method));
            if (swaggerAPIPath == null) {
                throw new RestAPIAuthSecurityException("Could not read API path from the swagger definition");
            }
            return swaggerAPIPath.getOperationMap().keySet().stream().map(Enum::toString)
                    .collect(Collectors.joining(", "));
        } else {
            throw new RestAPIAuthSecurityException("Couldn't find the SwaggerDefinition for API path", ExceptionCodes
                    .INTERNAL_ERROR);
        }
    }

    /**
     * Get defined HTTP methods in the swagger definition as a comma separated string
     *
     * @param request Request
     * @return Http Methods as a comma separated string
     */
    public static RestAPIInfo getElectedRestApiInfo(Request request) {

        if (request.getProperty(RestAPIConstants.ELECTED_BASE_PATH) != null) {
            return ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap().get(request.getProperty
                    (RestAPIConstants.ELECTED_BASE_PATH));
        }
        String requestURI = request.getUri().toLowerCase(Locale.ENGLISH);
        RestAPIInfo electedSwagger = null;
        for (String basePath : ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap().keySet()) {
            if (requestURI.contains(basePath)) {
                electedSwagger = ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap().get(basePath);
                break;
            }
        }
        if (electedSwagger != null) {
            request.setProperty(RestAPIConstants.ELECTED_BASE_PATH, electedSwagger.getBasePath());
        }
        return electedSwagger;
    }

    public static String getApiPath(Method method) {

        String apiPath = method.getDeclaringClass().getAnnotation(javax.ws.rs.ApplicationPath.class).value();
        javax.ws.rs.Path apiPathAnnotation = method.getAnnotation(javax.ws.rs.Path.class);
        if (apiPathAnnotation != null) {
            apiPath += apiPathAnnotation.value();
        }
        return apiPath;
    }
}
