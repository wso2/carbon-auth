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

package org.wso2.carbon.auth.user.store.connector.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.core.exception.UserNotFoundException;
import org.wso2.carbon.auth.core.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.jdbc.queries.MySQLFamilySQLQueryFactory;
import org.wso2.carbon.auth.user.store.constant.DatabaseColumnNames;
import org.wso2.carbon.auth.user.store.constant.JDBCConnectorConstants;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.StoreException;
import org.wso2.carbon.auth.user.store.internal.ConnectorDataHolder;
import org.wso2.carbon.auth.user.store.util.NamedPreparedStatement;
import org.wso2.carbon.auth.user.store.util.UnitOfWork;
import org.wso2.carbon.auth.user.store.util.UserStoreUtil;
import org.wso2.carbon.datasource.core.exception.DataSourceException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.sql.DataSource;

/**
 * Connector for JDBC based identity stores.
 *
 * @since 1.0.0
 */
public class JDBCUserStoreConnector implements UserStoreConnector {

    private static Logger log = LoggerFactory.getLogger(JDBCUserStoreConnector.class);
    protected DataSource dataSource;
    protected UserStoreConfiguration userStoreConfig;
    protected String identityStoreId;    
    protected Map<String, String> sqlQueries;

    protected void loadQueries(Map<String, Object> properties) {

        String databaseType = (String) properties.get(JDBCConnectorConstants.DATABASE_TYPE);

        if (databaseType != null && (databaseType.equalsIgnoreCase("MySQL") || databaseType.equalsIgnoreCase("H2"))) {
            sqlQueries = new MySQLFamilySQLQueryFactory().getQueries();
            if (log.isDebugEnabled()) {
                log.debug("{} sql queries loaded for database type: {}.", sqlQueries.size(), databaseType);
            }
        } else {
            throw new StoreException("Invalid or unsupported database type specified in the configuration.");
        }

        // If there are matching queries in the properties, we have to override the default and replace with them.
        /*sqlQueries.putAll(sqlQueries.keySet().stream()
                .filter(properties::containsKey)
                .collect(Collectors.toMap(key -> key, properties::get)));*/
    }

    @Override
    public void init(UserStoreConfiguration userStoreConfig) throws UserStoreConnectorException {

        Map<String, Object> properties = userStoreConfig.getProperties();
        this.userStoreConfig = userStoreConfig;

        try {
            dataSource = ConnectorDataHolder.getInstance()
                    .getDataSource((String) properties.get(JDBCConnectorConstants.DATA_SOURCE));
        } catch (DataSourceException e) {
            throw new UserStoreConnectorException("Error occurred while initiating data source.", e);
        }

        loadQueries(properties);

        if (log.isDebugEnabled()) {
            log.debug("JDBC identity store with id: {} initialized successfully.", identityStoreId);
        }

    }

    @Override
    public String getConnectorUserId(String attributeName, String attributeValue) throws UserNotFoundException,
            UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_USER_FROM_ATTRIBUTE));

            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, attributeName);
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attributeValue);

            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(DatabaseColumnNames.User.USER_UNIQUE_ID);
                } else {
                    throw new UserNotFoundException("User not found with the given attribute");
                }
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("An error occurred while getting searching the user.", e);
        }
    }

    @Override
    public List<String> listConnectorUserIds(String attributeName, String attributeValue, int startIndex, int length)
            throws UserStoreConnectorException {

        // Database handles start index as 0
        if (startIndex > 0) {
            startIndex--;
        }
        // Get the max allowed row count if the length is -1.
        if (length == -1) {
            length = getMaxRowRetrievalCount();
        }

        List<String> userList = new ArrayList<>();

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {
            NamedPreparedStatement listUsersNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), 
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_USERS_BY_ATTRIBUTE));
            listUsersNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME,
                    attributeName);

            listUsersNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                    attributeValue);
            listUsersNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.LENGTH, length);
            listUsersNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.OFFSET, startIndex);

            try (ResultSet resultSet = listUsersNamedPreparedStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String userUniqueId = resultSet.getString(DatabaseColumnNames.User.USER_UNIQUE_ID);
                    userList.add(userUniqueId);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("{} users retrieved from identity store: {}.", userList.size(), identityStoreId);
            }

            return userList;
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while listing users.", e);
        }
    }

    @Override
    public List<Attribute> getUserAttributeValues(String userId) throws UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_USER_ATTRIBUTES));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_ID, userId);
            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {

                List<Attribute> userAttributes = new ArrayList<>();

                while (resultSet.next()) {
                    Attribute attribute = new Attribute();
                    attribute.setAttributeName(resultSet.getString(DatabaseColumnNames.UserAttributes.ATTR_NAME));
                    attribute.setAttributeValue(resultSet.getString(DatabaseColumnNames.UserAttributes.ATTR_VALUE));
                    userAttributes.add(attribute);
                }

                if (log.isDebugEnabled()) {
                    log.debug(userAttributes.size() + " attributes of user: {} retrieved from identity store: {}.",
                            userId, identityStoreId);
                }

                return userAttributes;
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving user attributes.", e);
        }
    }
    

    @Override
    public String getConnectorGroupId(String attributeName, String attributeValue) throws GroupNotFoundException,
            UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_GROUP_FROM_ATTRIBUTE));

            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, attributeName);
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attributeValue);

            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(DatabaseColumnNames.Group.GROUP_UNIQUE_ID);
                } else {
                    throw new GroupNotFoundException("User not found with the given attribute");
                }
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("An error occurred while getting searching the group.", e);
        }
    }

    @Override
    public List<String> listConnectorGroupIds(String attributeName, String attributeValue, int startIndex, int length)
            throws UserStoreConnectorException {

        // Database handles start index as 0
        if (startIndex > 0) {
            startIndex--;
        }
        // Get the max allowed row count if the length is -1.
        if (length == -1) {
            length = getMaxRowRetrievalCount();
        }

        List<String> groups = new ArrayList<>();

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement listGroupsNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_GROUP_BY_ATTRIBUTE));
            listGroupsNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME,
                    attributeName);
            listGroupsNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                    attributeValue);
            listGroupsNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.LENGTH, length);
            listGroupsNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.OFFSET, startIndex);

            try (ResultSet resultSet = listGroupsNamedPreparedStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String groupUniqueId = resultSet.getString(DatabaseColumnNames.Group.GROUP_UNIQUE_ID);
                    groups.add(groupUniqueId);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug(groups.size() + " groups retrieved for filter pattern {} from identity store: {}.", 
                        attributeValue, identityStoreId);
            }

            return groups;

        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving group list.");
        }
    }


    @Override
    public List<Attribute> getGroupAttributeValues(String groupId) throws UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_GROUP_ATTRIBUTES));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_ID, groupId);

            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {
                List<Attribute> groupAttributes = new ArrayList<>();

                while (resultSet.next()) {
                    Attribute attribute = new Attribute();
                    attribute.setAttributeName(resultSet.getString(DatabaseColumnNames.GroupAttributes.ATTR_NAME));
                    attribute.setAttributeValue(resultSet.getString(DatabaseColumnNames.GroupAttributes.ATTR_VALUE));
                    groupAttributes.add(attribute);
                }

                return groupAttributes;
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving attribute values of the group" +
                    ".", e);
        }
    }


    @Override
    public boolean isUserInGroup(String userId, String groupId) throws UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_IS_USER_IN_GROUP));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_ID, userId);
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_ID, groupId);

            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error while checking users in group.", e);
        }
    }

    @Override
    public UserStoreConfiguration getUserStoreConfig() {
        return userStoreConfig;
    }

    @Override
    public List<String> getUsers(List<Attribute> attributes, int offset, int length)
            throws UserStoreConnectorException {

        List<String> userIdsToReturn = new ArrayList<>();
        Map<String, Object> properties = userStoreConfig.getProperties();
        String databaseType =  (String) properties.get(JDBCConnectorConstants.DATABASE_TYPE);
        String sqlQuerryForUserAttributes;

        if (databaseType != null && (databaseType.equalsIgnoreCase("MySQL") ||
                databaseType.equalsIgnoreCase("H2"))) {

            sqlQuerryForUserAttributes = new MySQLFamilySQLQueryFactory()
                    .getQuerryForUserIdFromMultipleAttributes(attributes, offset, length);

            if (log.isDebugEnabled()) {
                log.debug("{} sql queries loaded for database type: {}.", sqlQueries.size(), databaseType);
            }
        } else {
            throw new StoreException("Invalid or unsupported database type specified in the configuration.");
        }
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement getUsersNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(), sqlQuerryForUserAttributes);

            try (ResultSet resultSet = getUsersNamedPreparedStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String userUniqueId = resultSet.getString(DatabaseColumnNames.User.USER_UNIQUE_ID);
                    userIdsToReturn.add(userUniqueId);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Users retrieved from identity store: {}.", userIdsToReturn.size());
            }

        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while getting database connection.", e);
        }
        return userIdsToReturn;
    }

    @Override
    public String addUser(List<Attribute> attributes) throws UserStoreConnectorException {

        String connectorUniqueId = UserStoreUtil.generateUUID();

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {

            NamedPreparedStatement addUserNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER));
            addUserNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID,
                    connectorUniqueId);
            addUserNamedPreparedStatement.getPreparedStatement().executeUpdate();

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, attribute
                        .getAttributeName());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attribute
                        .getAttributeValue());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID,
                        connectorUniqueId);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while storing user.", e);
        }
        return connectorUniqueId;
    }

    @Override
    public String updateUserAttributes(String userIdentifier, List<Attribute> attributes) throws
            UserStoreConnectorException {

        //PUT operation

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {

            //Delete the existing attributes
            NamedPreparedStatement removeAttributesNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes
                    .SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_USER));
            removeAttributesNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID,
                    userIdentifier);
            removeAttributesNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //Add new user attributes
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, attribute
                        .getAttributeName());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attribute
                        .getAttributeValue());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID,
                        userIdentifier);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating user.", e);
        }
        return userIdentifier;
    }

    @Override
    public void deleteUser(String userIdentifier) throws UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_USER));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            namedPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while deleting user.", e);
        }
    }

    @Override
    public void updateGroupsOfUser(String userIdentifier, List<String> groupIdentifiers) throws
            UserStoreConnectorException {

        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            //remove already existing groups
            NamedPreparedStatement deleteNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes
                    .SQL_QUERY_REMOVE_ALL_GROUPS_OF_USER));
            deleteNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, 
                    userIdentifier);
            deleteNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //add new groups
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_GROUP));
            for (String groupIdentifier : groupIdentifiers) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID,
                        userIdentifier);
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                        groupIdentifier);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating groups of user.", e);
        }
    }

    @Override
    public String addGroup(List<Attribute> attributes) throws UserStoreConnectorException {

        String connectorUniqueId = UserStoreUtil.generateUUID();

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement addGroupNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP));
            addGroupNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                    connectorUniqueId);
            addGroupNamedPreparedStatement.getPreparedStatement().executeUpdate();

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, attribute
                        .getAttributeName());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attribute
                        .getAttributeValue());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                        connectorUniqueId);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while storing group.", e);
        }

        return connectorUniqueId;
    }

    @Override
    public Map<String, String> addGroups(Map<String, List<Attribute>> attributes) throws
            UserStoreConnectorException {

        UserStoreConnectorException identityStoreException = new UserStoreConnectorException();
        Map<String, String> groupIdsToReturn = new HashMap<>();
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement addGroupNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP));
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES));
            attributes.entrySet().stream().forEach(entry -> {
                try {
                    String connectorUniqueId = UserStoreUtil.generateUUID();
                    try {

                        addGroupNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                                connectorUniqueId);
                        addGroupNamedPreparedStatement.getPreparedStatement().addBatch();

                        for (Attribute attribute : entry.getValue()) {
                            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME,
                                    attribute.getAttributeName());
                            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                                    attribute
                                            .getAttributeValue());
                            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                                    connectorUniqueId);
                            namedPreparedStatement.getPreparedStatement().addBatch();
                        }
                    } catch (SQLException e) {
                        throw new UserStoreConnectorException("Error occurred while storing group.", e);
                    }
                    groupIdsToReturn.put(entry.getKey(), connectorUniqueId);
                } catch (UserStoreConnectorException e) {
                    identityStoreException.addSuppressed(e);
                }
            });
            addGroupNamedPreparedStatement.getPreparedStatement().executeBatch();
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while getting database connection.", e);
        }

        if (identityStoreException.getSuppressed().length > 0) {
            throw identityStoreException;
        }
        return groupIdsToReturn;
    }

    @Override
    public String updateGroupAttributes(String groupIdentifier, List<Attribute> attributes) throws
            UserStoreConnectorException {

        //PUT operation

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {

            //Delete the existing attributes
            NamedPreparedStatement removeAttributesNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes
                    .SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_GROUP));
            removeAttributesNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                    groupIdentifier);
            removeAttributesNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //Add new group attributes
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, attribute
                        .getAttributeName());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attribute
                        .getAttributeValue());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                        groupIdentifier);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating user.", e);
        }
        return groupIdentifier;
    }

    @Override
    public void deleteGroup(String groupIdentifier) throws UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_GROUP));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
            namedPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while deleting user.", e);
        }
    }

    @Override
    public void updateUsersOfGroup(String groupIdentifier, List<String> userIdentifiers) throws
            UserStoreConnectorException {

        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            //remove already existing users
            NamedPreparedStatement deleteNamedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes
                    .SQL_QUERY_REMOVE_ALL_USERS_OF_GROUP));
            deleteNamedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, 
                    groupIdentifier);
            deleteNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //add new users
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork
                    .getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_GROUP));
            for (String userIdentifier : userIdentifiers) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID,
                        userIdentifier);
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID,
                        groupIdentifier);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating groups of user.", e);
        }
    }

    /**
     * Get the maximum number of rows allowed to retrieve in a single query.
     *
     * @return Max allowed number of rows.
     */
    private int getMaxRowRetrievalCount() {

        int length;

        String maxValue = (String) userStoreConfig.getProperties().get(JDBCConnectorConstants.MAX_ROW_LIMIT);

        if (maxValue == null) {
            length = Integer.MAX_VALUE;
        } else {
            length = Integer.parseInt(maxValue);
        }

        return length;
    }

    @Override
    public String addCredential(String userIdentifier, List<Callback> callbacks) throws UserStoreConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String updateCredentials(String userIdentifier, List<Callback> credentialCallbacks)
            throws UserStoreConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteCredential(String userIdentifier) throws UserStoreConnectorException {
        // TODO Auto-generated method stub
        
    }

}
