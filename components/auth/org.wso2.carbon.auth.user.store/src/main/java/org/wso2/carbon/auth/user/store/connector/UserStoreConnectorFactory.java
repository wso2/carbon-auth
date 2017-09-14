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

import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.jdbc.JDBCUserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;

/**
 * Factory class to create user store connector
 *
 */
public class UserStoreConnectorFactory {
    
    public static UserStoreConnector getUserStoreConnector() {
        UserStoreConfiguration userStoreConfiguration = ServiceReferenceHolder.getInstance().
                getAuthConfiguration().getUserStoreConfiguration();
        if (UserStoreConstants.JDBC_CONNECTOR_TYPE.equals(userStoreConfiguration.getConnectorType())) {
            return new JDBCUserStoreConnector();
        }
        return new JDBCUserStoreConnector();
    }

}
