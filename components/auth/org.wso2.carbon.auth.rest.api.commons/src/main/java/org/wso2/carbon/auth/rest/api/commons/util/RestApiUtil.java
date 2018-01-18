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
import org.wso2.carbon.auth.core.exception.ExceptionCodeHandler;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.dto.ErrorDTO;
import org.wso2.msf4j.Request;

import java.util.HashMap;
import java.util.Map;

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
}
