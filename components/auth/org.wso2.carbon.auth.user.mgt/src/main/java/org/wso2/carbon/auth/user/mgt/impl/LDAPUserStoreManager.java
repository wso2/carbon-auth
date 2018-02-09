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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Managing LDAP user store functions
 */
public class LDAPUserStoreManager implements UserStoreManager {
    private static Logger log = LoggerFactory.getLogger(LDAPUserStoreManager.class);
    private UserStoreConnector userStoreConnector;

    public LDAPUserStoreManager() throws UserStoreException {
        try {
            this.userStoreConnector = UserStoreConnectorFactory.getUserStoreConnector();
        } catch (UserStoreConnectorException e) {
            throw new UserStoreException("Error while initializing LDAP user store connector", e);
        }
    }

    public LDAPUserStoreManager(UserStoreConnector userStoreConnector) {
        this.userStoreConnector = userStoreConnector;
    }

    @Override
    public boolean doAuthenticate(String userName, Object credential) throws UserStoreException {
        try {
            String password = (String) credential;
            String userId = userStoreConnector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, userName);
            Map info = userStoreConnector.getUserPasswordInfo(userId);
            PasswordHandler passwordHandler = new DefaultPasswordHandler();
            passwordHandler.setIterationCount((int) info.get(UserStoreConstants.ITERATION_COUNT));
            passwordHandler.setKeyLength((int) info.get(UserStoreConstants.KEY_LENGTH));
            String hashedPassword = passwordHandler
                    .hashPassword(password.toCharArray(), (String) info.get(UserStoreConstants.PASSWORD_SALT),
                            (String) info.get(UserStoreConstants.HASH_ALGO));

            if (hashedPassword.equals(info.get(UserStoreConstants.PASSWORD))) {
                return true;
            } else {
                return false;
            }

        } catch (UserStoreConnectorException e) {
            log.error("User Connector exception occurred", e);
            throw new UserStoreException("User Connector exception occurred", e);
        } catch (UserNotFoundException e) {
            log.error("User not found exception occurred", e);
            throw new UserStoreException("User not found exception occurred", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm exception occurred", e);
            throw new UserStoreException("No such algorithm exception occurred", e);
        }
    }
}
