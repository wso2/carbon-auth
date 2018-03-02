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

package org.wso2.carbon.auth.core.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides Utility functionality required for working with Data sources
 */
public class DAOUtil {
    private static final Logger log = LoggerFactory.getLogger(DAOUtil.class);
    private static DataSource userManagementDataSource;
    private static DataSource authDataSource;
    public static final String DAO_ERROR_PREFIX = "Error occurred in DAO layer while ";

    private DAOUtil() {

    }
    
    public static synchronized void initializeUMDataSource(DataSource dataSource) {
        if (DAOUtil.userManagementDataSource != null) {
            log.debug("UM datasource already initialized");
            return;
        }

        DAOUtil.userManagementDataSource = dataSource;
    }

    public static synchronized void initializeAuthDataSource(DataSource dataSource) {
        if (DAOUtil.authDataSource != null) {
            log.debug("Auth datasource already initialized");
            return;
        }

        DAOUtil.authDataSource = dataSource;
    }

    /**
     * Utility method to get a new UM database connection
     *
     * @return Connection
     * @throws java.sql.SQLException if failed to get Connection
     */

    public static Connection getUMConnection() throws SQLException {
        if (userManagementDataSource != null) {
            return userManagementDataSource.getConnection();
        }
        throw new IllegalStateException("UM Datasource is not configured properly.");
    }

    /**
     * Utility method to get a new Auth database connection
     *
     * @return Connection
     * @throws java.sql.SQLException if failed to get Connection
     */

    public static Connection getAuthConnection() throws SQLException {
        if (authDataSource != null) {
            return authDataSource.getConnection();
        }
        throw new IllegalStateException("Auth Datasource is not configured properly.");
    }

    /**
     * Get is auto commit enabled in UM DB
     *
     * @return true if auto commit is enabled, false otherwise
     * @throws SQLException Error while getting if auto commit is enabled
     */
    public static boolean isAutoCommitUM() throws SQLException {
        return userManagementDataSource.getDatasource().isAutoCommit();
    }

    /**
     * Get is auto commit enabled in Auth DB
     *
     * @return true if auto commit is enabled, false otherwise
     * @throws SQLException Error while getting if auto commit is enabled
     */
    public static boolean isAutoCommitAuth() throws SQLException {
        return authDataSource.getDatasource().isAutoCommit();
    }

    public static void clearUMDataSource() {
        userManagementDataSource = null;
    }

    public static void clearAuthDataSource() {
        authDataSource = null;
    }
}
