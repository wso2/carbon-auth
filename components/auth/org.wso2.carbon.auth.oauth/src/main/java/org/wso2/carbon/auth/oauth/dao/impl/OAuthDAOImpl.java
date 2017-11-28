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
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * Implementation of ClientDOA interface
 */
public class OAuthDAOImpl implements OAuthDAO {
    private static final Logger log = LoggerFactory.getLogger(OAuthDAOImpl.class);

    /**
     * Constructor is package private, use factory class to create an instance of this class
     */
    OAuthDAOImpl() {
    }

    @Override
    public Optional<Optional<String>> getRedirectUri(String clientId) throws OAuthDAOException {
        log.debug("Calling getRedirectUri for clientId: {}", clientId);
        final String query = "SELECT REDIRECT_URI FROM AUTH_OAUTH2_APPLICATION WHERE CLIENT_ID = ?";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clientId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Optional.of(rs.getString("REDIRECT_URI")));
                }
            }
        } catch (SQLException e) {
            throw new OAuthDAOException(
                    String.format("Error occurred while getting client public info(clientId : %s", clientId), e);
        }

        return Optional.empty();
    }

    @Override
    public void addAuthCodeInfo(String authCode, String clientId, String scope, URI redirectUri)
                                                                                    throws OAuthDAOException {
        log.debug("Calling addAuthCodeInfo for clientId: {}", clientId);
        try {
            addAuthCodeInfoInDB(authCode, clientId, scope, redirectUri);
        } catch (SQLException e) {
            throw new OAuthDAOException(
                    String.format("Error occurred while registering redirect Uri(clientId : %s, redirectUri : %s",
                            clientId, redirectUri), e);
        }
    }

    @Override
    @CheckForNull
    public String getScopeForAuthCode(String authCode, String clientId, @Nullable URI redirectUri)
            throws OAuthDAOException {
        log.debug("Calling getScopeForAuthCode for clientId: {}", clientId);

        final String query = "SELECT SCOPE FROM AUTH_OAUTH2_AUTHORIZATION_CODE " +
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
                if (rs.next()) {
                    return rs.getString("SCOPE");
                }
            }
        } catch (SQLException e) {
            throw new OAuthDAOException("Error occurred while checking if auth code info is valid(clientId: "
                    + clientId, e);
        }

        return null;
    }

    @Override
    public boolean isClientCredentialsValid(String clientId, String clientSecret) throws OAuthDAOException {
        log.debug("Calling isClientCredentialsValid for clientId: {}", clientId);

        final String query = "SELECT 1 FROM AUTH_OAUTH2_APPLICATION WHERE CLIENT_ID = ? AND CLIENT_SECRET = ?";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clientId);
            statement.setString(2, clientSecret);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new OAuthDAOException("Error occurred while checking if client credentials valid(clientId: "
                    + clientId, e);
        }
    }

    @Override
    public void addAccessTokenInfo(AccessTokenData accessTokenData) throws OAuthDAOException {
        log.debug("Calling addAccessTokenInfo for clientId: {}", accessTokenData.getClientId());

        try {
            addAccessTokenInfoInDB(accessTokenData);
        } catch (SQLException e) {
            throw new OAuthDAOException("Error occurred while adding access token info(clientId: "
                    + accessTokenData.getClientId(), e);
        }

    }


    private void addAccessTokenInfoInDB(AccessTokenData accessTokenData) throws SQLException {
        log.debug("Calling addAccessTokenInfoInDB for clientId: {}", accessTokenData.getClientId());

        final String query = "INSERT INTO AUTH_OAUTH2_ACCESS_TOKEN" +
                "(ACCESS_TOKEN, REFRESH_TOKEN, APPLICATION_ID, GRANT_TYPE, ACCESS_TOKEN_TIME_CREATED, " +
                "REFRESH_TOKEN_TIME_CREATED, ACCESS_TOKEN_VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, " +
                "TOKEN_STATE) SELECT ?,?,ID FROM AUTH_OAUTH2_APPLICATION WHERE CLIENT_ID = ?,?,?,?,?,?,?";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, accessTokenData.getAccessToken());
                statement.setString(2, accessTokenData.getRefreshToken());
                statement.setString(3, accessTokenData.getClientId());
                statement.setString(4, accessTokenData.getGrantType());
                statement.setTimestamp(5, Timestamp.from(accessTokenData.getAccessTokenCreatedTime()));
                statement.setTimestamp(6, Timestamp.from(accessTokenData.getRefreshTokenCreatedTime()));
                statement.setLong(7, accessTokenData.getAccessTokenValidityPeriod());
                statement.setLong(8, accessTokenData.getRefreshTokenValidityPeriod());
                statement.setString(9, accessTokenData.getTokenState().toString());

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

    private void addAuthCodeInfoInDB(String authCode, String clientId, String scope, @Nullable URI redirectUri)
                                                                                                throws SQLException {
        log.debug("Calling addAuthCodeInfoInDB for clientId: {}", clientId);

        final String query = "INSERT INTO AUTH_OAUTH2_AUTHORIZATION_CODE" +
                "(CLIENT_ID, AUTHORIZATION_CODE, REDIRECT_URI, SCOPE) VALUES(?, ?, ?)";

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

                statement.setString(4, scope);

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
