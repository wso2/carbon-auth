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
package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.core.datasource.DataSource;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DAOFactory.class })
public class TokenApiServiceFactoryTest {
    @Test
    public void getTokenApiTest() throws Exception {
        TokenApiServiceFactory tokenApiServiceFactory = new TokenApiServiceFactory();
        Assert.assertNotNull(tokenApiServiceFactory);

        PowerMockito.mockStatic(DAOFactory.class);
        TokenDAO tokenDAO = PowerMockito.mock(TokenDAO.class);
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        DatabaseMetaData metaData = Mockito.mock(DatabaseMetaData.class);

        PowerMockito.when(DAOFactory.getTokenDAO()).thenReturn(tokenDAO);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getDriverName()).thenReturn("H2");
        DAOUtil.initializeAuthDataSource(dataSource);

        TokenApiService tokenApiService = TokenApiServiceFactory.getTokenApi();
        Assert.assertNotNull(tokenApiService);

        PowerMockito.when(DAOFactory.getClientDAO()).thenThrow(OAuthDAOException.class);
        try {
            tokenApiService = TokenApiServiceFactory.getTokenApi();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
        }
    }
}
