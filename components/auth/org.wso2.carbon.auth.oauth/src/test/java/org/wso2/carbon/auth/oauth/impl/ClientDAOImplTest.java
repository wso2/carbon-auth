package org.wso2.carbon.auth.oauth.impl;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.testng.Assert;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DAOUtil.class })
public class ClientDAOImplTest {
    @Mock
    Connection connection;
    @Mock
    PreparedStatement statement;
    @Mock
    ResultSet resultSet;
    String authCode = "authcode";
    String clientId = "clientId";
    String clientSec = "clientSec";
    String scope = "scope";
    URI redirectUri;
    ClientDAO dao;

    @Before
    public void init() throws Exception {
        redirectUri = new URI("http://host");
        dao = DAOFactory.getClientDAO();
        PowerMockito.mockStatic(DAOUtil.class);
    }

    @Test
    public void testGetRedirectUri() throws Exception {
        PowerMockito.when(DAOUtil.getAuthConnection()).thenReturn(connection);
        PowerMockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(statement);
        PowerMockito.when(statement.executeQuery()).thenReturn(resultSet);
        try {
            dao.getRedirectUri(clientId);
        } catch (ClientDAOException e) {
            Assert.fail("exception not expected");
        }

        PowerMockito.when(statement.executeQuery()).thenThrow(SQLException.class);
        try {
            dao.getRedirectUri(clientId);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }

        PowerMockito.when(DAOUtil.getAuthConnection()).thenThrow(SQLException.class);
        try {
            dao.getRedirectUri(clientId);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }
    }

    @Test
    public void testAddAuthCodeInfo() throws Exception {
        PowerMockito.when(DAOUtil.getAuthConnection()).thenReturn(connection);
        PowerMockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(statement);
        try {
            dao.addAuthCodeInfo(authCode, clientId, scope, redirectUri);
        } catch (ClientDAOException e) {
            Assert.fail("exception not expected");
        }

        PowerMockito.when(statement.execute()).thenThrow(SQLException.class);
        try {
            dao.addAuthCodeInfo(authCode, clientId, scope, redirectUri);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }

        PowerMockito.when(DAOUtil.getAuthConnection()).thenThrow(SQLException.class);
        try {
            dao.addAuthCodeInfo(authCode, clientId, scope, redirectUri);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }
    }

    @Test
    public void testGetScopeForAuthCode() throws Exception {
        PowerMockito.when(DAOUtil.getAuthConnection()).thenReturn(connection);
        PowerMockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(statement);
        PowerMockito.when(statement.executeQuery()).thenReturn(resultSet);
        try {
            dao.getScopeForAuthCode(authCode, clientId, redirectUri);
        } catch (ClientDAOException e) {
            Assert.fail("exception not expected");
        }

        try {
            dao.getScopeForAuthCode(authCode, clientId, null);
        } catch (ClientDAOException e) {
            Assert.fail("exception not expected");
        }

        PowerMockito.when(statement.executeQuery()).thenThrow(SQLException.class);
        try {
            dao.getScopeForAuthCode(authCode, clientId, redirectUri);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }

        PowerMockito.when(DAOUtil.getAuthConnection()).thenThrow(SQLException.class);
        try {
            dao.getScopeForAuthCode(authCode, clientId, redirectUri);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }
    }

    @Test
    public void testIsClientCredentialsValid() throws Exception {
        PowerMockito.when(DAOUtil.getAuthConnection()).thenReturn(connection);
        PowerMockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(statement);
        PowerMockito.when(statement.executeQuery()).thenThrow(SQLException.class);
        try {
            dao.isClientCredentialsValid(clientId, clientSec);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }

        PowerMockito.when(DAOUtil.getAuthConnection()).thenThrow(SQLException.class);
        try {
            dao.isClientCredentialsValid(clientId, clientSec);
            Assert.fail("exception expected");
        } catch (ClientDAOException e) {
        }
    }

}
