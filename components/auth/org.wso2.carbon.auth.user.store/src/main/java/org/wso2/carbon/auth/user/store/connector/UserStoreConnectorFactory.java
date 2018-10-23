/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.user.store.connector;

import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.jdbc.JDBCUserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.ldap.LDAPUserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder;

/**
 * Factory class to create user store connector
 */
public class UserStoreConnectorFactory {

    public static UserStoreConnector getUserStoreConnector() throws UserStoreConnectorException {
        UserStoreConfiguration userStoreConfiguration = ServiceReferenceHolder.getInstance()
                .getUserStoreConfigurationService().getUserStoreConfiguration();
        UserStoreConnector connector;
        if (UserStoreConstants.JDBC_CONNECTOR_TYPE.equals(userStoreConfiguration.getConnectorType())) {
            connector = new JDBCUserStoreConnector();
        } else if (UserStoreConstants.LDAP_CONNECTOR_TYPE.equals(userStoreConfiguration.getConnectorType())) {
            connector = new LDAPUserStoreConnector();
        } else {
            throw new UserStoreConnectorException("User store connector type is not defined in configuration");
        }
        connector.init(userStoreConfiguration);
        return connector;

    }

}
