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
package org.wso2.carbon.auth.user.mgt.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserStoreConnectorFactory.class, SecretKeyFactory.class })
public class JDBCUserStoreManagerTest {
    UserStoreManager userStoreManager;
    @Mock
    UserStoreConnector connector;
    @Mock
    DefaultPasswordHandler defaultPasswordHandler;
    @Mock
    SecretKeyFactory secretKeyFactory;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(UserStoreConnectorFactory.class);
        PowerMockito.when(UserStoreConnectorFactory.getUserStoreConnector()).thenReturn(connector);
        userStoreManager = new JDBCUserStoreManager();
        Assert.assertNotNull(userStoreManager);

        PowerMockito.when(connector, "init", Mockito.any(UserStoreConfiguration.class))
                .thenThrow(UserStoreConnectorException.class);
        userStoreManager = new JDBCUserStoreManager();
        Assert.assertNotNull(userStoreManager);
    }

    @Test
    public void testDoAuthenticate() throws Exception {
        boolean authenticated;
        String hashedPass = "a1A1s2S2";
        String username = "admin";
        String password = "admin";
        String userId = "admin@123";
        Map info = PowerMockito.mock(HashMap.class);
        PowerMockito.when(info.get(UserStoreConstants.PASSWORD)).thenReturn(hashedPass);
        PowerMockito.when(info.get(UserStoreConstants.ITERATION_COUNT)).thenReturn(1);
        PowerMockito.when(info.get(UserStoreConstants.KEY_LENGTH)).thenReturn(1);
        PowerMockito.when(info.get(UserStoreConstants.PASSWORD_SALT)).thenReturn("PASSWORD_SALT");
        PowerMockito.when(info.get(UserStoreConstants.HASH_ALGO)).thenReturn("HASH_ALGO");

        PowerMockito.mockStatic(SecretKeyFactory.class);
        PowerMockito.when(SecretKeyFactory.getInstance(Mockito.anyString())).thenReturn(secretKeyFactory);
        byte[] decoded = Base64.getDecoder().decode(hashedPass);
        SecretKey secretKey = PowerMockito.mock(SecretKey.class);
        PowerMockito.when(secretKey.getEncoded()).thenReturn(decoded);
        PowerMockito.when(secretKeyFactory.generateSecret(Mockito.any(java.security.spec.KeySpec.class)))
                .thenReturn(secretKey);

        PowerMockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, username)).thenReturn(userId);
        PowerMockito.when(connector.getUserPasswordInfo(userId)).thenReturn(info);

        //correct pass
        authenticated = userStoreManager.doAuthenticate(username, password);
        Assert.assertTrue(authenticated);

        //wrong pass
        PowerMockito.when(info.get(UserStoreConstants.PASSWORD)).thenReturn("wrongPass");
        authenticated = userStoreManager.doAuthenticate(username, password);
        Assert.assertFalse(authenticated);

        //when NoSuchAlgorithmException occurred
        PowerMockito.when(SecretKeyFactory.getInstance(Mockito.anyString())).thenThrow(NoSuchAlgorithmException.class);
        try {
            authenticated = userStoreManager.doAuthenticate(username, password);
            Assert.fail("exception expected");
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getCause() instanceof NoSuchAlgorithmException);
        }

        //when UserStoreConnectorException occurred
        PowerMockito.when(connector.getUserPasswordInfo(userId)).thenThrow(UserStoreConnectorException.class);
        try {
            authenticated = userStoreManager.doAuthenticate(username, password);
            Assert.fail("exception expected");
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getCause() instanceof UserStoreConnectorException);
        }

        //when UserNotFoundException occurred
        PowerMockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, username))
                .thenThrow(UserNotFoundException.class);
        try {
            authenticated = userStoreManager.doAuthenticate(username, password);
            Assert.fail("exception expected");
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getCause() instanceof UserNotFoundException);
        }
    }
}
