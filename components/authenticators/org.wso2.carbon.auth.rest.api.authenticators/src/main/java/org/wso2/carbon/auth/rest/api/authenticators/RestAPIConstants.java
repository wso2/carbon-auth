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

package org.wso2.carbon.auth.rest.api.authenticators;

/**
 * Constants for Authenticators
 */
public class RestAPIConstants {

    public static final String HTTP_OPTIONS = "OPTIONS";

    public static final String AUTH_TYPE_OAUTH2 = "OAuth2";
    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String AUTHORIZATION = "Authorization";
    public static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    public static final String LOGGED_IN_PSEUDO_USER = "LOGGED_IN_PSEUDO_USER";
    public static final String ELECTED_BASE_PATH = "ELECTED_BASE_PATH";
    public static final String ORIGIN_HEADER = "Origin";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_LIST = "Accept, Accept-Encoding, Accept-Language, " +
            "Authorization, Content-Type, Cache-Control, Connection, Cookie, Host, Pragma, " +
            "Referer, User-Agent";
}
