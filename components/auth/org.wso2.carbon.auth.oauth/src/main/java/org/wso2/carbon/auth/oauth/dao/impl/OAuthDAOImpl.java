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
import org.wso2.carbon.auth.oauth.constants.JDBCAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * Implementation of OAuthDAO interface
 */
public class OAuthDAOImpl implements OAuthDAO {
    private static final Logger log = LoggerFactory.getLogger(OAuthDAOImpl.class);
    private static final String UTC = "UTC";


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

        try (Connection connection = DAOUtil.getAuthConnection()) {
            try {
                connection.setAutoCommit(false);
                addAccessTokenInfoInDB(connection, accessTokenData);
                persistingTokenScopes(connection, accessTokenData);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }
        } catch (SQLException e) {
            throw new OAuthDAOException("Error occurred while adding access token info(clientId: "
                    + accessTokenData.getClientId(), e);
        }

    }

    @Override
    public AccessTokenDTO getTokenInfo(String authUser, String grantType, String clientId, String scopes)
            throws OAuthDAOException {
        log.debug("Calling getTokenInfo for clientId: {}", clientId);
        final String query =
                "SELECT ACCESS_TOKEN, REFRESH_TOKEN, CONSUMER_KEY_ID, AUTHZ_USER, GRANT_TYPE, TIME_CREATED, "
                        + "REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_STATE , "
                        + "TOKEN_SCOPE FROM AUTH_OAUTH2_ACCESS_TOKEN INNER JOIN AUTH_OAUTH2_ACCESS_TOKEN_SCOPE ON "
                        + "AUTH_OAUTH2_ACCESS_TOKEN.ID = AUTH_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID WHERE AUTHZ_USER = ? "
                        + "AND GRANT_TYPE = ? AND CONSUMER_KEY_ID = (SELECT ID FROM AUTH_OAUTH2_APPLICATION WHERE "
                        + "CLIENT_ID  = ? ) AND TOKEN_STATE = 'ACTIVE' AND TOKEN_SCOPE = ?";
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, authUser);
            statement.setString(2, grantType);
            statement.setString(3, clientId);
            statement.setString(4, scopes);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    accessTokenDTO.setAccessToken(rs.getString(JDBCAuthConstants.ACCESS_TOKEN));
                    accessTokenDTO.setRefreshToken(rs.getString(JDBCAuthConstants.REFRESH_TOKEN));
                    accessTokenDTO.setAuthUser(rs.getString(JDBCAuthConstants.AUTHZ_USER));
                    accessTokenDTO.setTimeCreated(rs.getTimestamp(JDBCAuthConstants.TIME_CREATED, Calendar
                            .getInstance(TimeZone.getTimeZone(UTC))).getTime());
                    accessTokenDTO.setRefreshTokenCreatedTime(rs.getTimestamp(JDBCAuthConstants
                            .REFRESH_TOKEN_TIME_CREATED, Calendar.getInstance(TimeZone.getTimeZone(UTC))).getTime());
                    accessTokenDTO.setValidityPeriod(rs.getInt(JDBCAuthConstants.VALIDITY_PERIOD));
                    accessTokenDTO
                            .setRefreshTokenValidityPeriod(rs.getInt(JDBCAuthConstants.REFRESH_TOKEN_VALIDITY_PERIOD));
                    accessTokenDTO.setTokenState(rs.getString(JDBCAuthConstants.TOKEN_STATE));
                    accessTokenDTO.setGrantType(rs.getString(JDBCAuthConstants.GRANT_TYPE));
                    accessTokenDTO.setScopes(rs.getString(JDBCAuthConstants.TOKEN_SCOPE));
                    return accessTokenDTO;
                }
            }
        } catch (SQLException e) {
            throw new OAuthDAOException(
                    "Error occurred while checking if auth code info is valid(clientId: " + clientId, e);
        }
        return null;
    }

    @Override
    public AccessTokenDTO getTokenInfo(String accessToken) throws OAuthDAOException {
        log.debug("Calling getTokenInfo for accessToken");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        final String query = "SELECT AUTH_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, CLIENT_ID, "
                + "AUTH_OAUTH2_ACCESS_TOKEN.AUTHZ_USER, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, "
                + "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_SCOPE_HASH, TOKEN_STATE, USER_TYPE, GRANT_TYPE, TOKEN_SCOPE "
                + "FROM AUTH_OAUTH2_ACCESS_TOKEN INNER JOIN  AUTH_OAUTH2_APPLICATION  ON ACCESS_TOKEN = ? AND "
                + "AUTH_OAUTH2_ACCESS_TOKEN.CONSUMER_KEY_ID = AUTH_OAUTH2_APPLICATION.ID LEFT OUTER JOIN "
                + "AUTH_OAUTH2_ACCESS_TOKEN_SCOPE ON AUTH_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID = "
                + "AUTH_OAUTH2_ACCESS_TOKEN.ID";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accessToken);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    accessTokenDTO.setTokenID(rs.getInt(JDBCAuthConstants.TOKEN_ID));
                    accessTokenDTO.setAccessToken(rs.getString(JDBCAuthConstants.ACCESS_TOKEN));
                    accessTokenDTO.setRefreshToken(rs.getString(JDBCAuthConstants.REFRESH_TOKEN));
                    accessTokenDTO.setConsumerKey(rs.getString(JDBCAuthConstants.CLIENT_ID));
                    accessTokenDTO.setAuthUser(rs.getString(JDBCAuthConstants.AUTHZ_USER));
                    accessTokenDTO.setTimeCreated(rs.getTimestamp(JDBCAuthConstants.TIME_CREATED, Calendar
                            .getInstance(TimeZone.getTimeZone(UTC))).getTime());
                    accessTokenDTO.setRefreshTokenCreatedTime(rs.getTimestamp(JDBCAuthConstants
                            .REFRESH_TOKEN_TIME_CREATED, Calendar.getInstance(TimeZone.getTimeZone(UTC))).getTime());
                    accessTokenDTO.setValidityPeriod(rs.getLong(JDBCAuthConstants.VALIDITY_PERIOD));
                    accessTokenDTO
                            .setRefreshTokenValidityPeriod(rs.getLong(JDBCAuthConstants.REFRESH_TOKEN_VALIDITY_PERIOD));
                    accessTokenDTO.setTokenScopeHash(rs.getString(JDBCAuthConstants.TOKEN_SCOPE_HASH));
                    accessTokenDTO.setTokenState(rs.getString(JDBCAuthConstants.TOKEN_STATE));
                    accessTokenDTO.setUserType(rs.getString(JDBCAuthConstants.USER_TYPE));
                    accessTokenDTO.setGrantType(rs.getString(JDBCAuthConstants.GRANT_TYPE));
                    accessTokenDTO.setScopes(rs.getString(JDBCAuthConstants.TOKEN_SCOPE));
                    return accessTokenDTO;
                }
            }
        } catch (SQLException e) {
            throw new OAuthDAOException("Error occurred while getting token information", e);
        }
        return null;
    }

    @Override
    public AccessTokenDTO getTokenInfo(String refreshToken, String consumerkey) throws OAuthDAOException {
        log.debug("Calling getTokenInfo from refreshToken, consumerkey");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        final String query = "SELECT AUTH_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, CLIENT_ID, "
                + "AUTH_OAUTH2_ACCESS_TOKEN.AUTHZ_USER, TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, "
                + "REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_SCOPE_HASH, TOKEN_STATE, USER_TYPE, GRANT_TYPE, TOKEN_SCOPE "
                + "FROM AUTH_OAUTH2_ACCESS_TOKEN INNER JOIN  AUTH_OAUTH2_APPLICATION  ON REFRESH_TOKEN = ? AND "
                + "CLIENT_ID = ? AND AUTH_OAUTH2_ACCESS_TOKEN.CONSUMER_KEY_ID = AUTH_OAUTH2_APPLICATION.ID "
                + "LEFT OUTER JOIN AUTH_OAUTH2_ACCESS_TOKEN_SCOPE ON AUTH_OAUTH2_ACCESS_TOKEN_SCOPE.TOKEN_ID = "
                + "AUTH_OAUTH2_ACCESS_TOKEN.ID";

        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, refreshToken);
            statement.setString(2, consumerkey);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    accessTokenDTO.setTokenID(rs.getInt(JDBCAuthConstants.TOKEN_ID));
                    accessTokenDTO.setAccessToken(rs.getString(JDBCAuthConstants.ACCESS_TOKEN));
                    accessTokenDTO.setRefreshToken(rs.getString(JDBCAuthConstants.REFRESH_TOKEN));
                    accessTokenDTO.setConsumerKey(rs.getString(JDBCAuthConstants.CLIENT_ID));
                    accessTokenDTO.setAuthUser(rs.getString(JDBCAuthConstants.AUTHZ_USER));
                    accessTokenDTO.setTimeCreated(rs.getTimestamp(JDBCAuthConstants.TIME_CREATED, Calendar
                            .getInstance(TimeZone.getTimeZone(UTC))).getTime());
                    accessTokenDTO.setRefreshTokenCreatedTime(rs.getTimestamp(JDBCAuthConstants
                            .REFRESH_TOKEN_TIME_CREATED, Calendar.getInstance(TimeZone.getTimeZone(UTC))).getTime());
                    accessTokenDTO.setValidityPeriod(rs.getLong(JDBCAuthConstants.VALIDITY_PERIOD));
                    accessTokenDTO
                            .setRefreshTokenValidityPeriod(rs.getLong(JDBCAuthConstants.REFRESH_TOKEN_VALIDITY_PERIOD));
                    accessTokenDTO.setTokenScopeHash(rs.getString(JDBCAuthConstants.TOKEN_SCOPE_HASH));
                    accessTokenDTO.setTokenState(rs.getString(JDBCAuthConstants.TOKEN_STATE));
                    accessTokenDTO.setUserType(rs.getString(JDBCAuthConstants.USER_TYPE));
                    accessTokenDTO.setGrantType(rs.getString(JDBCAuthConstants.GRANT_TYPE));
                    accessTokenDTO.setScopes(rs.getString(JDBCAuthConstants.TOKEN_SCOPE));
                    return accessTokenDTO;
                }
            }
        } catch (SQLException e) {
            throw new OAuthDAOException("Error occurred while getting token information", e);
        }
        return null;
    }

    private void addAccessTokenInfoInDB(Connection connection, AccessTokenData accessTokenData) throws SQLException {
        log.debug("Calling addAccessTokenInfoInDB for clientId: {}", accessTokenData.getClientId());

        final String query = "INSERT INTO AUTH_OAUTH2_ACCESS_TOKEN" +
                "(ACCESS_TOKEN, REFRESH_TOKEN, CONSUMER_KEY_ID, AUTHZ_USER, GRANT_TYPE, TIME_CREATED, " +
                "REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, " +
                "TOKEN_STATE) SELECT ?,?, AUTH_OAUTH2_APPLICATION.ID ,?,?,?,?,?,?,? " +
                "FROM AUTH_OAUTH2_APPLICATION WHERE CLIENT_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accessTokenData.getAccessToken());
            statement.setString(2, accessTokenData.getRefreshToken());
            statement.setString(3, accessTokenData.getAuthUser());
            statement.setString(4, accessTokenData.getGrantType());
            statement.setTimestamp(5, Timestamp.from(accessTokenData.getAccessTokenCreatedTime()), Calendar
                    .getInstance(TimeZone.getTimeZone(UTC)));
            if (accessTokenData.getRefreshTokenCreatedTime() != null) {
                statement.setTimestamp(6, Timestamp.from(accessTokenData.getRefreshTokenCreatedTime()), Calendar
                        .getInstance(TimeZone.getTimeZone(UTC)));
            } else {
                statement.setNull(6, Types.TIMESTAMP);
            }
            statement.setLong(7, accessTokenData.getAccessTokenValidityPeriod());
            statement.setLong(8, accessTokenData.getRefreshTokenValidityPeriod());
            statement.setString(9, accessTokenData.getTokenState().toString());
            statement.setString(10, accessTokenData.getClientId());

            statement.execute();
        }
    }

    private void persistingTokenScopes(Connection connection, AccessTokenData accessTokenData) throws SQLException {
        log.debug("Calling persistingTokenScopes for clientId: {}", accessTokenData.getClientId());

        final String query = "INSERT INTO AUTH_OAUTH2_ACCESS_TOKEN_SCOPE (TOKEN_ID, " +
                "TOKEN_SCOPE) VALUES ((SELECT ID FROM AUTH_OAUTH2_ACCESS_TOKEN WHERE ACCESS_TOKEN = ?),?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accessTokenData.getAccessToken());
            statement.setString(2, accessTokenData.getScopes());
            statement.execute();
        }
    }

    private void addAuthCodeInfoInDB(String authCode, String clientId, String scope, @Nullable URI redirectUri)
            throws SQLException {
        log.debug("Calling addAuthCodeInfoInDB for clientId: {}", clientId);

        final String query = "INSERT INTO AUTH_OAUTH2_AUTHORIZATION_CODE" +
                "(CLIENT_ID, AUTHORIZATION_CODE, REDIRECT_URI, SCOPE) VALUES(?, ?, ?, ?)";

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
