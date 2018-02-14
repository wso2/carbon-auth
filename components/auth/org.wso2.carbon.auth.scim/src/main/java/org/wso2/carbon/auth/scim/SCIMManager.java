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
        init();
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
    
    private void init() {
        // register endpoint URLs in AbstractResourceEndpoint since they are called with in the API
        registerEndpointURLs();
        // register the charon related configurations
        registerCharonConfig();
    }
    
    public CarbonAuthSCIMUserManager getCarbonAuthSCIMUserManager() throws AuthUserManagementException {
        //TODO : CarbonAuthSCIMUserManager should be initialized with UserManagement API
        UserStoreConnector userStoreConnector;
        try {
            userStoreConnector = UserStoreConnectorFactory.getUserStoreConnector();
            return new CarbonAuthSCIMUserManager(userStoreConnector);
        } catch (UserStoreConnectorException e) {
            throw new AuthUserManagementException("User manager initialization failed");
        }
    }
    
    /*
     * Resgister endpoint URLs in AbstractResourceEndpoint.
     */
    private void registerEndpointURLs() {
        if (endpointURLs != null && !endpointURLs.isEmpty()) {
            AbstractResourceManager.setEndpointURLMap(endpointURLs);
        }
    }
    
    /*
     * This create the basic operational configurations for charon
     */
    private void registerCharonConfig() {
        //config charon
        //this values will be used in /ServiceProviderConfigResource endpoint
        CharonConfiguration.getInstance().setDocumentationURL(SCIMCommonConstants.DOCUMENTATION_URL);
        CharonConfiguration.getInstance().setBulkSupport(false,
                SCIMCommonConstants.MAX_OPERATIONS,
                SCIMCommonConstants.MAX_PAYLOAD_SIZE);
        CharonConfiguration.getInstance().setSortSupport(false);
        CharonConfiguration.getInstance().setETagSupport(false);
        CharonConfiguration.getInstance().setChangePasswordSupport(true);
        CharonConfiguration.getInstance().setFilterSupport(true, SCIMCommonConstants.MAX_RESULTS);
        CharonConfiguration.getInstance().setPatchSupport(false);
        CharonConfiguration.getInstance().setCountValueForPagination(SCIMCommonConstants.COUNT_FOR_PAGINATION);

        Object[] auth1 = {SCIMCommonConstants.AUTHENTICATION_SCHEMES_NAME_1,
                SCIMCommonConstants.AUTHENTICATION_SCHEMES_DESCRIPTION_1,
                SCIMCommonConstants.AUTHENTICATION_SCHEMES_SPEC_URI_1,
                SCIMCommonConstants.AUTHENTICATION_SCHEMES_DOCUMENTATION_URL_1,
                SCIMCommonConstants.AUTHENTICATION_SCHEMES_TYPE_1,
                SCIMCommonConstants.AUTHENTICATION_SCHEMES_PRIMARY_1};
        ArrayList<Object[]> authList = new ArrayList<Object[]>();
        authList.add(auth1);
        CharonConfiguration.getInstance().setAuthenticationSchemes(authList);
    }

}
