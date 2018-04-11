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

package org.wso2.carbon.auth.core.exception;

/**
 * This enum class holds error codes that we need to pass to upper level. 
 * You have to define your custom error codes here.
 */
public enum ExceptionCodes implements ExceptionCodeHandler {
    
    INTERNAL_ERROR(900300, "General Error", 500, "Server Error Occurred"),
    DAO_EXCEPTION(900301, "Internal server error", 500, " Error occurred while persisting/retrieving data"),
    DATA_NOT_FOUND(900302, "Data not found", 404, "Data not found"),

    //scopes related exception codes
    SCOPE_ALREADY_EXISTS(900400, "Resource already exists", 409, "A scope already exists with same name"),
    SCOPE_NOT_FOUND(900401, "Not found", 404, "Scope not found"),

    //oauth2 grants related exception codes
    OAUTH2_GRANT_PROCESS_EXCEPTION(900500, "Internal OAuth2 grant processing error", 500,
            "Error while processing OAuth2 grant request"),
    INVALID_REQUEST(900600, "Invalid request", 400, "Invalid request"),
    UNSUPPORTED_SCOPE(900601, "Unsupported scope", 400, "Unsupported scope"),
    INVALID_TOKEN(900602, "Invalid token", 401, "The Access Token expired");

    private final long errorCode;
    private final String errorMessage;
    private final int httpStatusCode;
    private final String errorDescription;

    /**
     * @param errorCode        This is unique error code that pass to upper level.
     * @param msg              The error message that you need to pass along with the error code.
     * @param httpErrorCode    This HTTP status code which should return from REST API layer. If you don't want to pass
     *                         a http status code keep it blank.
     * @param errorDescription The error description.
     */
    ExceptionCodes(long errorCode, String msg, int httpErrorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorMessage = msg;
        this.httpStatusCode = httpErrorCode;
        this.errorDescription = errorDescription;
    }

    @Override
    public long getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    @Override
    public String getErrorDescription() {
        return this.errorDescription;
    }
}
