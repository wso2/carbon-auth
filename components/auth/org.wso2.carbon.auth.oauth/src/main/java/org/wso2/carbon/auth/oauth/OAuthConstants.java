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

/**
 *  OAuth2 related constants
 */
public class OAuthConstants {
    public static final String RESPONSE_TYPE_CODE = "code";

    // Headers
    public static final String LOCATION_HEADER = "Location";

    // Query parameters
    public static final String ERROR_QUERY_PARAM = "error";
    public static final String CLIENT_ID_QUERY_PARAM = "client_id";
    public static final String CLIENT_SECRET_QUERY_PARAM = "client_secret";
    public static final String REDIRECT_URI_QUERY_PARAM = "redirect_uri";
    public static final String RESPONSE_TYPE_QUERY_PARAM = "response_type";
    public static final String SCOPE_QUERY_PARAM = "scope";
    public static final String STATE_QUERY_PARAM = "state";
    public static final String CODE_QUERY_PARAM = "code";
    public static final String GRANT_TYPE_QUERY_PARAM = "grant_type";
    public static final String REFRESH_TOKEN_QUERY_PARAM = "refresh_token";
    public static final String ACCESS_TOKEN_QUERY_PARAM = "access_token";
    public static final String EXPERIES_IN_QUERY_PARAM = "expires_in";
    public static final String VALIDITY_PERIOD_QUERY_PARAM = "validity_period";
    public static final String TOKEN_TYPE_QUERY_PARAM = "token_type";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    // Auth Types
    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String AUTH_TYPE_BEARER = "Bearer";

    public static final String SCOPE_DEFAULT = "default";

    public static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED";
    public static final String BAD_REQUEST = "BAD_REQUEST";

    //internal constants
    public static final String CLIENT_ID = "clientId";
    public static final String AUTH_USER = "AUTH_USER";
    public static final String APPLICATION_OWNER = "APPLICATION_OWNER";
    public static final String GRANT_TYPE = "GRANT_TYPE";
    public static final String VALIDITY_PERIOD = "validity_period";
    public static final String SCOPES = "scopes";
}
