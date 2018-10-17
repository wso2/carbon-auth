/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.client.registration.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.client.registration.Constants;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.datasource.DAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the ApplicationDAO interface. Uses SQL syntax that is common to H2 and MySQL DBs.
 * Hence is considered as the default due to its re-usability.
 */
public class ApplicationDAOImpl implements ApplicationDAO {

    /**
     * Constructor is package private, use factory class to create the
     */
    ApplicationDAOImpl() {
    }

    @Override
    public Application getApplication(String clientId) throws ClientRegistrationDAOException {
        final String query = "SELECT * "
                + "FROM AUTH_OAUTH2_APPLICATION WHERE CLIENT_ID = ?";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clientId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Application data = new Application();
                    data.setClientId(clientId);
                    /* todo add encryption util
                    data.setClientSecret(
                            persistenceProcessor.getPreprocessedClientSecret(rs.getString("CLIENT_SECRET")));
                    */
                    data.setClientSecret(rs.getString("CLIENT_SECRET"));
                    data.setAuthUser(rs.getString("AUTHZ_USER"));
                    data.setClientName(rs.getString("APP_NAME"));
                    data.setCallBackUrl(rs.getString("REDIRECT_URI"));
                    data.setGrantTypes(rs.getString("GRANT_TYPES"));
                    data.setApplicationAccessTokenExpiryTime(rs.getLong("APP_ACCESS_TOKEN_EXPIRE_TIME"));
                    data.setRefreshTokenExpiryTime(rs.getString("REFRESH_TOKEN_EXPIRE_TIME"));
                    data.setUserAccessTokenExpiryTime(rs.getString("USER_ACCESS_TOKEN_EXPIRE_TIME"));
                    data.setTokenType(rs.getString("TOKEN_TYPE"));
                    data.setAudiences(getAudiences(connection, clientId));
                    return data;
                }
            }
        } catch (SQLException e) {
            throw new ClientRegistrationDAOException(
                    String.format("Error occurred while getting client application public info(clientId : %s",
                            clientId), e);
        }

        return null;
    }

    @Override
    public void deleteApplication(String clientId) throws ClientRegistrationDAOException {
        final String query = "DELETE FROM AUTH_OAUTH2_APPLICATION WHERE CLIENT_ID=?";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);
                statement.setString(1, clientId);
                statement.execute();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }

        } catch (SQLException e) {
            throw new ClientRegistrationDAOException(
                    String.format("Error occurred while deleting client application (clientId : %s",
                            clientId), e);
        }
    }

    @Override
    public Application createApplication(Application application) throws ClientRegistrationDAOException  {
        try {
            addClientInfoInDB(application);
        } catch (SQLException e) {
            throw new ClientRegistrationDAOException(
                    String.format("Error occurred while registering Client App (clientId : %s, redirectUri : %s",
                            application.getClientId(), application.getCallBackUrl()), e);
        }
        return getApplication(application.getClientId());
    }

    @Override
    public Application updateApplication(String clientId, Application application)
            throws ClientRegistrationDAOException {

        try {
            updateClientInfoInDB(application, clientId);
        } catch (SQLException e) {
            throw new ClientRegistrationDAOException(
                    String.format("Error occurred while updating a Client App (clientId : %s, redirectUri : %s",
                            application.getClientId(), application.getCallBackUrl()), e);
        }
        return getApplication(clientId);
    }

    private void addClientInfoInDB(Application application) throws SQLException, ClientRegistrationDAOException {
        final String query = "INSERT INTO AUTH_OAUTH2_APPLICATION " +
                "(CLIENT_ID, CLIENT_SECRET, AUTHZ_USER, APP_NAME, OAUTH_VERSION," +
                " REDIRECT_URI, GRANT_TYPES, APP_ACCESS_TOKEN_EXPIRE_TIME,TOKEN_TYPE) VALUES (?,?,?,?,?,?,?,?,?) ";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, application.getClientId());
                /* todo add encryption util
                statement.setString(2, persistenceProcessor.getProcessedClientSecret(application.getClientSecret()));
                */
                statement.setString(2, application.getClientSecret());
                statement.setString(3, application.getAuthUser());
                statement.setString(4, application.getClientName());
                statement.setString(5, application.getOauthVersion());

                if (application.getCallBackUrl() != null) {
                    statement.setString(6, application.getCallBackUrl());
                } else {
                    statement.setNull(6, Types.VARCHAR);
                }

                if (application.getGrantTypes() != null) {
                    statement.setString(7, application.getGrantTypes());
                } else {
                    statement.setNull(7, Types.VARCHAR);
                }
                if (application.getApplicationAccessTokenExpiryTime() != null) {
                    statement.setLong(8, application.getApplicationAccessTokenExpiryTime());
                } else {
                    statement.setNull(8, Types.INTEGER);
                }
                if (StringUtils.isNotEmpty(application.getTokenType())) {
                    statement.setString(9, application.getTokenType());
                } else {
                    statement.setString(9, Constants.DEFAULT_TOKEN_TYPE);
                }
                statement.execute();
                if (!application.getAudiences().isEmpty()) {
                    addAudiences(connection, application.getClientId(), application.getAudiences());
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }
        }
    }

    private void addAudiences(Connection connection, String clientId, List<String> audiences) throws SQLException {
        final String query = "INSERT INTO AUTH_OAUTH2_APPLICATION_AUDIENCES (CLIENT_ID,AUDIENCE_VALUE) VALUES (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (String audience : audiences) {
                preparedStatement.setString(1, clientId);
                preparedStatement.setString(2, audience);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    private List<String> getAudiences(Connection connection, String clientId) throws SQLException {
        List<String> audiencesList = new ArrayList();
        final String query = "SELECT AUDIENCE_VALUE FROM AUTH_OAUTH2_APPLICATION_AUDIENCES WHERE CLIENT_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, clientId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    audiencesList.add(resultSet.getString("AUDIENCE_VALUE"));
                }
            }
            return audiencesList;
        }
    }
    private void removeAudiences(Connection connection, String clientId) throws SQLException {
        final String query = "DELETE FROM AUTH_OAUTH2_APPLICATION_AUDIENCES WHERE CLIENT_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, clientId);
            preparedStatement.executeUpdate();
        }
    }

    private void updateClientInfoInDB(Application application, String clientId) throws SQLException {
        String query = "UPDATE AUTH_OAUTH2_APPLICATION SET APP_NAME=?," +
                " REDIRECT_URI=?, GRANT_TYPES=?, USER_ACCESS_TOKEN_EXPIRE_TIME=?, " +
                "APP_ACCESS_TOKEN_EXPIRE_TIME=?, REFRESH_TOKEN_EXPIRE_TIME=?, TOKEN_TYPE=?" +
                "WHERE CLIENT_ID=?";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, application.getClientName());
                statement.setString(2, application.getCallBackUrl());
                statement.setString(3, application.getGrantTypes());
                statement.setString(4, application.getUserAccessTokenExpiryTime());
                if (application.getApplicationAccessTokenExpiryTime() != null) {
                    statement.setLong(5, application.getApplicationAccessTokenExpiryTime());
                } else {
                    statement.setNull(5, Types.INTEGER);
                }
                statement.setString(6, application.getRefreshTokenExpiryTime());
                statement.setString(7, application.getTokenType());
                statement.setString(8, clientId);

                statement.execute();
                removeAudiences(connection, clientId);
                if (!application.getAudiences().isEmpty()) {
                    addAudiences(connection, clientId, application.getAudiences());
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }
        }
    }
}
