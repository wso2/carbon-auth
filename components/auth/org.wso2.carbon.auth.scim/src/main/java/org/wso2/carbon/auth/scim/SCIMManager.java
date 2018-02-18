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

package org.wso2.carbon.auth.scim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.impl.CarbonAuthSCIMUserManager;
import org.wso2.carbon.auth.scim.impl.constants.SCIMCommonConstants;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles charon initialization
 *
 */
public class SCIMManager {
    private static Logger log = LoggerFactory.getLogger(SCIMManager.class);
    private static volatile SCIMManager carbonAuthSCIMManager;
    private static Map<String, String> endpointURLs = new HashMap<String, String>();
    
    private SCIMManager() {
    }
    
    public static SCIMManager getInstance() {
        if (carbonAuthSCIMManager == null) {
            synchronized (SCIMManager.class) {
                if (carbonAuthSCIMManager == null) {
                    carbonAuthSCIMManager = new SCIMManager();
                    return carbonAuthSCIMManager;
                } else {
                    return carbonAuthSCIMManager;
                }
            }
        } else {
            return carbonAuthSCIMManager;
        }
    }
    
    public CarbonAuthSCIMUserManager getCarbonAuthSCIMUserManager() throws AuthUserManagementException {
        //TODO : CarbonAuthSCIMUserManager should be initialized with UserManagement API
        UserStoreConnector userStoreConnector;
        try {
            userStoreConnector = UserStoreConnectorFactory.getUserStoreConnector();
            return new CarbonAuthSCIMUserManager(userStoreConnector);
        } catch (UserStoreConnectorException e) {
            throw new AuthUserManagementException("User manager initialization failed", e);
        }
    }

}
