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

package org.wso2.carbon.auth.oauth.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dto.ClientPublicInfo;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Implementation of ClientDOA interface
 */
public class ClientDAOImpl implements ClientDAO {
    private static final Logger log = LoggerFactory.getLogger(ClientDAOImpl.class);

    /**
     * Constructor is package private, use factory class to create an instance of this class
     */
    ClientDAOImpl() {
    }

    @Override
    public Optional<ClientPublicInfo> getClientPublicInfo(String clientId) throws ClientDAOException {
        final String query = "SELECT REDIRECT_URI FROM AUTH_OAUTH2_CLIENTS WHERE CLIENT_ID = ?";
        Optional<ClientPublicInfo> info = Optional.empty();

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clientId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    ClientPublicInfo data = new ClientPublicInfo();
                    data.setClientId(clientId);
                    data.setRedirectUri(rs.getString("REDIRECT_URI"));
                    info = Optional.of(data);
                }
            }
        } catch (SQLException e) {
            throw new ClientDAOException(
                    String.format("Error occurred while getting client public info(clientId : %s", clientId), e);
        }

        return info;
    }

    @Override
    public void addAuthCodeInfo(String authCode, String clientId, URI redirectUri) throws ClientDAOException {
        try {
            addAuthCodeInfoInDB(authCode, clientId, redirectUri);
        } catch (SQLException e) {
            throw new ClientDAOException(
                    String.format("Error occurred while registering redirect Uri(clientId : %s, redirectUri : %s",
                            clientId, redirectUri), e);
        }
    }

    @Override
    public boolean isAuthCodeInfoValid(String authCode, String clientId, @Nullable URI redirectUri)
                                                                            throws ClientDAOException {
        final String query = "SELECT 1 FROM AUTH_OAUTH2_AUTHORIZATION_CODE " +
                "WHERE CLIENT_ID = ? AND AUTHORIZATION_CODE = ? AND REDIRECT_URI = ?";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clientId);
            statement.setString(2, authCode);

            if (redirectUri != null) {
                statement.setString(3, redirectUri.toString());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ClientDAOException("Error occurred while checking if auth code info is valid(clientId: "
                    + clientId, e);
        }
    }

    private void addAuthCodeInfoInDB(String authCode, String clientId, @Nullable URI redirectUri) throws SQLException {
        final String query = "INSERT INTO AUTH_OAUTH2_AUTHORIZATION_CODE" +
                "(CLIENT_ID, AUTHORIZATION_CODE, REDIRECT_URI) VALUES(?, ?, ?)";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, clientId);
                statement.setString(2, authCode);

                if (redirectUri != null) {
                    statement.setString(3, redirectUri.toString());
                } else {
                    statement.setNull(3, Types.VARCHAR);
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
}
