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

package org.wso2.carbon.auth.client.registration.impl;

import com.zaxxer.hikari.HikariDataSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.SampleTestObjectCreator;
import org.wso2.carbon.auth.client.registration.dto.ClientRegistrationResponse;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.core.datasource.DataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientRegistrationHandlerImplExceptionTest {

    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationHandlerImplExceptionTest.class);
    private static final String DUMMY_CLIENT_ID = "some_dummy_client_id";

    @BeforeMethod
    public void init() throws Exception {

        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        HikariDataSource hikariDataSource = Mockito.mock(HikariDataSource.class);
        Mockito.when(dataSource.getDatasource()).thenReturn(hikariDataSource);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.execute()).thenThrow(SQLException.class);
        Mockito.when(preparedStatement.executeQuery()).thenThrow(SQLException.class);
        Field field = DAOUtil.class.getDeclaredField("authDataSource");
        field.setAccessible(true);
        field.set(DAOUtil.class, dataSource);
        log.info("Data sources initialized");
    }

    @Test
    public void testAddApplicationUnsuccessful() throws Exception {
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
        Application application = SampleTestObjectCreator.createDefaultApplication();
        ClientRegistrationResponse registrationResponse = handler.registerApplication(application);
        Assert.assertFalse(registrationResponse.isSuccessful());
        Assert.assertNotNull(registrationResponse.getErrorObject());
    }

    @Test
    public void testGetApplicationUnsuccessful() throws Exception {
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
        ClientRegistrationResponse getResponse = handler.getApplication(DUMMY_CLIENT_ID);
        Assert.assertFalse(getResponse.isSuccessful());
        Assert.assertNotNull(getResponse.getErrorObject());
    }

    @Test
    public void testUpdateApplicationUnsuccessful() throws Exception {
        Application application = SampleTestObjectCreator.createDefaultApplication();
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
        ClientRegistrationResponse updateResponse = handler.updateApplication(DUMMY_CLIENT_ID, application);
        Assert.assertFalse(updateResponse.isSuccessful());
        Assert.assertNotNull(updateResponse.getErrorObject());
    }

    @Test
    public void testDeleteApplicationUnsuccessful() throws Exception {
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
        ClientRegistrationResponse deleteResponse = handler.deleteApplication(DUMMY_CLIENT_ID);
        Assert.assertFalse(deleteResponse.isSuccessful());
        Assert.assertNotNull(deleteResponse.getErrorObject());
    }
}
