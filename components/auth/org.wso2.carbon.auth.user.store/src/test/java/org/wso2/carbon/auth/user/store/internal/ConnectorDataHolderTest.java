/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.auth.user.store.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.jdbc.JDBCUserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.exception.DataSourceException;

import javax.security.auth.callback.PasswordCallback;
import javax.sql.DataSource;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserStoreConnectorFactory.class })
public class ConnectorDataHolderTest {
    private DataSourceService dataSourceService;
    private ServiceReferenceHolder holder;
    private UserStoreConnector connector;
    private String user = "admin";
    private String userId = "123456";
    private DataSource dataSource;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(UserStoreConnectorFactory.class);

        holder = ServiceReferenceHolder.getInstance();
        dataSourceService = Mockito.mock(DataSourceService.class);
        dataSource = Mockito.mock(DataSource.class);
        connector = Mockito.mock(JDBCUserStoreConnector.class);

        Mockito.when(UserStoreConnectorFactory.getUserStoreConnector()).thenReturn(connector);
        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, user)).thenReturn(userId);
        Mockito.when(dataSourceService.getDataSource(Constants.DATASOURCE_WSO2UM_DB)).thenReturn(dataSource);
        holder.setDataSourceService(dataSourceService);
    }

    @Test
    public void testSetDataSourceService() throws Exception {
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();

        PowerMockito.when(connector, "init", Mockito.any(UserStoreConfiguration.class))
                .thenThrow(UserStoreConnectorException.class);
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();
    }

    private void checkAssertDataSource() throws DataSourceException {
        DataSource source = (DataSource) holder.getDataSourceService().getDataSource(Constants.DATASOURCE_WSO2UM_DB);
        Assert.assertEquals(dataSource, source);
    }

    @Test
    public void testSetDataSourceServiceUserStoreConnectorException() throws Exception {
        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, user)).thenReturn(null);
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();

        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, user))
                .thenThrow(UserStoreConnectorException.class);
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();
    }

    @Test
    public void testSetDataSourceServiceUserNotFoundException() throws Exception {
        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, user))
                .thenThrow(UserNotFoundException.class);
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();
    }

    @Test
    public void testSetDataSourceServiceAddCredential() throws Exception {
        Mockito.when(connector.addCredential(Mockito.any(String.class), Mockito.any(PasswordCallback.class)))
                .thenThrow(UserStoreConnectorException.class);
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();
    }

    @Test
    public void testGetDataSource() throws Exception {
        holder.setDataSourceService(dataSourceService);
        checkAssertDataSource();
    }
}
