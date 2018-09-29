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

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JDBCUserStoreManagerTest {

    UserStoreManager userStoreManager;
    UserStoreConnector connector;
    PasswordHandler defaultPasswordHandler;

    @BeforeMethod
    public void init() {

        connector = Mockito.mock(UserStoreConnector.class);
        defaultPasswordHandler = Mockito.mock(PasswordHandler.class);
        userStoreManager = new JDBCUserStoreManager(connector, defaultPasswordHandler);
        Assert.assertNotNull(userStoreManager);
    }

    @Test
    public void testDoAuthenticate() throws Exception {

        boolean authenticated;
        String hashedPass = "a1A1s2S2";
        String username = "admin";
        String password = "admin";
        String userId = "admin@123";
        Map info = new HashMap();
        info.put(UserStoreConstants.PASSWORD, hashedPass);
        info.put(UserStoreConstants.ITERATION_COUNT, 1);
        info.put(UserStoreConstants.KEY_LENGTH, 1);
        info.put(UserStoreConstants.PASSWORD_SALT, "PASSWORD_SALT");
        info.put(UserStoreConstants.HASH_ALGO, "HASH_ALGO");
        Mockito.when(defaultPasswordHandler.hashPassword(password.toCharArray(), (String) info.get(UserStoreConstants
                .PASSWORD_SALT), (String) info.get(UserStoreConstants.HASH_ALGO))).thenReturn(hashedPass);
        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, username)).thenReturn(userId);
        Mockito.when(connector.getUserPasswordInfo(userId)).thenReturn(info);

        //correct pass
        authenticated = userStoreManager.doAuthenticate(username, password);
        Assert.assertTrue(authenticated);

        //wrong pass
        Mockito.when(info.get(UserStoreConstants.PASSWORD)).thenReturn("wrongPass");
        authenticated = userStoreManager.doAuthenticate(username, password);
        Assert.assertFalse(authenticated);

        Mockito.when(defaultPasswordHandler.hashPassword(password.toCharArray(), (String) info.get(UserStoreConstants
                .PASSWORD_SALT), (String) info.get(UserStoreConstants.HASH_ALGO))).thenThrow(new
                NoSuchAlgorithmException(""));
        try {
            authenticated = userStoreManager.doAuthenticate(username, password);
            Assert.fail("exception expected");
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getCause() instanceof NoSuchAlgorithmException);
        }

        //when UserStoreConnectorException occurred
        Mockito.when(connector.getUserPasswordInfo(userId)).thenThrow(UserStoreConnectorException.class);
        try {
            authenticated = userStoreManager.doAuthenticate(username, password);
            Assert.fail("exception expected");
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getCause() instanceof UserStoreConnectorException);
        }

        //when UserNotFoundException occurred
        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, username))
                .thenThrow(UserNotFoundException.class);
        try {
            authenticated = userStoreManager.doAuthenticate(username, password);
            Assert.fail("exception expected");
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getCause() instanceof UserNotFoundException);
        }
    }

    @Test
    public void testGetRoleListOfUser() throws UserStoreConnectorException, UserNotFoundException, UserStoreException {

        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin")).thenReturn("1234");
        Mockito.when(connector.getGroupsOfUser("1234")).thenReturn(Arrays.asList("1"));
        userStoreManager.getRoleListOfUser("admin");
        Mockito.verify(connector, Mockito.times(1)).getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin");
        Mockito.verify(connector, Mockito.times(1)).getGroupsOfUser("1234");
    }

    @Test
    public void testGetRoleListOfUserUserNotfoundException() throws UserStoreConnectorException,
            UserNotFoundException {

        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin1")).thenThrow(new
                UserNotFoundException(""));
        try {
            userStoreManager.getRoleListOfUser("admin1");
            Assert.fail();
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getMessage().contains("User not found exception occurred"));
        }
        Mockito.verify(connector, Mockito.times(1)).getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin1");
        Mockito.verify(connector, Mockito.times(0)).getGroupsOfUser(Mockito.anyString());
    }

    @Test
    public void testGetRoleListOfUserStoreException() throws UserStoreConnectorException,
            UserNotFoundException {

        Mockito.when(connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin1")).thenThrow(new
                UserStoreConnectorException(""));
        try {
            userStoreManager.getRoleListOfUser("admin1");
            Assert.fail();
        } catch (UserStoreException e) {
            Assert.assertTrue(e.getMessage().contains("User Connector exception occurred"));
        }
        Mockito.verify(connector, Mockito.times(1)).getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin1");
        Mockito.verify(connector, Mockito.times(0)).getGroupsOfUser(Mockito.anyString());
    }

}
