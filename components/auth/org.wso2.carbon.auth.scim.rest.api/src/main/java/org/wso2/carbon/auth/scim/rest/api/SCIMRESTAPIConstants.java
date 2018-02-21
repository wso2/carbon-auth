/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.scim.rest.api;

/**
 * Constants used for SCIM REST API
 * 
 */
public class SCIMRESTAPIConstants {
    public static final String USERS_URL = "/api/identity/scim2/v1.0/Users";
    public static final String GROUPS_URL = "/api/identity/scim2/v1.0/Groups";

    public static final String DOCUMENTATION_URL = "";
    public static final int MAX_OPERATIONS = 1000;
    public static final int MAX_PAYLOAD_SIZE = 1048576;
    public static final int MAX_RESULTS = 200;
    public static final int COUNT_FOR_PAGINATION = 200;

    public static final String AUTHENTICATION_SCHEMES_NAME_1 = "HTTP Basic";
    public static final String AUTHENTICATION_SCHEMES_DESCRIPTION_1 = "Authentication scheme using the HTTP Basic " +
            "Standard";
    public static final String AUTHENTICATION_SCHEMES_SPEC_URI_1 = "http://www.rfc-editor.org/info/rfc2617";
    public static final String AUTHENTICATION_SCHEMES_DOCUMENTATION_URL_1 = "http://example.com/help/httpBasic.html";
    public static final String AUTHENTICATION_SCHEMES_TYPE_1 = "httpbasic";
    public static final Boolean AUTHENTICATION_SCHEMES_PRIMARY_1 = true;
    
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_AUTHORIZATION_BASIC = "Basic";
    public static final String CHARSET_UTF8 = "UTF-8";
    
    public static final String ERROR_SCIM_INITIALISATION = "Error in initializing the CarbonAuthSCIMUserManager";
}
