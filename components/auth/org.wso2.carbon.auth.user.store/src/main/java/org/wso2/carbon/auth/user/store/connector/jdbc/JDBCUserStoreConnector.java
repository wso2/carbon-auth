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
import org.wso2.carbon.auth.core.exception.TemplateExceptionCodes;
import org.wso2.carbon.auth.user.store.configuration.models.AttributeConfiguration;
import org.wso2.carbon.auth.user.store.configuration.models.Uniqueness;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.jdbc.queries.MySQLFamilySQLQueryFactory;
import org.wso2.carbon.auth.user.store.constant.DatabaseColumnNames;
import org.wso2.carbon.auth.user.store.constant.JDBCConnectorConstants;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants.Operation;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.StoreException;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.store.util.NamedPreparedStatement;
import org.wso2.carbon.auth.user.store.util.UnitOfWork;
import org.wso2.carbon.auth.user.store.util.UserStoreUtil;
import org.wso2.carbon.datasource.core.exception.DataSourceException;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.security.auth.callback.PasswordCallback;
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
    protected String userStoreId;
    protected Map<String, String> sqlQueries;
    private Map<String, Object> properties;

    protected void loadQueries(Map<String, String> properties) {

        String databaseType = properties.get(JDBCConnectorConstants.DATABASE_CLASS_NAME);

        if (databaseType != null && (databaseType.contains("MySQL") || databaseType.contains("H2"))) {
            sqlQueries = new MySQLFamilySQLQueryFactory().getQueries();
            if (log.isDebugEnabled()) {
                log.debug("{} sql queries loaded for database type: {}.", sqlQueries.size(), databaseType);
            }
        } else {
            throw new StoreException("Invalid or unsupported database type specified in the configuration.");
        }

        // If there are matching queries in the properties, we have to override the default and replace with them.
        sqlQueries.putAll(sqlQueries.keySet().stream().filter(properties::containsKey)
                .collect(Collectors.toMap(key -> key, properties::get)));
    }

    @Override
    public void init(UserStoreConfiguration userStoreConfig) throws UserStoreConnectorException {
        this.userStoreId = UserStoreConstants.USER_STORE_ID_CONST;
        this.properties = userStoreConfig.getJdbcProperties();
        Map<String, String> strProperties = new HashMap<String, String>();

        for (Entry<String, Object> entry : this.properties.entrySet()) {
            if (entry.getValue() instanceof String) {
                strProperties.put(entry.getKey(), (String) entry.getValue());
            }
        }

        this.userStoreConfig = userStoreConfig;

        try {
            dataSource = (DataSource) ServiceReferenceHolder.getInstance().getDataSourceService()
                    .getDataSource((String) properties.get(JDBCConnectorConstants.DATA_SOURCE));
            if (dataSource == null) {
                throw new UserStoreConnectorException("Datasource is not configured properly");
            }
            try (Connection con = dataSource.getConnection()) {
                strProperties.put(JDBCConnectorConstants.DATABASE_CLASS_NAME, con.getMetaData().getDriverName());
                this.properties.put(JDBCConnectorConstants.DATABASE_CLASS_NAME, con.getMetaData().getDriverName());
            }
        } catch (DataSourceException e) {
            throw new UserStoreConnectorException("Error occurred while initiating data source.", e);
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while getting data source metadata.", e);
        }

        loadQueries(strProperties);

        if (log.isDebugEnabled()) {
            log.debug("JDBC identity store with id: {} initialized successfully.", userStoreId);
        }

    }

    @Override
    public String getConnectorUserId(String attributeUri, String attributeValue)
            throws UserNotFoundException, UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_USER_FROM_ATTRIBUTE));

            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attributeUri);
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
    public List<String> listConnectorUserIds(String attributeUri, String attributeValue, int startIndex, int length)
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
            NamedPreparedStatement listUsersNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_USER_IDS_BY_ATTRIBUTE));
            listUsersNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attributeUri);

            listUsersNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attributeValue);
            listUsersNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.LENGTH, length);
            listUsersNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.OFFSET, startIndex);

            try (ResultSet resultSet = listUsersNamedPreparedStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String userUniqueId = resultSet.getString(DatabaseColumnNames.User.USER_UNIQUE_ID);
                    userList.add(userUniqueId);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("{} users retrieved from identity store: {}.", userList.size(), userStoreId);
            }

            return userList;
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while listing users.", e);
        }
    }

    @Override
    public List<String> listConnectorUserIds(int offset, int length) throws UserStoreConnectorException {
        // Database handles start index as 0
        if (offset > 0) {
            offset--;
        }
        // Get the max allowed row count if the length is -1.
        if (length == -1) {
            length = getMaxRowRetrievalCount();
        }

        List<String> userList = new ArrayList<>();

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {
            NamedPreparedStatement listUsersNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_USER_IDS));
            listUsersNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.LENGTH, length);
            listUsersNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.OFFSET, offset);

            try (ResultSet resultSet = listUsersNamedPreparedStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String userUniqueId = resultSet.getString(DatabaseColumnNames.User.USER_UNIQUE_ID);
                    userList.add(userUniqueId);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("{} users retrieved from identity store: {}.", userList.size(), userStoreId);
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
                    attribute.setAttributeUri(resultSet.getString(DatabaseColumnNames.UserAttributes.ATTR_URI));
                    attribute.setAttributeValue(resultSet.getString(DatabaseColumnNames.UserAttributes.ATTR_VALUE));
                    userAttributes.add(attribute);
                }

                if (log.isDebugEnabled()) {
                    log.debug(userAttributes.size() + " attributes of user: {} retrieved from identity store: {}.",
                            userId, userStoreId);
                }

                return userAttributes;
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving user attributes.", e);
        }
    }

    @Override
    public String getConnectorGroupId(String attributeUri, String attributeValue)
            throws GroupNotFoundException, UserStoreConnectorException {

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_GROUP_FROM_ATTRIBUTE));

            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attributeUri);
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
    public List<String> listConnectorGroupIds(String attributeUri, String attributeValue, int startIndex, int length)
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
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_GROUPS_BY_ATTRIBUTE));
            listGroupsNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attributeUri);
            listGroupsNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE, attributeValue);
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
                        attributeValue, userStoreId);
            }

            return groups;

        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving group list.", e);
        }
    }

    @Override
    public List<String> listConnectorGroupIds(int startIndex, int length) throws UserStoreConnectorException {

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
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_GROUPS));
            listGroupsNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.LENGTH, length);
            listGroupsNamedPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.OFFSET, startIndex);

            try (ResultSet resultSet = listGroupsNamedPreparedStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String groupUniqueId = resultSet.getString(DatabaseColumnNames.Group.GROUP_UNIQUE_ID);
                    groups.add(groupUniqueId);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug(groups.size() + " groups retrieved from identity store: {}.", userStoreId);
            }

            return groups;

        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving group list.", e);
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
                    attribute.setAttributeUri(resultSet.getString(DatabaseColumnNames.GroupAttributes.ATTR_URI));
                    attribute.setAttributeValue(resultSet.getString(DatabaseColumnNames.GroupAttributes.ATTR_VALUE));
                    groupAttributes.add(attribute);
                }

                return groupAttributes;
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while retrieving attribute values of the group" + ".",
                    e);
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
        String databaseType = (String) this.properties.get(JDBCConnectorConstants.DATABASE_CLASS_NAME);
        String sqlQuerryForUserAttributes;

        if (databaseType != null && (databaseType.contains("MySQL") || databaseType.contains("H2"))) {

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
        
        String connectorUniqueId = Optional.ofNullable(getIdFromAttributes(attributes))
                .orElse(UserStoreUtil.generateUUID());
        //validate if all attributes are valid (exists in DB)
        validateAttributes(attributes, UserStoreConstants.RESOURCE_USER, Operation.ADD);

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {

            NamedPreparedStatement addUserNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER));
            addUserNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, connectorUniqueId);
            addUserNamedPreparedStatement.getPreparedStatement().executeUpdate();

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attribute.getAttributeUri());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                        attribute.getAttributeValue());
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, connectorUniqueId);
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
    public String updateUserAttributes(String userIdentifier, List<Attribute> attributes)
            throws UserStoreConnectorException {

        //validate if all attributes are valid (exists in DB)
        validateAttributes(attributes, UserStoreConstants.RESOURCE_USER, Operation.UPDATE);

        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {

            //Delete the existing attributes
            NamedPreparedStatement removeAttributesNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_USER));
            removeAttributesNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            removeAttributesNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //Add new user attributes
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attribute.getAttributeUri());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                        attribute.getAttributeValue());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
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
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_USER));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            namedPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while deleting user.", e);
        }
    }

    @Override
    public void updateGroupsOfUser(String userIdentifier, List<String> groupIdentifiers)
            throws UserStoreConnectorException {

        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            //remove already existing groups
            NamedPreparedStatement deleteNamedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_GROUPS_OF_USER));
            deleteNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            deleteNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //add new groups
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_GROUP));
            for (String groupIdentifier : groupIdentifiers) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating groups of user.", e);
        }
    }

    @Override
    public void removeGroupsOfUser(String userIdentifier) throws UserStoreConnectorException {
        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            //remove already existing groups
            NamedPreparedStatement deleteNamedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_GROUPS_OF_USER));
            deleteNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            deleteNamedPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while deleting groups of user.", e);
        }
    }

    @Override
    public List<String> getUserIdsOfGroup(String groupIdentifier) throws UserStoreConnectorException {
        List<String> userIdsToReturn = new ArrayList<>();
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement getUsersOfGroupStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_USER_IDS_OF_GROUP));
            getUsersOfGroupStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
            try (ResultSet resultSet = getUsersOfGroupStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String userUniqueId = resultSet.getString(DatabaseColumnNames.User.USER_UNIQUE_ID);
                    userIdsToReturn.add(userUniqueId);
                }
                
                return userIdsToReturn;
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating groups of user.", e);
        }
    }

    @Override
    public List<String> getGroupIdsOfUser(String userIdentifier) throws UserStoreConnectorException {
        List<String> groupIdsToReturn = new ArrayList<>();
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement getUsersOfGroupStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_LIST_GROUP_IDS_OF_USER));
            getUsersOfGroupStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            try (ResultSet resultSet = getUsersOfGroupStatement.getPreparedStatement().executeQuery()) {

                while (resultSet.next()) {
                    String userUniqueId = resultSet.getString(DatabaseColumnNames.Group.GROUP_UNIQUE_ID);
                    groupIdsToReturn.add(userUniqueId);
                }

                return groupIdsToReturn;
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating groups of user.", e);
        }
    }

    @Override
    public String addGroup(List<Attribute> attributes) throws UserStoreConnectorException {

        String connectorUniqueId = Optional.ofNullable(getIdFromAttributes(attributes))
                .orElse(UserStoreUtil.generateUUID());
        //validate if all attributes are valid (exists in DB)
        validateAttributes(attributes, UserStoreConstants.RESOURCE_GROUP, Operation.ADD);

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement addGroupNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP));
            addGroupNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, connectorUniqueId);
            addGroupNamedPreparedStatement.getPreparedStatement().executeUpdate();

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attribute.getAttributeUri());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                        attribute.getAttributeValue());
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, connectorUniqueId);
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
    public Map<String, String> addGroups(Map<String, List<Attribute>> attributes) throws UserStoreConnectorException {

        UserStoreConnectorException identityStoreException = new UserStoreConnectorException();
        Map<String, String> groupIdsToReturn = new HashMap<>();
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement addGroupNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(), sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP));
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES));
            attributes.entrySet().stream().forEach(entry -> {
                try {
                    String connectorUniqueId = UserStoreUtil.generateUUID();
                    try {

                        addGroupNamedPreparedStatement
                                .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, connectorUniqueId);
                        addGroupNamedPreparedStatement.getPreparedStatement().addBatch();

                        for (Attribute attribute : entry.getValue()) {
                            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI,
                                    attribute.getAttributeUri());
                            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                                    attribute.getAttributeValue());
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
    public String updateGroupAttributes(String groupIdentifier, List<Attribute> attributes)
            throws UserStoreConnectorException {

        //validate if all attributes are valid (exists in DB)
        validateAttributes(attributes, UserStoreConstants.RESOURCE_GROUP, Operation.UPDATE);

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {

            //Delete the existing attributes
            NamedPreparedStatement removeAttributesNamedPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_ATTRIBUTES_OF_GROUP));
            removeAttributesNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
            removeAttributesNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //Add new group attributes
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_GROUP_ATTRIBUTES));
            for (Attribute attribute : attributes) {
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, attribute.getAttributeUri());
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_VALUE,
                        attribute.getAttributeValue());
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
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
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_GROUP));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
            namedPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while deleting user.", e);
        }
    }

    @Override
    public void updateUsersOfGroup(String groupIdentifier, List<String> userIdentifiers)
            throws UserStoreConnectorException {

        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            //remove already existing users
            NamedPreparedStatement deleteNamedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_USERS_OF_GROUP));
            deleteNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
            deleteNamedPreparedStatement.getPreparedStatement().executeUpdate();

            //add new users
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_USER_GROUP));
            for (String userIdentifier : userIdentifiers) {
                namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
                namedPreparedStatement
                        .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
                namedPreparedStatement.getPreparedStatement().addBatch();
            }
            namedPreparedStatement.getPreparedStatement().executeBatch();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while updating users of group.", e);
        }
    }

    @Override
    public void removeUsersOfGroup(String groupIdentifier) throws UserStoreConnectorException {

        //PUT operation
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            //remove already existing users
            NamedPreparedStatement deleteNamedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_REMOVE_ALL_USERS_OF_GROUP));
            deleteNamedPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.GROUP_UNIQUE_ID, groupIdentifier);
            deleteNamedPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error occurred while deleting users of group.", e);
        }
    }

    /**
     * Get the maximum number of rows allowed to retrieve in a single query.
     *
     * @return Max allowed number of rows.
     */
    private int getMaxRowRetrievalCount() {

        int length;

        String maxValue = (String) this.properties.get(JDBCConnectorConstants.MAX_ROW_LIMIT);

        if (maxValue == null) {
            length = Integer.MAX_VALUE;
        } else {
            length = Integer.parseInt(maxValue);
        }

        return length;
    }

    @Override
    public String addCredential(String userIdentifier, PasswordCallback passwordCallback)
            throws UserStoreConnectorException {

        char[] password = passwordCallback.getPassword();

        String hashAlgo = getHashAlgo();
        int iterationCount = getIterationCount();
        int keyLength = getKeyLength();

        String salt = UserStoreUtil.generateUUID();

        PasswordHandler passwordHandler = new DefaultPasswordHandler();

        passwordHandler.setIterationCount(iterationCount);
        passwordHandler.setKeyLength(keyLength);

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement addPasswordPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_CREDENTIAL));
            NamedPreparedStatement addPasswordInfoPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_PASSWORD_INFO));

            String hashedPassword;
            try {
                hashedPassword = passwordHandler.hashPassword(password, salt, hashAlgo);
            } catch (NoSuchAlgorithmException e) {
                throw new UserStoreConnectorException("Error while hashing the password.", e);
            }

            //Store password
            addPasswordPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            addPasswordPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.PASSWORD, hashedPassword);

            //Store password info.
            addPasswordInfoPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.PASSWORD_SALT, salt);
            addPasswordInfoPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.HASH_ALGO, hashAlgo);
            addPasswordInfoPreparedStatement
                    .setInt(JDBCConnectorConstants.SQLPlaceholders.ITERATION_COUNT, iterationCount);
            addPasswordInfoPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.KEY_LENGTH, keyLength);
            addPasswordInfoPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);

            addPasswordPreparedStatement.getPreparedStatement().executeUpdate();
            addPasswordInfoPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error while storing user credential.", e);
        }

        return userIdentifier;
    }

    @Override
    public String updateCredentials(String userIdentifier, PasswordCallback passwordCallback)
            throws UserStoreConnectorException {
        char[] password = passwordCallback.getPassword();
        String hashAlgo = getHashAlgo();
        int iterationCount = getIterationCount();
        int keyLength = getKeyLength();

        String salt = UserStoreUtil.generateUUID();

        PasswordHandler passwordHandler = new DefaultPasswordHandler();

        passwordHandler.setIterationCount(iterationCount);
        passwordHandler.setKeyLength(keyLength);

        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement updatePasswordPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_UPDATE_CREDENTIAL));
            NamedPreparedStatement updatePasswordInfoPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_UPDATE_PASSWORD_INFO));

            String hashedPassword;
            try {
                hashedPassword = passwordHandler.hashPassword(password, salt, hashAlgo);
            } catch (NoSuchAlgorithmException e) {
                throw new UserStoreConnectorException("Error while hashing the password.", e);
            }

            //Store password
            updatePasswordPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);
            updatePasswordPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.PASSWORD, hashedPassword);

            //Store password info.
            updatePasswordInfoPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.PASSWORD_SALT, salt);
            updatePasswordInfoPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.HASH_ALGO, hashAlgo);
            updatePasswordInfoPreparedStatement
                    .setInt(JDBCConnectorConstants.SQLPlaceholders.ITERATION_COUNT, iterationCount);
            updatePasswordInfoPreparedStatement.setInt(JDBCConnectorConstants.SQLPlaceholders.KEY_LENGTH, keyLength);
            updatePasswordInfoPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);

            updatePasswordPreparedStatement.getPreparedStatement().executeUpdate();
            updatePasswordInfoPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();

        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error while updating user credential.", e);
        }
        return userIdentifier;
    }

    @Override
    public void deleteCredential(String userIdentifier) throws UserStoreConnectorException {
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection(), false)) {
            NamedPreparedStatement deleteCredentialPreparedStatement = new NamedPreparedStatement(
                    unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_DELETE_CREDENTIAL));
            deleteCredentialPreparedStatement
                    .setString(JDBCConnectorConstants.SQLPlaceholders.USER_UNIQUE_ID, userIdentifier);

            deleteCredentialPreparedStatement.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException("Error while updating user credential.", e);
        }

    }

    @Override
    public Map getUserPasswordInfo(String userId) throws UserStoreConnectorException {
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {

            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_PASSWORD_DATA));

            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.USER_ID, userId);

            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {
                if (resultSet.next()) {
                    Map info = new HashMap();
                    info.put(UserStoreConstants.PASSWORD, resultSet.getString(DatabaseColumnNames.User.PASSWORD));
                    info.put(UserStoreConstants.PASSWORD_SALT,
                            resultSet.getString(DatabaseColumnNames.PasswordInfo.PASSWORD_SALT));
                    info.put(UserStoreConstants.HASH_ALGO,
                            resultSet.getString(DatabaseColumnNames.PasswordInfo.HASH_ALGO));
                    info.put(UserStoreConstants.ITERATION_COUNT,
                            resultSet.getInt(DatabaseColumnNames.PasswordInfo.ITERATION_COUNT));
                    info.put(UserStoreConstants.KEY_LENGTH,
                            resultSet.getInt(DatabaseColumnNames.PasswordInfo.KEY_LENGTH));
                    return info;
                } else {
                    throw new UserStoreConnectorException("Password not found for the user.");
                }
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("An error occurred while getting password info.", e);
        }
    }

    @Override
    public AttributeConfiguration getAttributeConfigByURI(String uri) throws UserStoreConnectorException {
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {
            NamedPreparedStatement namedPreparedStatement = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_GET_ATTR_BY_URI));
            namedPreparedStatement.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, uri);

            try (ResultSet resultSet = namedPreparedStatement.getPreparedStatement().executeQuery()) {
                if (resultSet.next()) {
                    String attributeName = resultSet.getString(DatabaseColumnNames.ATTRIBUTE.NAME);
                    String attributeUri = resultSet.getString(DatabaseColumnNames.ATTRIBUTE.URI);
                    String displayName = resultSet.getString(DatabaseColumnNames.ATTRIBUTE.DISPLAY_NAME);
                    String regex = resultSet.getString(DatabaseColumnNames.ATTRIBUTE.REGEX);
                    Boolean required = resultSet.getBoolean(DatabaseColumnNames.ATTRIBUTE.REQUIRED);
                    int uniqueness = resultSet.getInt(DatabaseColumnNames.ATTRIBUTE.UNIQUENESS);
                    return new AttributeConfiguration(attributeName, attributeUri, displayName, required, regex,
                            Uniqueness.from(uniqueness));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new UserStoreConnectorException("An error occurred while getting attribute info of " + uri, e);
        }
    }

    @Override
    public void addAttribute(AttributeConfiguration config) throws UserStoreConnectorException {
        try (UnitOfWork unitOfWork = UnitOfWork.beginTransaction(dataSource.getConnection())) {
            NamedPreparedStatement namedPrepStmt = new NamedPreparedStatement(unitOfWork.getConnection(),
                    sqlQueries.get(JDBCConnectorConstants.QueryTypes.SQL_QUERY_ADD_ATTR));
            namedPrepStmt.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_NAME, config.getAttributeName());
            namedPrepStmt.setString(JDBCConnectorConstants.SQLPlaceholders.ATTRIBUTE_URI, config.getAttributeUri());
            namedPrepStmt.setString(JDBCConnectorConstants.SQLPlaceholders.DISPLAY_NAME, config.getDisplayName());
            namedPrepStmt.setString(JDBCConnectorConstants.SQLPlaceholders.REGEX, config.getRegex());
            namedPrepStmt.setBoolean(JDBCConnectorConstants.SQLPlaceholders.REQUIRED, config.isRequired());
            namedPrepStmt.setInt(JDBCConnectorConstants.SQLPlaceholders.UNIQUENESS, config.getUniqueness().getValue());
            namedPrepStmt.getPreparedStatement().executeUpdate();
            unitOfWork.endTransaction();
        } catch (SQLException e) {
            throw new UserStoreConnectorException(
                    "An error occurred while adding attribute with uri " + config.getAttributeUri(), e);
        }
    }

    private String getHashAlgo() {
        return userStoreConfig.getHashAlgo();
    }

    private int getIterationCount() {
        return userStoreConfig.getIterationCount();
    }

    private int getKeyLength() {
        return userStoreConfig.getKeyLength();
    }

    /**
     * Validate attributes if they are already exist in the DB
     *
     * @param attributes list of attributes
     * @throws UserStoreConnectorException if any attribute is not present in the DB
     */
    private void validateAttributes(List<Attribute> attributes, int resourceType, Operation operation)
            throws UserStoreConnectorException {
        for (Attribute attribute : attributes) {
            boolean fail = false;
            AttributeConfiguration configuration = getAttributeConfigByURI(attribute.getAttributeUri());
            //throws an error if any attribute type is missing in the database.
            if (configuration == null) {
                throw new UserStoreConnectorException("Cannot find attribute uri " + attribute.getAttributeUri());
            }
            
            //Checking if there are already user or group exists in a system for the particular attribute if it is
            //  specified as a unique attribute.
            if (Uniqueness.GLOBAL == configuration.getUniqueness() || Uniqueness.SERVER == configuration
                    .getUniqueness()) {
                final String errorMsg =
                        "A resource already exist with unique attribute " + configuration.getAttributeName()
                                + " with value " + attribute.getAttributeValue();

                //Retrieve the existing resources with unique attribute value
                List<String> returnedResourceIds = new ArrayList<>();
                if (resourceType == UserStoreConstants.RESOURCE_GROUP) {
                    returnedResourceIds = listConnectorGroupIds(attribute.getAttributeUri(),
                            attribute.getAttributeValue(), 0, 2);
                } else if (resourceType == UserStoreConstants.RESOURCE_USER) {
                    returnedResourceIds = listConnectorUserIds(attribute.getAttributeUri(),
                            attribute.getAttributeValue(), 0, 2);
                }

                //Validation
                if (returnedResourceIds.size() > 1) {
                    //There are more than one resources with specified unique attribute value. Hence fail.
                    fail = true;
                } else if (returnedResourceIds.size() == 1) {
                    //There is one resources with specified unique attribute value. The existing resource id should be 
                    // equal to the current resource id. Otherwise fail.
                    String resourceId = getIdFromAttributes(attributes);
                    if (resourceId != null && !resourceId.equals(returnedResourceIds.get(0))) {
                        fail = true;
                    }
                }

                //handle if fail
                if (fail) {
                    if (Operation.ADD.equals(operation)) {
                        throw new UserStoreConnectorException(errorMsg,
                                new TemplateExceptionCodes.UniqueAttributeViolationAddingResource(
                                        configuration.getAttributeName(), attribute.getAttributeValue()));
                    } else {
                        throw new UserStoreConnectorException(errorMsg,
                                new TemplateExceptionCodes.UniqueAttributeViolationUpdatingResource(
                                        configuration.getAttributeName(), attribute.getAttributeValue()));
                    }
                }
            }
        }
    }

    /**
     * Retrieve the ID from the provided attributes
     * 
     * @param attributes list of attributes
     * @return returns the attribute value with id attribute, null if id attribute not present
     * @throws UserStoreConnectorException if error occurred while getting id attribute
     */
    private String getIdFromAttributes(List<Attribute> attributes) throws UserStoreConnectorException {
        for (Attribute attribute: attributes) {
            if (UserStoreConstants.CLAIM_ID.equals(attribute.getAttributeUri())) {
                return attribute.getAttributeValue();
            }
        }
        return null;
    }

}
