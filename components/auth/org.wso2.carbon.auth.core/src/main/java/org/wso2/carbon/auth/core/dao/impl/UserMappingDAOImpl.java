/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.core.dao.impl;


import org.wso2.carbon.auth.core.dao.UserMappingDAO;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.core.exception.AuthDAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Provides conversion between real user name and pseudo name.
 * This was implemented to GDPR compliance in API Manager.
 */
public class UserMappingDAOImpl implements UserMappingDAO {

    /**
     * @param pseudoName pseudo name parameter of the user
     * @return String user id
     * @throws AuthDAOException when error in data retrieving
     */
    @Override
    public String getUserIDByPseudoName(String pseudoName) throws AuthDAOException {
        final String query = "SELECT USER_DOMAIN_NAME, USER_IDENTIFIER FROM AUTH_USER_NAME_MAPPING WHERE " +
                "PSEUDO_NAME = ?";
        String realName = null;
        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, pseudoName);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    realName = rs.getString("USER_IDENTIFIER");
                }
            }
        } catch (SQLException e) {
            throw new AuthDAOException(DAOUtil.DAO_ERROR_PREFIX + "getting name mappings", e);
        }
        return realName;
    }

    /**
     * @param userID user identifier
     * @return String pseudo name parameter of the user
     * @throws AuthDAOException when error in data retrieving
     */
    @Override
    public String getPseudoNameByUserID(String userID) throws AuthDAOException {
        final String query = "SELECT PSEUDO_NAME FROM AUTH_USER_NAME_MAPPING WHERE USER_IDENTIFIER = ?";
        String pseudoName = null;
        try (Connection connection = DAOUtil.getAuthConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pseudoName = rs.getString("PSEUDO_NAME");
                }
            }
            if (pseudoName == null) {
                pseudoName = addUserMapping(userID, connection);
            }
        } catch (SQLException e) {
            throw new AuthDAOException(DAOUtil.DAO_ERROR_PREFIX + "getting name mappings", e);
        }
        return pseudoName;
    }

    /**
     * @param userName
     * @return
     * @throws AuthDAOException when error while inserting data
     */
    private String addUserMapping(String userName, Connection connection) throws AuthDAOException, SQLException {
        String pseudoName = UUID.randomUUID().toString();
        final String query = "INSERT INTO AUTH_USER_NAME_MAPPING (PSEUDO_NAME, USER_IDENTIFIER) VALUES (?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);
                statement.setString(1, pseudoName);
                statement.setString(2, userName);
                statement.execute();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                String errorMessage = "Error while adding user mapping ";
                throw new AuthDAOException(DAOUtil.DAO_ERROR_PREFIX + errorMessage, e);
            } finally {
                connection.setAutoCommit(DAOUtil.isAutoCommitAuth());
            }
        }
        return pseudoName;
    }
}
