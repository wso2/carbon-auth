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
package org.wso2.carbon.auth.oauth.impl;

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
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DAOUtil.class})
public class TokenDAOImplTest {
    @Mock
    Connection connection;
    @Mock
    PreparedStatement statement;
    @Mock
    ResultSet resultSet;
    URI redirectUri;
    OAuthDAO dao;

    @Before
    public void init() throws Exception {
        redirectUri = new URI("http://host");
        dao = DAOFactory.getClientDAO();
        PowerMockito.mockStatic(DAOUtil.class);
    }

    @Test
    public void testGetTokenInfo() throws Exception {
        String accessToken = UUID.randomUUID().toString();

        PowerMockito.when(DAOUtil.getAuthConnection()).thenReturn(connection);
        PowerMockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(statement);
        PowerMockito.when(statement.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(false);
        try {
            dao.getTokenInfo(accessToken);
        } catch (OAuthDAOException e) {
            Assert.fail("exception not expected");
        }

        PowerMockito.when(statement.executeQuery()).thenThrow(SQLException.class);
        try {
            dao.getTokenInfo(accessToken);
            Assert.fail("exception expected");
        } catch (OAuthDAOException e) {
        }

        PowerMockito.when(DAOUtil.getAuthConnection()).thenThrow(SQLException.class);
        try {
            dao.getTokenInfo(accessToken);
            Assert.fail("exception expected");
        } catch (OAuthDAOException e) {
        }
    }

    @Test
    public void testGetTokenInfoFromTokenAndCK() throws Exception {
        String accessToken = UUID.randomUUID().toString();
        String consumerKey = UUID.randomUUID().toString();

        PowerMockito.when(DAOUtil.getAuthConnection()).thenReturn(connection);
        PowerMockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(statement);
        PowerMockito.when(statement.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(false);
        try {
            dao.getTokenInfo(accessToken, consumerKey);
        } catch (OAuthDAOException e) {
            Assert.fail("exception not expected");
        }

        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getTimestamp(Mockito.anyString(), Mockito.any(Calendar.class)))
                .thenReturn(Timestamp.from(Instant.now()));
        PowerMockito.when(resultSet.getTimestamp(Mockito.anyString(), Mockito.any(Calendar
                .class))).thenReturn(Timestamp.from(Instant.now()));

        try {
            AccessTokenDTO accessTokenDTO = dao.getTokenInfo(accessToken, consumerKey);
            Assert.assertNotNull(accessTokenDTO);
        } catch (OAuthDAOException e) {
            Assert.fail("exception not expected");
        }

        PowerMockito.when(statement.executeQuery()).thenThrow(SQLException.class);
        try {
            dao.getTokenInfo(accessToken, consumerKey);
            Assert.fail("exception expected");
        } catch (OAuthDAOException e) {
        }

        PowerMockito.when(DAOUtil.getAuthConnection()).thenThrow(SQLException.class);
        try {
            dao.getTokenInfo(accessToken, consumerKey);
            Assert.fail("exception expected");
        } catch (OAuthDAOException e) {
        }
    }
}
