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
 * Connector related constants.
 */
public class JDBCConnectorConstants {

    public static final String DATA_SOURCE = "dataSource";
    public static final String DATABASE_CLASS_NAME = "databaseClassName";
    public static final String MAX_ROW_LIMIT = "maxRowLimit";

    //Credential store connector properties
    public static final String HASH_ALGO = "hashAlgorithm";
    public static final String ITERATION_COUNT = "iterationCount";
    public static final String KEY_LENGTH = "keyLength";


    /**
     * Placeholders related to the named prepared statement.
     */
    public static final class SQLPlaceholders {

        public static final String ATTRIBUTE_NAME = "attr_name";
        public static final String ATTRIBUTE_VALUE = "attr_value";
        public static final String ATTRIBUTE_VALUES = "attr_values";
        public static final String ATTRIBUTE_URI = "attr_uri";
        public static final String DISPLAY_NAME = "display_name";
        public static final String REQUIRED = "required";
        public static final String IS_UNIQUE = "is_unique";
        public static final String REGEX = "regex";
        public static final String USER_ID = "user_id";
        public static final String GROUP_ID = "group_id";
        public static final String LENGTH = "length";
        public static final String OFFSET = "offset";
        public static final String ATTRIBUTE_NAMES = "attr_names";
        public static final String USER_UNIQUE_ID = "user_unique_id";
        public static final String GROUP_UNIQUE_ID = "group_unique_id";
        public static final String PASSWORD_SALT = "password_salt";
        public static final String HASH_ALGO = "hash_algo";
        public static final String ITERATION_COUNT = "iteration_count";
        public static final String KEY_LENGTH = "key_length";
        public static final String PASSWORD = "password";
        public static final String ROLE_NAME = "role_name";
        public static final String ROLE_ID = "role_id";
        public static final String PERMISSION_ID = "permission_id";
        public static final String ACTION_NAMESPACE = "action_namespace";
        public static final String USERNAME = "username";
        public static final String GROUP_NAME = "group_name";
        public static final String IDENTITY_STORE_ID = "identity_store_id";
        public static final String HASHED_PASSWORD = "hashed_password";
        public static final String ROLE_UNIQUE_ID = "role_unique_id";
        public static final String RESOURCE_NAME = "resource_name";
        public static final String ACTION_NAME = "action_name";
        public static final String RESOURCE_NAMESPACE = "resource_namespace";
        public static final String RESOURCE_ID = "resource_id";
        public static final String ACTION_ID = "action_id";
        public static final String NAMESPACE = "namespace";
        public static final String DESCRIPTION = "description";
        public static final String NAMESPACE_ID = "namespace_id";
        public static final String USER_UNIQUE_ID_UPDATE = "user_unique_id_update";
        public static final String GROUP_UNIQUE_ID_UPDATE = "group_unique_id_update";
    }

    /**
     * Query type related constants.
     */
    public static final class QueryTypes {

        public static final String SQL_QUERY_GET_GROUP_FROM_ATTRIBUTE = "sql_query_get_group_from_attribute";
        public static final String SQL_QUERY_GET_PASSWORD_DATA = "sql_query_get_password_data";
        public static final String SQL_QUERY_GET_USER_FROM_ATTRIBUTE = "sql_query_get_user_from_attribute";
        public static final String SQL_QUERY_GET_USERS_FROM_ATTRIBUTES = "sql_query_get_users_from_attributes";
        public static final String SQL_QUERY_GET_USER_ATTRIBUTES = "sql_query_get_user_attributes";
        public static final String SQL_QUERY_LIST_USERS_BY_ATTRIBUTE_PATTERN =
                "sql_query_list_users_by_attribute_pattern";
        public static final String SQL_QUERY_LIST_USER_IDS_BY_ATTRIBUTE = "sql_query_list_user_ids_by_attribute";
        public static final String SQL_QUERY_LIST_USER_IDS = "sql_query_list_user_ids";
        public static final String SQL_QUERY_GET_USER_ATTRIBUTES_FROM_NAME = "sql_query_get_user_attributes_from_uri";
        public static final String SQL_QUERY_IS_USER_IN_GROUP = "sql_query_is_user_in_group";
        public static final String SQL_QUERY_LIST_GROUP_BY_ATTRIBUTE_PATTERN =
                "sql_query_list_group_by_attribute_pattern";
        public static final String SQL_QUERY_LIST_GROUPS_BY_ATTRIBUTE = "sql_query_list_group_by_attribute";
        public static final String SQL_QUERY_LIST_GROUPS = "sql_query_list_group";
        public static final String SQL_QUERY_GET_GROUP_ATTRIBUTES = "sql_query_get_group_attributes";
//        public static final String SQL_QUERY_GET_GROUP_ATTRIBUTES_FROM_NAME =
//                "sql_query_get_group_attributes_from_name";
        public static final String SQL_QUERY_COUNT_USERS = "sql_query_count_users";
        public static final String SQL_QUERY_COUNT_GROUPS = "sql_query_count_groups";
        public static final String SQL_QUERY_ADD_USER_ATTRIBUTES = "sql_query_add_user_attributes";

        public static final String SQL_QUERY_ADD_GROUP_ATTRIBUTES = "sql_query_add_group_attributes";
        public static final String SQL_QUERY_ADD_USER = "sql_query_add_user";
        public static final String SQL_QUERY_ADD_GROUP = "sql_query_add_group";
        public static final String SQL_QUERY_ADD_USER_GROUP = "sql_query_add_user_group";
        public static final String SQL_QUERY_REMOVE_ALL_GROUPS_OF_USER = "sql_query_remove_all_groups_of_user";
        public static final String SQL_QUERY_REMOVE_ALL_USERS_OF_GROUP = "sql_query_remove_all_users_of_group";
        public static final String SQL_QUERY_REMOVE_GROUP_OF_USER = "sql_query_remove_group_of_user";
        public static final String SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_USER = "sql_query_remove_all_attributes_of_user";
        public static final String SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_GROUP =
                "sql_query_remove_all_attributes_of_group";
        public static final String SQL_QUERY_REMOVE_ATTRIBUTE_OF_USER = "sql_query_remove_attribute_of_user";
        public static final String SQL_QUERY_REMOVE_ATTRIBUTE_OF_GROUP = "sql_query_remove_attribute_of_group";
        public static final String SQL_QUERY_DELETE_USER = "sql_query_delete_user";
        public static final String SQL_QUERY_DELETE_GROUP = "sql_query_delete_group";
        public static final String SQL_QUERY_UPDATE_USER_ATTRIBUTES = "sql_query_update_user_claims";
        public static final String SQL_QUERY_UPDATE_GROUP_ATTRIBUTES = "sql_query_update_group_claims";
        public static final String SQL_QUERY_ADD_PASSWORD_INFO = "sql_query_add_password_info";
        public static final String SQL_QUERY_ADD_CREDENTIAL = "sql_query_add_credential";
        public static final String SQL_QUERY_UPDATE_CREDENTIAL = "sql_query_update_credential";
        public static final String SQL_QUERY_UPDATE_PASSWORD_INFO = "sql_query_update_password_info";
        public static final String SQL_QUERY_DELETE_CREDENTIAL = "sql_query_delete_credential";
        public static final String SQL_QUERY_GET_ATTR_BY_URI = "sql_query_get_attr_by_uri";
        public static final String SQL_QUERY_ADD_ATTR = "sql_query_add_attr";
        public static final String SQL_QUERY_LIST_USER_IDS_OF_GROUP = "sql_query_get_users_of_group";
        public static final String SQL_QUERY_LIST_GROUP_IDS_OF_USER = "sql_query_get_groups_of_user";

        public static final String SQL_QUERY_GET_ROLE = "sql_query_get_role";
        public static final String SQL_QUERY_GET_ROLES_FOR_USER = "sql_query_get_roles_for_user";
        public static final String SQL_QUERY_GET_PERMISSIONS_FROM_RESOURCE_FOR_ROLE =
                "sql_query_get_permissions_from_resource_for_role";
        public static final String SQL_QUERY_GET_ROLES_FOR_GROUP = "sql_query_get_roles_for_group";
        public static final String SQL_QUERY_ADD_PERMISSION = "sql_query_add_permission";
        public static final String SQL_QUERY_ADD_ROLE = "sql_query_add_role";
        public static final String SQL_QUERY_ADD_PERMISSIONS_TO_ROLE = "sql_query_add_role_permission";
        public static final String SQL_QUERY_ADD_PERMISSIONS_TO_ROLE_BY_UNIQUE_ID =
                "sql_query_add_permissions_to_role_by_unique_id";
        public static final String SQL_QUERY_DELETE_ROLE = "sql_query_delete_role";
        public static final String SQL_QUERY_DELETE_PERMISSION = "sql_query_delete_permission";
        public static final String SQL_QUERY_IS_USER_IN_ROLE = "sql_query_is_user_in_role";
        public static final String SQL_QUERY_IS_GROUP_IN_ROLE = "sql_query_is_group_in_role";
        public static final String SQL_QUERY_DELETE_ROLES_FROM_USER = "sql_query_delete_roles_of_user";
        public static final String SQL_QUERY_ADD_ROLES_TO_USER = "sql_query_add_roles_to_user";
        public static final String SQL_QUERY_ADD_ROLES_TO_GROUP = "sql_query_add_roles_to_group";
        public static final String SQL_QUERY_DELETE_ROLES_FROM_GROUP = "sql_query_delete_roles_from_group";
        public static final String SQL_QUERY_DELETE_GROUPS_FROM_ROLE = "sql_query_delete_groups_from_role";
        public static final String SQL_QUERY_DELETE_USERS_FROM_ROLE = "sql_query_delete_users_of_role";
        public static final String SQL_QUERY_DELETE_PERMISSIONS_FROM_ROLE = "sql_query_delete_permissions_from_role";
        public static final String SQL_QUERY_DELETE_GIVEN_ROLES_FROM_USER = "sql_query_delete_given_roles_from_user";
        public static final String SQL_QUERY_DELETE_GIVEN_ROLES_FROM_GROUP = "sql_query_delete_given_roles_from_group";
        public static final String SQL_QUERY_DELETE_GIVEN_PERMISSIONS_FROM_ROLE =
                "sql_query_delete_given_permissions_from_role";
        public static final String SQL_QUERY_GET_PERMISSION = "sql_query_get_permission";
        public static final String SQL_QUERY_ADD_RESOURCE = "sql_query_add_resource_if_not_exist";
        public static final String SQL_QUERY_ADD_ACTION = "sql_query_add_action_if_not_exist";
        public static final String SQL_QUERY_GET_RESOURCE_ID = "sql_query_get_resource_id";
        public static final String SQL_QUERY_GET_ACTION_ID = "sql_query_get_action_id";
        public static final String SQL_QUERY_GET_PERMISSIONS_FROM_ACTION_FOR_ROLE =
                "sql_query_get_permissions_from_action_for_role";
        public static final String SQL_QUERY_GET_NAMESPACE_ID = "sql_query_get_namespace_id";
        public static final String SQL_QUERY_ADD_NAMESPACE = "sql_query_add_namespace";
        public static final String SQL_QUERY_COUNT_ROLES = "sql_query_count_roles";
        public static final String SQL_QUERY_COUNT_PERMISSIONS = "sql_query_count_permissions";
        public static final String SQL_QUERY_LIST_ROLES = "sql_query_list_roles";
        public static final String SQL_QUERY_GET_RESOURCES = "sql_query_get_resources";
        public static final String SQL_QUERY_GET_ACTIONS = "sql_query_get_actions";
        public static final String SQL_QUERY_LIST_PERMISSIONS = "sql_query_list_permissions";
        public static final String SQL_QUERY_DELETE_RESOURCE = "sql_query_delete_resource";
        public static final String SQL_QUERY_DELETE_ACTION = "sql_query_delete_action";

    }
}
