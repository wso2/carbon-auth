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

package org.wso2.carbon.auth.client.registration;

/**
 * This class holds the constants used by DynamicClientRegistration component.
 */
public class Constants {

    public static final String CALLBACK_URL_REGEXP_PREFIX = "regexp=";
    public static final String DEFAULT_TOKEN_TYPE = "Default";
    public static final String JWT_TOKEN_TYPE = "JWT";

    /**
     * Constants related to grant type
     */
    public static class GrantTypes {
        public static final String IMPLICIT = "implicit";
        public static final String AUTHORIZATION_CODE = "authorization_code";
    }
}
