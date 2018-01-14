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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.dto.ErrorDTO;
import org.wso2.msf4j.Request;

/**
 * Utility class for all REST APIs.
 */
public class RestApiUtil {

    private static final Logger log = LoggerFactory.getLogger(RestApiUtil.class);
    private static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

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
        errorDTO.setCode(500);
        errorDTO.setMessage(RestApiConstants.STATUS_INTERNAL_SERVER_ERROR_MESSAGE_DEFAULT);
        errorDTO.setDescription(RestApiConstants.STATUS_INTERNAL_SERVER_ERROR_DESCRIPTION_DEFAULT);
        return errorDTO;
    }

    /**
     * Returns a generic errorDTO
     *
     * @param message     specifies the error message
     * @param code        error code.
     * @param description error description.
     * @return A generic errorDTO with the specified details
     */
    public static ErrorDTO getErrorDTO(String message, Integer code, String description) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(code);
        errorDTO.setMessage(message);
        errorDTO.setDescription(description);
        return errorDTO;
    }
}
