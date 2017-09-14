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

package org.wso2.carbon.auth.oauth;

public class OAuthConstants {
    public static final String RESPONSE_TYPE_CODE = "code";

    // Headers
    public static final String LOCATION_HEADER = "Location";

    // Query parameters
    public static final String ERROR_QUERY_PARAM = "error";
    public static final String ERROR_DESCRIPTION_QUERY_PARAM = "error_description";
    public static final String STATE_QUERY_PARAM = "state";
    public static final String CODE_QUERY_PARAM = "code";

    // Auth Types
    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String AUTH_TYPE_BEARER = "Bearer";


    // Error conditions
    public enum ErrorCondition {
        ERROR_INVALID_REQUEST("invalid_request"),
        ERROR_UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
        ERROR_INVALID_SCOPE("invalid_scope");

        private String condition;

        ErrorCondition(String condition) {
            this.condition = condition;
        }

        public String getCondition() {
            return condition;
        }
    }
}
