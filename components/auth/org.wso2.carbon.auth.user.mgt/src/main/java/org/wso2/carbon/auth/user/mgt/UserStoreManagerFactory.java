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
package org.wso2.carbon.auth.user.mgt;

import org.wso2.carbon.auth.user.mgt.impl.JDBCUserStoreManager;
import org.wso2.carbon.auth.user.mgt.impl.LDAPUserStoreManager;
import org.wso2.carbon.auth.user.mgt.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;

/**
 * Factory class for user store manager
 */
public class UserStoreManagerFactory {
    public static UserStoreManager getUserStoreManager() throws UserStoreException {
        UserStoreConfiguration userStoreConfiguration = ServiceReferenceHolder.getInstance().
                getUserStoreConfigurationService().getUserStoreConfiguration();
        if (UserStoreConstants.JDBC_CONNECTOR_TYPE.equals(userStoreConfiguration.getConnectorType())) {
            return new JDBCUserStoreManager();
        } else if (UserStoreConstants.LDAP_CONNECTOR_TYPE.equals(userStoreConfiguration.getConnectorType())) {
            return new LDAPUserStoreManager();
        }
        return new JDBCUserStoreManager();
    }
}
