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

package org.wso2.carbon.auth.user.store.constant;

/**
 * User store related constants
 *
 */
public class UserStoreConstants {
    
    public static final String JDBC_CONNECTOR_TYPE = "JDBC";
    public static final String LDAP_CONNECTOR_TYPE = "LDAP";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_SALT = "password_salt";
    public static final String HASH_ALGO = "hash_algo";
    public static final String ITERATION_COUNT = "iteration_count";
    public static final String KEY_LENGTH = "key_length";
    public static final String CLAIM_USERNAME = "urn:ietf:params:scim:schemas:core:2.0:User:userName";
    public static final String CLAIM_ID = "urn:ietf:params:scim:schemas:core:2.0:id";
    public static final String USER_DISPLAY_NAME = "urn:ietf:params:scim:schemas:core:2.0:User:displayName";
    public static final String GROUP_DISPLAY_NAME = "urn:ietf:params:scim:schemas:core:2.0:Group:displayName";
    public static final String NICKNAME = "nickName";
    public static final String PASSWORD_URI = "password";
    public static final String DATASOURCE_WSO2UM_DB = "WSO2_UM_DB";

    public static final String LDAP_MEMBER_ATTRIBUTE = "member";
    public static final String LDAP_EXTENSIBLEOBJECT_ATTRIBUTE = "extensibleObject";

    public static final String OPERATION_NOT_SUPPORTED_IN_LDAP = "Operation not supported in LDAP Connector";

}
