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
package org.wso2.carbon.auth.rest.api.commons.util;

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.configuration.AuthConfigurationService;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.core.exception.ExceptionCodeHandler;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.dto.ErrorDTO;
import org.wso2.msf4j.Request;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for all REST APIs.
 */
public class RestApiUtil {

    private static final Logger log = LoggerFactory.getLogger(RestApiUtil.class);
    private static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static Map<String, Swagger> swaggerRestAPIDefinitions = new HashMap<>();
    public static final String WEB_PROTOCOL_SUFFIX = "://";
    public static final String ALLOW_ALL_ORIGINS = "*";


    static {
        try {
            swaggerRestAPIDefinitions.put(RestApiConstants.APPType.DCRM, new SwaggerParser()
                    .parse(getSwaggerDefinition(RestApiConstants.APPType.DCRM_SWAGGER_DEFINITION_FILE_PATH)));
            swaggerRestAPIDefinitions.put(RestApiConstants.APPType.INTROSPECT, new SwaggerParser()
                    .parse(getSwaggerDefinition(RestApiConstants.APPType.INTROSPECTION_SWAGGER_DEFINITION_FILE_PATH)));
            swaggerRestAPIDefinitions.put(RestApiConstants.APPType.OAUTH, new SwaggerParser()
                    .parse(getSwaggerDefinition(RestApiConstants.APPType.OAUTH2_SWAGGER_DEFINITION_FILE_PATH)));
            swaggerRestAPIDefinitions.put(RestApiConstants.APPType.SCIM, new SwaggerParser()
                    .parse(getSwaggerDefinition(RestApiConstants.APPType.SCIM_SWAGGER_DEFINITION_FILE_PATH)));
            swaggerRestAPIDefinitions.put(RestApiConstants.APPType.SCOPE, new SwaggerParser()
                    .parse(getSwaggerDefinition(RestApiConstants.APPType.SCOPE_SWAGGER_DEFINITION_FILE_PATH)));
        } catch (AuthException e) {
            log.error("Error while parsing the swagger definition to " + Swagger.class.getName(), e);
        }
    }

    /**
     * Get the current logged in user's username
     *
     * @param request msf4j request
     * @return The current logged in user's username or null if user is not logged in.
     */
    public static String getLoggedInUsername(Request request) {
        return request.getProperty(LOGGED_IN_USER) != null ? request.getProperty(LOGGED_IN_USER).toString() : null;
    }

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
     * @param exceptionCodeHandler with mapped HTTP error details
     * @return An errorDTO with the specified details
     */
    public static ErrorDTO getErrorDTO(ExceptionCodeHandler exceptionCodeHandler) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(exceptionCodeHandler.getErrorCode());
        errorDTO.setMessage(exceptionCodeHandler.getErrorMessage());
        errorDTO.setDescription(exceptionCodeHandler.getErrorDescription());
        return errorDTO;
    }

    /**
     * Returns the next/previous offset/limit parameters properly when current offset,
     * limit and size parameters are specified
     *
     * @param offset current starting index
     * @param limit  current max records
     * @param total  maximum index possible
     * @return the next/previous offset/limit parameters as a hash-map
     */
    public static Map<String, Integer> getPaginationParams(Integer offset, Integer limit, Integer total) {
        Map<String, Integer> result = new HashMap<>();
        if (offset >= total || offset < 0) {
            return result;
        }

        int start = offset;
        int end = offset + limit - 1;

        int nextStart = end + 1;
        if (nextStart < total) {
            result.put(RestApiConstants.PAGINATION_NEXT_OFFSET, nextStart);
            result.put(RestApiConstants.PAGINATION_NEXT_LIMIT, limit);
        }

        int previousEnd = start - 1;
        int previousStart = previousEnd - limit + 1;

        if (previousEnd >= 0) {
            if (previousStart < 0) {
                result.put(RestApiConstants.PAGINATION_PREVIOUS_OFFSET, 0);
                result.put(RestApiConstants.PAGINATION_PREVIOUS_LIMIT, limit);
            } else {
                result.put(RestApiConstants.PAGINATION_PREVIOUS_OFFSET, previousStart);
                result.put(RestApiConstants.PAGINATION_PREVIOUS_LIMIT, limit);
            }
        }
        return result;
    }

    private static String getSwaggerDefinition(String filePath) throws AuthException {

        try {
            return IOUtils
                    .toString(RestApiUtil.class.getResourceAsStream(filePath), "UTF-8");
        } catch (IOException e) {
            String message = "Error while reading the swagger definition of Rest API :" + filePath;
            log.error(message, e);
            throw new AuthException(message, e);
        }

    }

    public static String getAllowedOrigin(String origin) {
        if (origin == null) {
            return null;
        }

        String host = origin.split(WEB_PROTOCOL_SUFFIX)[1];
        List<String> allowedOrigins = AuthConfigurationService.getInstance().getAuthConfiguration()
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
     * @param request           Request
     * @param method Method information for the request
     * @return Http Methods as a comma separated string
     * @throws AuthException if failed to get defined http methods
     */
    public static String getDefinedMethodHeadersInSwaggerContent(
            Request request, Method method) throws AuthException {
        String requestURI = request.getUri().toLowerCase(Locale.ENGLISH);
        Swagger swagger = null;

        if (requestURI.contains("/api/identity/oauth2/introspect")) {
            swagger = swaggerRestAPIDefinitions.get(RestApiConstants.APPType.INTROSPECT);
        } else if (requestURI.contains("/api/identity/oauth2/dcr")) {
            swagger = swaggerRestAPIDefinitions.get(RestApiConstants.APPType.DCRM);
        } else if (requestURI.contains("/api/auth/oauth2")) {
            swagger = swaggerRestAPIDefinitions.get(RestApiConstants.APPType.OAUTH);
        } else if (requestURI.contains("/api/identity/scim2")) {
            swagger = swaggerRestAPIDefinitions.get(RestApiConstants.APPType.SCIM);
        } else if (requestURI.contains("/api/auth/scope-registration")) {
            swagger = swaggerRestAPIDefinitions.get(RestApiConstants.APPType.SCOPE);
        }
        if (swagger == null) {
            throw new AuthException("Error while parsing the swagger definition");
        }

        Path swaggerAPIPath = swagger.getPath(getApiPath(method));
        if (swaggerAPIPath == null) {
            throw new AuthException("Could not read API path from the swagger definition");
        }
        return swaggerAPIPath.getOperationMap().keySet().stream().map(Enum::toString)
                .collect(Collectors.joining(", "));
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
