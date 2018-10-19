/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.wso2.carbon.auth.user.store.claim.ClaimConstants;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.connector.ldap.LDAPUserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.util.UserStoreUtil;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LDAPUserStoreManagerTest {
    @Mock
    LDAPUserStoreConnector connector;
    String userName = "admin";
    String password = "admin";
    String userId = "asdadasdsa12131";
    String usernameAttrName = "uid";

    @Before
    public void init() throws Exception {
        System.setProperty(ClaimConstants.CARBON_RUNTIME_DIR_PROP_NAME,
                System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                        + "resources" + File.separator + "runtime.home" + File.separator);
        connector = Mockito.mock(LDAPUserStoreConnector.class);
        Mockito.when(connector.getConnectorUserId(usernameAttrName, userName)).thenReturn(userId);

        Map info = new HashMap();

        int iterationCount = 4096;
        int keyLength = 256;
        String hashAlgo = "SHA256";
        PasswordHandler passwordHandler = new DefaultPasswordHandler();
        passwordHandler.setIterationCount(iterationCount);
        passwordHandler.setKeyLength(keyLength);
        String salt = UserStoreUtil.generateUUID();
        String hashedPassword;
        char[] pass = password.toCharArray();
        try {
            hashedPassword = passwordHandler.hashPassword(pass, salt, hashAlgo);
        } catch (NoSuchAlgorithmException e) {
            throw new UserStoreConnectorException("Error while hashing the password.", e);
        }

        info.put(UserStoreConstants.PASSWORD, hashedPassword);
        info.put(UserStoreConstants.PASSWORD_SALT, salt);
        info.put(UserStoreConstants.HASH_ALGO, "SHA256");
        info.put(UserStoreConstants.ITERATION_COUNT, iterationCount);
        info.put(UserStoreConstants.KEY_LENGTH, keyLength);

        Mockito.when(connector.getUserPasswordInfo(userId)).thenReturn(info);
    }

    @Test
    public void testDoAuthenticate() throws Exception {
        LDAPUserStoreManager manager = new LDAPUserStoreManager(connector);
        boolean isAutheticated = manager.doAuthenticate(userName, password);
        Assert.assertTrue(isAutheticated);
    }
}
