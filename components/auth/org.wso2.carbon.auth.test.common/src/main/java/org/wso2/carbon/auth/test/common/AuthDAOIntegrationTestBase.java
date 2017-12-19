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

package org.wso2.carbon.auth.test.common;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.core.datasource.DataSource;
import org.wso2.carbon.auth.core.util.DBScriptRunnerUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The base class to use for integration tests
 * 
 */
public class AuthDAOIntegrationTestBase {
    private String database;
    protected DataSource authDataSource;
    protected DataSource umDataSource;

    private static final String H2 = "h2";
    private static final int MAX_RETRIES = 5;
    private static final long MAX_WAIT = 5000;

    private static final Logger log = LoggerFactory.getLogger(AuthDAOIntegrationTestBase.class);

    public AuthDAOIntegrationTestBase() {
        database = System.getenv("DATABASE_TYPE");
        if (StringUtils.isEmpty(database)) {
            database = H2;
        }
    }

    protected void init() throws Exception {
        // This used to check connection healthy
        if (H2.equals(database)) {
            authDataSource = new H2TestDataSource("jdbc:h2:./src/test/resources/amdb");
            umDataSource = new H2TestDataSource("jdbc:h2:./src/test/resources/umdb");
        }
        verifyDataSourceConnection(authDataSource, MAX_RETRIES, MAX_WAIT);
        verifyDataSourceConnection(umDataSource, MAX_RETRIES, MAX_WAIT);
    }

    @SuppressFBWarnings("UC_USELESS_CONDITION")
    private void verifyDataSourceConnection(DataSource dataSource, int maxRetries, long maxWait) throws SQLException {
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

    protected void setUp() throws Exception {
        String authSqlFilePath = null;
        String umSqlFilePath = null;
        if (H2.equals(database)) {
            ((H2TestDataSource) authDataSource).resetDB();
            ((H2TestDataSource) umDataSource).resetDB();
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
            DBScriptRunnerUtil.executeSQLScript(authSqlFilePath, connection);
        }
        try (Connection connection = DAOUtil.getUMConnection()) {
            DBScriptRunnerUtil.executeSQLScript(umSqlFilePath, connection);
        }
    }

    protected void setUpWithoutTables() throws Exception {
        DAOUtil.clearAuthDataSource();
        DAOUtil.clearUMDataSource();
        DAOUtil.initializeAuthDataSource(authDataSource);
        DAOUtil.initializeUMDataSource(umDataSource);
    }

    protected void tempDBCleanup() throws Exception {
        if (H2.equals(database)) {
            ((H2TestDataSource) authDataSource).resetDB();
            ((H2TestDataSource) umDataSource).resetDB();
        }
    }
}
