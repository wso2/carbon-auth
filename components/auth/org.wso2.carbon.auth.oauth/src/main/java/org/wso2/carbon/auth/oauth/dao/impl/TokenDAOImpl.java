/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.oauth.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.oauth.constants.JDBCAuthConstants;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * TokenDAO implementation
 */
public class TokenDAOImpl implements TokenDAO {
    private static final Logger log = LoggerFactory.getLogger(TokenDAOImpl.class);

    /**
     * Persisting token information
     *
     * @param accessToken
     * @param refreshToken
     * @param clientID
     * @param authUser
     * @param userDomain
     * @param timeCreated
     * @param refreshTokenCreatedTime
     * @param validityPeriod
     * @param refreshTokenValidityPeriod
     * @param tokenScopeHash
     * @param tokenState
     * @param userType
     * @param grantType
     * @throws SQLException
     */
    @Override
    public void persistToken(String accessToken, String refreshToken, String clientID, String authUser,
            String userDomain, long timeCreated, long refreshTokenCreatedTime, int validityPeriod,
            int refreshTokenValidityPeriod, String tokenScopeHash, String tokenState, String userType, String grantType)
            throws SQLException {
        String query = "INSERT INTO AUTH_ACCESS_TOKEN (ACCESS_TOKEN, "
                + "REFRESH_TOKEN, CONSUMER_KEY_ID, AUTHZ_USER, TIME_CREATED, "
                + "REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, TOKEN_SCOPE_HASH, "
                + "TOKEN_STATE, USER_TYPE, GRANT_TYPE) SELECT ?,?,(SELECT ID FROM AUTH_OAUTH2_APPLICATIONS "
                + "WHERE CLIENT_ID = ?),?,?,?,?,?,?,?,?,?";

        log.debug("Calling persistToken for clientId: {}", clientID);

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);

                statement.setString(1, accessToken);
                statement.setString(2, refreshToken);
                statement.setString(3, clientID);
                statement.setString(4, authUser);
                statement.setTimestamp(5, new Timestamp(timeCreated));
                statement.setTimestamp(6, new Timestamp(refreshTokenCreatedTime));
                statement.setInt(7, validityPeriod);
                statement.setInt(8, refreshTokenValidityPeriod);
                statement.setString(9, tokenScopeHash);
                statement.setString(10, tokenState);
                statement.setString(11, userType);
                statement.setString(12, grantType);

                statement.execute();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Error occurred while storing token information", e);
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }
        }
    }

    /**
     * Get token information by ID
     *
     * @param tokenID
     * @throws SQLException
     */
    @Override
    public AccessTokenDTO getTokenInfoByID(String tokenID) throws SQLException {
        //Todo: implemented
        return null;
    }

    /**
     * Get token information by token
     *
     * @param accessToken
     * @return
     * @throws SQLException
     */
    @Override
    public AccessTokenDTO getTokenInfo(String accessToken) throws SQLException {
        log.debug("Calling getTokenInfo for accessToken");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        final String query = "SELECT TOKEN_ID, ACCESS_TOKEN, REFRESH_TOKEN, CONSUMER_KEY_ID, AUTHZ_USER, "
                + "TIME_CREATED, REFRESH_TOKEN_TIME_CREATED, VALIDITY_PERIOD, REFRESH_TOKEN_VALIDITY_PERIOD, "
                + "TOKEN_SCOPE_HASH, TOKEN_STATE, USER_TYPE, GRANT_TYPE FROM AUTH_ACCESS_TOKEN WHERE "
                + "ACCESS_TOKEN = ? ";

        try (Connection connection = DAOUtil.getAuthConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accessToken);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    accessTokenDTO.setTokenID(rs.getInt(JDBCAuthConstants.TOKEN_ID));
                    accessTokenDTO.setAccessToken(rs.getString(JDBCAuthConstants.ACCESS_TOKEN));
                    accessTokenDTO.setRefreshToken(rs.getString(JDBCAuthConstants.REFRESH_TOKEN));
                    accessTokenDTO.setConsumerKey(rs.getString(JDBCAuthConstants.CONSUMER_KEY_ID));
                    accessTokenDTO.setAuthUser(rs.getString(JDBCAuthConstants.AUTHZ_USER));
                    accessTokenDTO.setTimeCreated(rs.getTimestamp(JDBCAuthConstants.TIME_CREATED).getTime());
                    accessTokenDTO.setRefreshTokenCreatedTime(
                            rs.getTimestamp(JDBCAuthConstants.REFRESH_TOKEN_TIME_CREATED).getTime());
                    accessTokenDTO.setValidityPeriod(rs.getInt(JDBCAuthConstants.VALIDITY_PERIOD));
                    accessTokenDTO
                            .setRefreshTokenValidityPeriod(rs.getInt(JDBCAuthConstants.REFRESH_TOKEN_VALIDITY_PERIOD));
                    accessTokenDTO.setTokenScopeHash(rs.getString(JDBCAuthConstants.TOKEN_SCOPE_HASH));
                    accessTokenDTO.setTokenState(rs.getString(JDBCAuthConstants.TOKEN_STATE));
                    accessTokenDTO.setUserType(rs.getString(JDBCAuthConstants.USER_TYPE));
                    accessTokenDTO.setGrantType(rs.getString(JDBCAuthConstants.GRANT_TYPE));
                    return accessTokenDTO;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error occurred while getting token information", e);
        }
        return null;
    }
}
