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
package org.wso2.carbon.auth.rest.api.commons;

/**
 * Represents constants related to REST APIs.
 */
public class RestApiConstants {
    public static final String HTTP_OPTIONS = "OPTIONS";

    public static final Integer PAGINATION_LIMIT_DEFAULT = 25;
    public static final Integer PAGINATION_OFFSET_DEFAULT = 0;
    public static final String PAGINATION_NEXT_OFFSET = "next_offset";
    public static final String PAGINATION_NEXT_LIMIT = "next_limit";
    public static final String PAGINATION_PREVIOUS_OFFSET = "previous_offset";
    public static final String PAGINATION_PREVIOUS_LIMIT = "previous_limit";
    public static final String LIMIT_PARAM = "{limit}";
    public static final String OFFSET_PARAM = "{offset}";


    public static final String APPLICATION_JSON = "application/json";
    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = APPLICATION_JSON;
    public static final String HEADER_CONTENT_TYPE = "Content-Type";


    public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
    public static final String AUTH_TYPE_OAUTH2 = "OAuth2";
    public static final String BEARER_PREFIX = "bearer";
    public static final String AUTH_SERVER_URL_KEY = "AUTH_SERVER_URL";
    public static final String SCOPE = "scope";
    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String CHARSET_UTF_8 = "UTF-8";

    public static final String COOKIE_HEADER = "Cookie";
    public static final String ORIGIN_HEADER = "Origin";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_LIST = "Accept, Accept-Encoding, Accept-Language, " +
            "Authorization, Content-Type, Cache-Control, Connection, Cookie, Host, Pragma, " +
            "Referer, User-Agent";

    public static final String WEB_PROTOCOL_SUFFIX = "://";
    public static final String SWAGGER_HOST_ELEMENT = "host";
    public static final String LOCATION_HEADER = "Location";

    public static final String RESOURCE_PATH_SCOPES = "/scopes";
    public static final String SCOPENAME_PARAM = "{scopeName}";
    public static final String RESOURCE_PATH_SCOPE = RESOURCE_PATH_SCOPES + "/" + SCOPENAME_PARAM;

    public static final String SCOPES_GET_PAGINATION_URL =
            RESOURCE_PATH_SCOPES + "?limit=" + LIMIT_PARAM + "&offset=" + OFFSET_PARAM;

    public static final String APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String US_ASCII = "US-ASCII";

    /**
     * Types of Apps in KeyManager
     */
    public static class APPType {
        public static final String DCRM = "client-registration";
        public static final String INTROSPECT = "introspect";
        public static final String OAUTH = "oauth2";
        public static final String SCIM = "scim";
        public static final String SCOPE = "scope";
        public static final String DCRM_SWAGGER_DEFINITION_FILE_PATH = "/client-registration-api.yaml";
        public static final String INTROSPECTION_SWAGGER_DEFINITION_FILE_PATH = "/introspection-api.yaml";
        public static final String OAUTH2_SWAGGER_DEFINITION_FILE_PATH = "/oauth-api.yaml";
        public static final String SCIM_SWAGGER_DEFINITION_FILE_PATH = "/scim-api.yaml";
        public static final String SCOPE_SWAGGER_DEFINITION_FILE_PATH = "/scope-api.yaml";
    }



}
