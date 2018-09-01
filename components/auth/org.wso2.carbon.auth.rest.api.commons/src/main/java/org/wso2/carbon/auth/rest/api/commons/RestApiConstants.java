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

    public static final Integer PAGINATION_LIMIT_DEFAULT = 25;
    public static final Integer PAGINATION_OFFSET_DEFAULT = 0;
    public static final String PAGINATION_NEXT_OFFSET = "next_offset";
    public static final String PAGINATION_NEXT_LIMIT = "next_limit";
    public static final String PAGINATION_PREVIOUS_OFFSET = "previous_offset";
    public static final String PAGINATION_PREVIOUS_LIMIT = "previous_limit";
    public static final String LIMIT_PARAM = "{limit}";
    public static final String OFFSET_PARAM = "{offset}";

    public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
    public static final String SCOPE = "scope";
    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String RESOURCE_PATH_SCOPES = "/scopes";
    public static final String SCOPENAME_PARAM = "{scopeName}";
    public static final String RESOURCE_PATH_SCOPE = RESOURCE_PATH_SCOPES + "/" + SCOPENAME_PARAM;

    public static final String SCOPES_GET_PAGINATION_URL =
            RESOURCE_PATH_SCOPES + "?limit=" + LIMIT_PARAM + "&offset=" + OFFSET_PARAM;

    /**
     * Types of Apps in KeyManager
     */
    public static class APPType {

        public static final String SCOPE = "scope";
    }

}
