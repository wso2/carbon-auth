/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.user.store.constant;

/**
 * Names of the database table columns.
 */
public class DatabaseColumnNames {

    /**
     * Names of the Group table columns.
     */
    public static final class Group {
        public static final String ID = "ID";
        public static final String GROUP_UNIQUE_ID = "GROUP_UNIQUE_ID";
        public static final String GROUP_NAME = "GROUP_NAME";
    }

    /**
     * Names of the User table columns.
     */
    public static final class User {
        public static final String ID = "ID";
        public static final String USERNAME = "USERNAME";
        public static final String PASSWORD = "PASSWORD";
        public static final String USER_UNIQUE_ID = "USER_UNIQUE_ID";
        public static final String IDENTITY_STORE_ID = "IDENTITY_STORE_ID";
        public static final String CREDENTIAL_STORE_ID = "CREDENTIAL_STORE_ID";
    }

    /**
     * Names of the ATTRIBUTE table columns.
     */
    public static final class ATTRIBUTE {
        public static final String NAME = "ATTR_NAME";
        public static final String URI = "ATTR_URI";
        public static final String DISPLAY_NAME = "DISPLAY_NAME";
        public static final String REQUIRED = "REQUIRED";
        public static final String REGEX = "REGEX";
        public static final String UNIQUE = "UNIQUE";
    }

    /**
     * Names of the Group table columns.
     */
    public static final class Role {

        public static final String ROLE_UNIQUE_ID = "ROLE_UNIQUE_ID";
        public static final String ROLE_NAME = "ROLE_NAME";
    }

    /**
     * Names of the UserAttributes table columns.
     */
    public static final class UserAttributes {
        public static final String ATTR_URI = "ATTR_URI";
        public static final String ATTR_VALUE = "ATTR_VALUE";
    }

    /**
     * Names of the GroupAttributes table columns.
     */
    public static final class GroupAttributes {
        public static final String ATTR_URI = "ATTR_URI";
        public static final String ATTR_VALUE = "ATTR_VALUE";
    }

    /**
     * Names of the UserGroup table columns.
     */
    public static final class UserGroup {
        public static final String USER_ID = "USER_ID";
        public static final String GROUP_ID = "GROUP_ID";
    }

    /**
     * Names of the PasswordInfo table columns.
     */
    public static final class PasswordInfo {
        public static final String HASH_ALGO = "HASH_ALGO";
        public static final String PASSWORD_SALT = "PASSWORD_SALT";
        public static final String ITERATION_COUNT = "ITERATION_COUNT";
        public static final String KEY_LENGTH = "KEY_LENGTH";
    }

    /**
     * Names of the PasswordInfo table columns.
     */
    public static final class Password {
        public static final String PASSWORD = "PASSWORD";
    }

    /**
     * Names of the Permission table columns.
     */
    public static final class Permission {
        public static final String ID = "ID";
        public static final String RESOURCE_ID = "DOMAIN";
        public static final String ACTION = "ACTION_NAMESPACE";
        public static final String PERMISSION_ID = "PERMISSION_UNIQUE_ID";
    }

    /**
     * Names of the Resource Namespace table columns.
     */
    public static final class ResourceNamespace {
        public static final String ID = "ID";
        public static final String NAMESPACE = "NAMESPACE";
    }

    /**
     * Names of the Resource table columns.
     */
    public static final class Resource {
        public static final String ID = "ID";
        public static final String NAMESPACE_ID = "NAMESPACE_ID";
        public static final String RESOURCE_NAME = "RESOURCE_NAME";
        public static final String USER_UNIQUE_ID = "USER_UNIQUE_ID";
        public static final String IDENTITY_STORE_ID = "IDENTITY_STORE_ID";
    }

    /**
     * Names of the Action table columns.
     */
    public static final class Action {
        public static final String ID = "ID";
        public static final String NAMESPACE_ID = "NAMESPACE_ID";
        public static final String ACTION_NAME = "ACTION_NAME";
    }


    /**
     * Names of the UserRole table columns.
     */
    public static final class UserRole {

        public static final String ROLE_ID = "ROLE_ID";
        public static final String USER_UNIQUE_ID = "USER_UNIQUE_ID";
        public static final String IDENTITY_STORE_ID = "IDENTITY_STORE_ID";
    }

    /**
     * Names of the GroupRole table columns.
     */
    public static final class GroupRole {

        public static final String ROLE_ID = "ROLE_ID";
        public static final String GROUP_UNIQUE_ID = "GROUP_UNIQUE_ID";
        public static final String IDENTITY_STORE_ID = "IDENTITY_STORE_ID";
    }

    /**
     * Names that defined in multi joins.
     */
    public static final class JoinNames {

        public static final String RESOURCE_NAMESPACE = "RESOURCE_NAMESPACE";
        public static final String ACTION_NAMESPACE = "ACTION_NAMESPACE";
    }
}
