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

import org.wso2.carbon.auth.client.registration.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.datasource.DAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
                + "FROM AUTH_OAUTH2_APPLICATIONS WHERE CLIENT_ID = ?";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clientId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Application data = new Application();
                    data.setClientId(clientId);
                    data.setClientSecret(rs.getString("CLIENT_SECRET"));
                    data.setClientName(rs.getString("APP_NAME"));
                    data.setCallBackUrl(rs.getString("CALLBACK_URL"));
                    data.setGrantTypes(rs.getString("GRANT_TYPES"));
                    data.setApplicationAccessTokenExpiryTime(rs.getString("APP_ACCESS_TOKEN_EXPIRE_TIME"));
                    data.setRefreshTokenExpiryTime(rs.getString("REFRESH_TOKEN_EXPIRE_TIME"));
                    data.setUserAccessTokenExpiryTime(rs.getString("USER_ACCESS_TOKEN_EXPIRE_TIME"));

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
        final String query = "DELETE FROM AUTH_OAUTH2_APPLICATIONS WHERE CLIENT_ID=?";

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

    private void addClientInfoInDB(Application application) throws SQLException {
        final String query = "INSERT INTO AUTH_OAUTH2_APPLICATIONS " +
                "(CLIENT_ID, CLIENT_SECRET, APP_NAME, OAUTH_VERSION," +
                " CALLBACK_URL, GRANT_TYPES) VALUES (?,?,?,?,?,?) ";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, application.getClientId());
                statement.setString(2, application.getClientSecret());
                statement.setString(3, application.getClientName());
                statement.setString(4, application.getOauthVersion());

                if (application.getCallBackUrl() != null) {
                    statement.setString(5, application.getCallBackUrl());
                } else {
                    statement.setNull(5, Types.VARCHAR);
                }

                if (application.getGrantTypes() != null) {
                    statement.setString(6, application.getGrantTypes());
                } else {
                    statement.setNull(6, Types.VARCHAR);
                }

                statement.execute();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }
        }
    }

    private void updateClientInfoInDB(Application application, String clientId) throws SQLException {
        String query = "UPDATE AUTH_OAUTH2_APPLICATIONS SET APP_NAME=?," +
                " CALLBACK_URL=?, GRANT_TYPES=?, USER_ACCESS_TOKEN_EXPIRE_TIME=?, " +
                "APP_ACCESS_TOKEN_EXPIRE_TIME=?, REFRESH_TOKEN_EXPIRE_TIME=? " +
                "WHERE CLIENT_ID=?";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, application.getClientName());
                statement.setString(2, application.getCallBackUrl());
                statement.setString(3, application.getGrantTypes());
                statement.setString(4, application.getUserAccessTokenExpiryTime());
                statement.setString(5, application.getApplicationAccessTokenExpiryTime());
                statement.setString(6, application.getRefreshTokenExpiryTime());
                statement.setString(7, clientId);

                statement.execute();
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
