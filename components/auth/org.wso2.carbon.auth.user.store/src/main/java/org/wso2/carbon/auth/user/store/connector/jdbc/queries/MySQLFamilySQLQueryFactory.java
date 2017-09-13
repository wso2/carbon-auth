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

package org.wso2.carbon.auth.user.store.connector.jdbc.queries;

import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.constant.JDBCConnectorConstants;

import java.util.List;

/**
 * SQL queries for MySQL family based databases.
 *
 * @since 1.0.0
 */
public class MySQLFamilySQLQueryFactory extends SQLQueryFactory {

    private static final String GET_PASSWORD_DATA =
            "SELECT USER_UNIQUE_ID, PASSWORD, PASSWORD_SALT, HASH_ALGO, ITERATION_COUNT, KEY_LENGTH " +
                    "FROM UM_PASSWORD LEFT JOIN UM_PASSWORD_INFO " +
                    "ON UM_PASSWORD.ID = UM_PASSWORD_INFO.USER_ID " +
                    "WHERE USER_UNIQUE_ID = :user_id; ";

    private static final String GET_USER_FROM_ATTRIBUTE =
            "SELECT UM_USER.USER_UNIQUE_ID " +
                    "FROM UM_USER LEFT JOIN UM_USER_ATTRIBUTES " +
                    "ON UM_USER_ATTRIBUTES.USER_ID = UM_USER.ID " +
                    "WHERE UM_USER_ATTRIBUTES.ATTR_ID = " +
                    "(SELECT ID " +
                    "FROM UM_ATTRIBUTES " +
                    "WHERE ATTR_NAME = :attr_name; ) " +
                    "AND UM_USER_ATTRIBUTES.ATTR_VALUE = :attr_value;";

    private static final String GET_GROUP_FROM_ATTRIBUTE =
            "SELECT UM_GROUP.GROUP_UNIQUE_ID " +
                    "FROM UM_GROUP LEFT JOIN UM_GROUP_ATTRIBUTES " +
                    "ON UM_GROUP_ATTRIBUTES.GROUP_ID = UM_GROUP.ID " +
                    "WHERE UM_GROUP_ATTRIBUTES.ATTR_ID = " +
                    "(SELECT ID " +
                    "FROM UM_ATTRIBUTES " +
                    "WHERE ATTR_NAME = :attr_name; ) " +
                    "AND UM_GROUP_ATTRIBUTES.ATTR_VALUE = :attr_value;";

    private static final String GET_USER_ATTRIBUTES =
            "SELECT ATTR_NAME, ATTR_VALUE " +
                    "FROM UM_USER_ATTRIBUTES LEFT JOIN UM_ATTRIBUTES " +
                    "ON UM_USER_ATTRIBUTES.ATTR_ID = UM_ATTRIBUTES.ID " +
                    "WHERE USER_ID = (SELECT ID " +
                    "FROM UM_USER " +
                    "WHERE USER_UNIQUE_ID = :user_id;)";

    private static final String LIST_USERS_BY_ATTRIBUTE_PATTERN =
            "SELECT UM_USER.USER_UNIQUE_ID " +
                    "FROM UM_USER LEFT JOIN UM_USER_ATTRIBUTES " +
                    "ON UM_USER_ATTRIBUTES.USER_ID = UM_USER.ID " +
                    "WHERE UM_USER_ATTRIBUTES.ATTR_ID = " +
                    "(SELECT ID " +
                    "FROM UM_ATTRIBUTES " +
                    "WHERE ATTR_NAME = :attr_name; ) " +
                    "AND UM_USER_ATTRIBUTES.ATTR_VALUE LIKE :attr_value; " +
                    "LIMIT :length; " +
                    "OFFSET :offset;";

    private static final String LIST_USERS_BY_ATTRIBUTE =
            "SELECT UM_USER.USER_UNIQUE_ID " +
                    "FROM UM_USER LEFT JOIN UM_USER_ATTRIBUTES " +
                    "ON UM_USER_ATTRIBUTES.USER_ID = UM_USER.ID " +
                    "WHERE UM_USER_ATTRIBUTES.ATTR_ID = " +
                    "(SELECT ID " +
                    "FROM UM_ATTRIBUTES " +
                    "WHERE ATTR_NAME = :attr_name; ) " +
                    "AND UM_USER_ATTRIBUTES.ATTR_VALUE = :attr_value; " +
                    "LIMIT :length; " +
                    "OFFSET :offset;";

    private static final String GET_USER_ATTRIBUTES_FROM_NAME =
            "SELECT ATTR_NAME, ATTR_VALUE " +
                    "FROM UM_USER_ATTRIBUTES LEFT JOIN UM_ATTRIBUTES " +
                    "ON UM_USER_ATTRIBUTES.ATTR_ID = UM_ATTRIBUTES.ID " +
                    "WHERE USER_ID = (SELECT ID " +
                    "FROM UM_USER " +
                    "WHERE USER_UNIQUE_ID = :user_id;) " +
                    "AND ATTR_NAME IN (:attr_names;)";

    private static final String IS_USER_IN_GROUP =
            "SELECT ID " +
                    "FROM UM_USER_GROUP " +
                    "WHERE USER_ID = (SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_id;) " +
                    "AND GROUP_ID = (SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_id;)";

    private static final String LIST_GROUP_BY_ATTRIBUTE_PATTERN =
            "SELECT UM_GROUP.GROUP_UNIQUE_ID " +
                    "FROM UM_GROUP LEFT JOIN UM_GROUP_ATTRIBUTES " +
                    "ON UM_GROUP_ATTRIBUTES.GROUP_ID = UM_GROUP.ID " +
                    "WHERE UM_GROUP_ATTRIBUTES.ATTR_ID = " +
                    "(SELECT ID " +
                    "FROM UM_ATTRIBUTES " +
                    "WHERE ATTR_NAME = :attr_name; ) " +
                    "AND UM_GROUP_ATTRIBUTES.ATTR_VALUE LIKE :attr_value; " +
                    "LIMIT :length; " +
                    "OFFSET :offset;";

    private static final String LIST_GROUP_BY_ATTRIBUTE =
            "SELECT UM_GROUP.GROUP_UNIQUE_ID " +
                    "FROM UM_GROUP LEFT JOIN UM_GROUP_ATTRIBUTES " +
                    "ON UM_GROUP_ATTRIBUTES.GROUP_ID = UM_GROUP.ID " +
                    "WHERE UM_GROUP_ATTRIBUTES.ATTR_ID = " +
                    "(SELECT ID " +
                    "FROM UM_ATTRIBUTES " +
                    "WHERE ATTR_NAME = :attr_name; ) " +
                    "AND UM_GROUP_ATTRIBUTES.ATTR_VALUE = :attr_value; " +
                    "LIMIT :length; " +
                    "OFFSET :offset;";

    private static final String GET_GROUP_ATTRIBUTES =
            "SELECT ATTR_NAME, ATTR_VALUE " +
                    "FROM UM_GROUP_ATTRIBUTES LEFT JOIN UM_ATTRIBUTES " +
                    "ON UM_GROUP_ATTRIBUTES.ATTR_ID = UM_ATTRIBUTES.ID " +
                    "WHERE GROUP_ID = (SELECT ID " +
                    "FROM UM_GROUP " +
                    "WHERE GROUP_UNIQUE_ID = :group_id;)";

    private static final String GET_GROUP_ATTRIBUTES_FROM_NAME =
            "SELECT ATTR_NAME, ATTR_VALUE " +
                    "FROM UM_GROUP_ATTRIBUTES LEFT JOIN UM_ATTRIBUTES " +
                    "ON UM_GROUP_ATTRIBUTES.ATTR_ID = UM_ATTRIBUTES.ID " +
                    "WHERE GROUP_ID = (SELECT ID " +
                    "FROM UM_GROUP " +
                    "WHERE GROUP_UNIQUE_ID = :group_id;) " +
                    "AND ATTR_NAME IN (:attr_names;)";

    private static final String COUNT_USERS = "SELECT COUNT(*) FROM UM_USER";

    private static final String COUNT_GROUPS = "SELECT COUNT(*) FROM UM_GROUP";

    private static final String ADD_USER_ATTRIBUTES =
            "INSERT INTO UM_USER_ATTRIBUTES (ATTR_ID, ATTR_VALUE, USER_ID) " +
                    "VALUES ((SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = :attr_name;), :attr_value;, " +
                    "(SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;)) ";

    private static final String ADD_USER =
            "INSERT INTO UM_USER (USER_UNIQUE_ID) " +
                    "VALUES (:user_unique_id;)";

    private static final String ADD_GROUP_ATTRIBUTES =
            "INSERT INTO UM_GROUP_ATTRIBUTES (ATTR_ID, ATTR_VALUE, GROUP_ID) " +
                    "VALUES ((SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = :attr_name;), :attr_value;, " +
                    "(SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;)) ";

    private static final String ADD_GROUP =
            "INSERT INTO UM_GROUP (GROUP_UNIQUE_ID) " +
                    "VALUES (:group_unique_id;)";

    private static final String ADD_USER_GROUPS =
            "INSERT INTO UM_USER_GROUP (USER_ID, GROUP_ID) " +
                    "VALUES ((SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;), " +
                    "(SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;))";

    private static final String REMOVE_ALL_GROUPS_OF_USER = "DELETE FROM UM_USER_GROUP " +
            "WHERE USER_ID = (SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;)";

    private static final String REMOVE_ALL_USERS_OF_GROUP = "DELETE FROM UM_USER_GROUP " +
            "WHERE GROUP_ID = (SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;)";

    private static final String REMOVE_GROUP_OF_USER = "DELETE FROM UM_USER_GROUP " +
            "WHERE USER_ID = (SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;) " +
            "AND GROUP_ID = (SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;)";

    private static final String REMOVE_ALL_ATTRIBUTES_OF_USER = "DELETE FROM UM_USER_ATTRIBUTES " +
            "WHERE USER_ID = (SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;)";

    private static final String REMOVE_ALL_ATTRIBUTES_OF_GROUP = "DELETE FROM UM_GROUP_ATTRIBUTES " +
            "WHERE GROUP_ID = (SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;)";

    private static final String REMOVE_ATTRIBUTE_OF_USER =
            "DELETE FROM UM_USER_ATTRIBUTES " +
                    "WHERE ATTR_ID = (SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = :attr_name;) AND " +
                    "USER_ID = (SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;) ";

    private static final String REMOVE_ATTRIBUTE_OF_GROUP =
            "DELETE FROM UM_GROUP_ATTRIBUTES " +
                    "WHERE ATTR_ID = (SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = :attr_name;) AND " +
                    "GROUP_ID = (SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;) ";

    private static final String DELETE_USER =
            "DELETE FROM UM_USER " +
                    "WHERE USER_UNIQUE_ID = :user_unique_id;";

    private static final String DELETE_GROUP =
            "DELETE FROM UM_GROUP " +
                    "WHERE GROUP_UNIQUE_ID = :group_unique_id;";

    private static final String UPDATE_USER_ATTRIBUTES = "UPDATE UM_USER_ATTRIBUTES SET ATTR_VALUE = " +
            ":attr_value; WHERE ATTR_ID = (SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = :attr_name;) AND " +
            "USER_ID = (SELECT ID FROM UM_USER WHERE USER_UNIQUE_ID = :user_unique_id;)";

    private static final String UPDATE_GROUP_ATTRIBUTES = "UPDATE UM_GROUP_ATTRIBUTES SET ATTR_VALUE = " +
            ":attr_value; WHERE ATTR_ID = (SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = :attr_name;) AND " +
            "GROUP_ID = (SELECT ID FROM UM_GROUP WHERE GROUP_UNIQUE_ID = :group_unique_id;)";

    private static final String ADD_PASSWORD_INFO = "INSERT INTO UM_PASSWORD_INFO " +
            "(PASSWORD_SALT, HASH_ALGO, ITERATION_COUNT, KEY_LENGTH, USER_ID) " +
            "VALUES (:password_salt;, :hash_algo;, :iteration_count;, :key_length;, (SELECT ID FROM UM_PASSWORD WHERE" +
            " USER_UNIQUE_ID = :user_unique_id;))";

    private static final String ADD_CREDENTIAL = "INSERT INTO UM_PASSWORD (PASSWORD, USER_UNIQUE_ID) " +
            "VALUES (:password;, :user_unique_id;)";

    private static final String UPDATE_CREDENTIAL = "UPDATE UM_PASSWORD SET PASSWORD = :password; " +
            "WHERE USER_UNIQUE_ID = :user_unique_id;";

    private static final String UPDATE_PASSWORD_INFO = "UPDATE UM_PASSWORD_INFO SET HASH_ALGO = :hash_algo;, " +
            "ITERATION_COUNT = :iteration_count;, KEY_LENGTH = :key_length;, PASSWORD_SALT = :password_salt; " +
            "WHERE USER_ID = (SELECT ID FROM UM_PASSWORD WHERE USER_UNIQUE_ID = :user_unique_id;)";

    private static final String DELETE_CREDENTIAL = "DELETE FROM UM_PASSWORD " +
            "WHERE USER_UNIQUE_ID = :user_unique_id;";

    public MySQLFamilySQLQueryFactory() {

        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_PASSWORD_DATA, GET_PASSWORD_DATA);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_USER_FROM_ATTRIBUTE, GET_USER_FROM_ATTRIBUTE);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_GROUP_FROM_ATTRIBUTE, GET_GROUP_FROM_ATTRIBUTE);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_USER_ATTRIBUTES, GET_USER_ATTRIBUTES);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_USERS_BY_ATTRIBUTE_PATTERN,
                LIST_USERS_BY_ATTRIBUTE_PATTERN);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_USERS_BY_ATTRIBUTE, LIST_USERS_BY_ATTRIBUTE);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_USER_ATTRIBUTES_FROM_NAME,
                GET_USER_ATTRIBUTES_FROM_NAME);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_IS_USER_IN_GROUP, IS_USER_IN_GROUP);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_GROUP_BY_ATTRIBUTE_PATTERN,
                LIST_GROUP_BY_ATTRIBUTE_PATTERN);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_GROUP_BY_ATTRIBUTE, LIST_GROUP_BY_ATTRIBUTE);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_GROUP_ATTRIBUTES, GET_GROUP_ATTRIBUTES);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_GROUP_ATTRIBUTES_FROM_NAME,
                GET_GROUP_ATTRIBUTES_FROM_NAME);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_COUNT_USERS, COUNT_USERS);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_COUNT_GROUPS, COUNT_GROUPS);

        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_ATTRIBUTES, ADD_USER_ATTRIBUTES);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES, ADD_GROUP_ATTRIBUTES);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER, ADD_USER);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP, ADD_GROUP);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_GROUP, ADD_USER_GROUPS);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_GROUPS_OF_USER,
                REMOVE_ALL_GROUPS_OF_USER);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_USERS_OF_GROUP,
                REMOVE_ALL_USERS_OF_GROUP);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_GROUP_OF_USER, REMOVE_GROUP_OF_USER);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_USER,
                REMOVE_ALL_ATTRIBUTES_OF_USER);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_GROUP,
                REMOVE_ALL_ATTRIBUTES_OF_GROUP);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ATTRIBUTE_OF_USER,
                REMOVE_ATTRIBUTE_OF_USER);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ATTRIBUTE_OF_GROUP,
                REMOVE_ATTRIBUTE_OF_GROUP);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_USER, DELETE_USER);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_GROUP, DELETE_GROUP);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_UPDATE_USER_ATTRIBUTES,
                UPDATE_USER_ATTRIBUTES);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_UPDATE_GROUP_ATTRIBUTES,
                UPDATE_GROUP_ATTRIBUTES);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_PASSWORD_INFO, ADD_PASSWORD_INFO);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_CREDENTIAL, ADD_CREDENTIAL);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_UPDATE_CREDENTIAL, UPDATE_CREDENTIAL);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_UPDATE_PASSWORD_INFO, UPDATE_PASSWORD_INFO);
        sqlQueries.put(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_CREDENTIAL, DELETE_CREDENTIAL);
    }

    public String getQuerryForUserIdFromMultipleAttributes(List<Attribute> attributes, int offset, int length) {
        StringBuilder getUniqueUserQuerry = new StringBuilder();
         getUniqueUserQuerry.append("SELECT UM_USER.USER_UNIQUE_ID FROM UM_USER WHERE UM_USER.ID IN");
        int count = 1;
        for (Attribute attribute : attributes) {
            getUniqueUserQuerry
                    .append(" (SELECT UM_USER_ATTRIBUTES.USER_ID FROM UM_USER_ATTRIBUTES" +
                            " WHERE ATTR_ID = (SELECT ID FROM UM_ATTRIBUTES WHERE ATTR_NAME = '")
                    .append(attribute.getAttributeName())
                    .append("' ) AND ATTR_VALUE = '")
                    .append(attribute.getAttributeValue())
                    .append("')");
            if (count < attributes.size()) {
                getUniqueUserQuerry.append(" AND ");
            }
            ++count;
        }
        getUniqueUserQuerry.append(" GROUP BY USER_UNIQUE_ID LIMIT ");
        getUniqueUserQuerry.append(length);
        getUniqueUserQuerry.append(";");

        return getUniqueUserQuerry.toString();
    }
}
