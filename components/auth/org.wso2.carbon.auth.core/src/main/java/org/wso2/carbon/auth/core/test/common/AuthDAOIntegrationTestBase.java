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

package org.wso2.carbon.auth.core.test.common;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.core.datasource.DataSource;
import org.wso2.carbon.auth.core.test.common.util.AuthCoreTestUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The base class to use for integration tests
 */
public class AuthDAOIntegrationTestBase {
    private String database;
    protected DataSource authDataSource;
    protected DataSource umDataSource;

    private static final String H2 = "h2";
    private static final int MAX_RETRIES = 5;
    private static final long MAX_WAIT = 5000;
    private static final String TEST_RESOURCES_FOLDER =
            "src" + File.separator + "test" + File.separator + "resources" + File.separator;

    private static final Logger log = LoggerFactory.getLogger(AuthDAOIntegrationTestBase.class);

    protected AuthDAOIntegrationTestBase() {
        database = System.getenv("DATABASE_TYPE");
        if (StringUtils.isEmpty(database)) {
            database = H2;
        }
    }

    protected void init() throws Exception {
        // This used to check connection healthy
        if (H2.equals(database)) {
            authDataSource = AuthCoreTestUtil.getDataSource("jdbc:h2:mem:amdb", "sa", "sa", true);
            umDataSource = AuthCoreTestUtil.getDataSource("jdbc:h2:mem:umdb", "sa", "sa", true);
        }
        verifyDataSourceConnection(authDataSource, MAX_RETRIES, MAX_WAIT);
        verifyDataSourceConnection(umDataSource, MAX_RETRIES, MAX_WAIT);
    }

    @SuppressFBWarnings("UC_USELESS_CONDITION")
    protected void verifyDataSourceConnection(DataSource dataSource, int maxRetries, long maxWait) throws SQLException {
        while (maxRetries > 0) {
            try (Connection ignored = dataSource.getConnection()) {
                log.info("Database Connection Successful: [" + dataSource.getDatasource().toString() + "]");
                break;
            } catch (Exception e) {
                if (maxRetries > 0) {
                    log.warn("Couldn't connect into database retrying after next 5 seconds [" + dataSource
                            .getDatasource().toString() + "]");
                    maxRetries--;
                    try {
                        Thread.sleep(maxWait);
                    } catch (InterruptedException ignored) {

                    }
                } else {
                    log.error("Max tries 5 exceed to connect");
                    throw e;
                }
            }
        }
    }

    protected void setup() throws Exception {
        String authSqlFilePath = null;
        String umSqlFilePath = null;
        if (H2.equals(database)) {
            cleanup();
            authSqlFilePath = ".." + File.separator + ".." + File.separator + ".." + File.separator
                    + "features" + File.separator + "auth-features" + File.separator
                    + "org.wso2.carbon.auth.core.feature" + File.separator + "resources"
                    + File.separator + "dbscripts" + File.separator + "auth" + File.separator + "h2.sql";
            umSqlFilePath = ".." + File.separator + ".." + File.separator + ".." + File.separator
                    + "features" + File.separator + "auth-features" + File.separator
                    + "org.wso2.carbon.auth.core.feature" + File.separator + "resources"
                    + File.separator + "dbscripts" + File.separator + "um" + File.separator + "h2.sql";
        }
        DAOUtil.clearAuthDataSource();
        DAOUtil.clearUMDataSource();
        DAOUtil.initializeAuthDataSource(authDataSource);
        DAOUtil.initializeUMDataSource(umDataSource);
        try (Connection connection = DAOUtil.getAuthConnection()) {
            AuthCoreTestUtil.executeSQLScript(authSqlFilePath, connection);
        }
        try (Connection connection = DAOUtil.getUMConnection()) {
            AuthCoreTestUtil.executeSQLScript(umSqlFilePath, connection);
        }
    }

    protected void setupWithoutTables() throws Exception {
        DAOUtil.clearAuthDataSource();
        DAOUtil.clearUMDataSource();
        DAOUtil.initializeAuthDataSource(authDataSource);
        DAOUtil.initializeUMDataSource(umDataSource);
    }

    protected void cleanup() throws Exception {
        if (H2.equals(database)) {
            final String dropAllQuery = "DROP ALL OBJECTS DELETE FILES";
            try (Connection connection = authDataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute(dropAllQuery);
            }
            try (Connection connection = umDataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute(dropAllQuery);
            }
        }
    }

    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    protected void executeOnAuthDb(final String query) throws Exception {
        if (H2.equals(database)) {
            try (Connection connection = authDataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute(query);
            }
        }
    }
}
