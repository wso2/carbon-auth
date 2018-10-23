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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.store.claim.ClaimMetadataStore;
import org.wso2.carbon.auth.user.store.claim.DefaultClaimManager;
import org.wso2.carbon.auth.user.store.claim.DefaultClaimMetadataStore;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Managing JDBC user store functions
 */
public class JDBCUserStoreManager implements UserStoreManager {
    private static final Logger log = LoggerFactory.getLogger(JDBCUserStoreManager.class);
    private UserStoreConnector userStoreConnector;
    private PasswordHandler passwordHandler;
    private ClaimMetadataStore claimMetadataStore;

    public JDBCUserStoreManager() throws UserStoreException {
        try {
            this.userStoreConnector = UserStoreConnectorFactory.getUserStoreConnector();
            DefaultClaimManager defaultClaimManager = DefaultClaimManager.getInstance();
            this.claimMetadataStore = new DefaultClaimMetadataStore(defaultClaimManager);
        } catch (UserStoreConnectorException e) {
            throw new UserStoreException("Error while initializing JDBC user store connector", e);
        }
        passwordHandler = new DefaultPasswordHandler();
    }

    protected JDBCUserStoreManager(UserStoreConnector userStoreConnector, PasswordHandler passwordHandler) {
        this.userStoreConnector = userStoreConnector;
        this.passwordHandler = passwordHandler;
        DefaultClaimManager defaultClaimManager = DefaultClaimManager.getInstance();
        this.claimMetadataStore = new DefaultClaimMetadataStore(defaultClaimManager);
    }

    @Override
    public boolean doAuthenticate(String userName, Object credential) throws UserStoreException {

        try {
            String password = (String) credential;
            String uid = claimMetadataStore.getAttributeName(UserStoreConstants.CLAIM_USERNAME);
            String userId = userStoreConnector.getConnectorUserId(uid, userName);
            Map info = userStoreConnector.getUserPasswordInfo(userId);


            passwordHandler.setIterationCount((int) info.get(UserStoreConstants.ITERATION_COUNT));
            passwordHandler.setKeyLength((int) info.get(UserStoreConstants.KEY_LENGTH));
            String hashedPassword = passwordHandler.hashPassword(password.toCharArray(), (String) info.get
                            (UserStoreConstants.PASSWORD_SALT), (String) info.get(UserStoreConstants.HASH_ALGO));

            if (hashedPassword.equals(info.get(UserStoreConstants.PASSWORD))) {
                return true;
            } else {
                return false;
            }

        } catch (UserStoreConnectorException e) {
            throw new UserStoreException("User Connector exception occurred", e);
        } catch (UserNotFoundException e) {
            throw new UserStoreException("User not found exception occurred", e);
        } catch (NoSuchAlgorithmException e) {
            throw new UserStoreException("No such algorithm exception occurred", e);
        }
    }

    @Override
    public List<String> getRoleListOfUser(String userName) throws UserStoreException {

        try {
            String uid = claimMetadataStore.getAttributeName(UserStoreConstants.CLAIM_USERNAME);
            String userId = userStoreConnector.getConnectorUserId(uid, userName);
            return userStoreConnector.getGroupsOfUser(userId);
        } catch (UserNotFoundException e) {
            throw new UserStoreException("User not found exception occurred", e);
        } catch (UserStoreConnectorException e) {
            throw new UserStoreException("User Connector exception occurred", e);
        }

    }

}
