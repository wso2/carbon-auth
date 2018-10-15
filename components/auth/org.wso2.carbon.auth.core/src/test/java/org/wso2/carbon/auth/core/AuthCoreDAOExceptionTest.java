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

package org.wso2.carbon.auth.core;

import com.zaxxer.hikari.HikariDataSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.core.datasource.DataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthCoreDAOExceptionTest {

    private static final Logger log = LoggerFactory.getLogger(AuthCoreDAOExceptionTest.class);

    @BeforeMethod
    public void init() throws Exception {

        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        HikariDataSource hikariDataSource = Mockito.mock(HikariDataSource.class);
        Mockito.when(dataSource.getDatasource()).thenReturn(hikariDataSource);
        Statement preparedStatement = Mockito.mock(Statement.class);
        Mockito.when(connection.createStatement()).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery(Mockito.anyString())).thenThrow(new SQLException("Table " +
                "\"AUTH_OAUTH2_CLIENTS\" not found"));
        Field field = DAOUtil.class.getDeclaredField("authDataSource");
        field.setAccessible(true);
        field.set(DAOUtil.class, dataSource);
        log.info("Data sources initialized");
    }

    @Test
    public void testDAOException() throws Exception {

        final String sql = "SELECT * FROM AUTH_OAUTH2_CLIENTS";
        try (Connection connection = DAOUtil.getAuthConnection();
             Statement statement = connection.createStatement();
             ResultSet ignored = statement.executeQuery(sql)) {
            //cannot reach this statement
            Assert.fail();
        } catch (SQLException e) {
            Assert.assertTrue(e.getMessage().contains("Table \"AUTH_OAUTH2_CLIENTS\" not found"));
            log.debug("Expected exception while executing query", e);
        }
    }
}
