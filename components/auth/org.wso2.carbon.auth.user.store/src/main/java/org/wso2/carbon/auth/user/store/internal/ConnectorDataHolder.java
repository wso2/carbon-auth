/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.user.store.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.util.UserStoreUtil;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.exception.DataSourceException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.security.auth.callback.PasswordCallback;
import javax.sql.DataSource;


/**
 * Database related utility methods.
 */
public class ConnectorDataHolder {
    private static Logger log = LoggerFactory.getLogger(ConnectorDataHolder.class);
    private static final ConnectorDataHolder instance = new ConnectorDataHolder();

    private DataSourceService dataSourceService;

    private ConnectorDataHolder() {
        super();
    }

    public static ConnectorDataHolder getInstance() {
        return instance;
    }

    public DataSource getDataSource(String dataSourceName) throws DataSourceException {

        if (dataSourceService == null) {
            throw new RuntimeException("Datasource service is null. Cannot retrieve data source");
        }
        return (DataSource) dataSourceService.getDataSource(dataSourceName);
    }

    public void setDataSourceService(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
        UserStoreConfiguration config = ServiceReferenceHolder.getInstance().getAuthConfiguration()
                .getUserStoreConfiguration();
        if (UserStoreConstants.JDBC_CONNECTOR_TYPE.equals(config.getConnectorType())) {
            if (dataSourceService == null) {
                log.warn("Admin user adding skip, since datasource is not configured.");
                return;
            }
        }

        //adding default admin user
        char[] password = config.getSuperUserPass().toCharArray();
        String user = config.getSuperUser();
        UserStoreConnector connector = UserStoreConnectorFactory.getUserStoreConnector();

        try {
            connector.init(config);
        } catch (UserStoreConnectorException e) {
            log.error("Error occurred while init UserStoreConnector", e);
            return;
        }
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute(UserStoreConstants.CLAIM_USERNAME, user));
        attributeList.add(new Attribute(UserStoreConstants.CLAIM_ID, UserStoreUtil.generateUUID()));
        try {
            String uid = connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, user);
            if (uid != null) {
                log.debug("default user already exist");
                return;
            }
        } catch (UserNotFoundException e) {
            //not logging exception since, this code is to handler default user populating logic
            //when user not exist
            log.warn("default user not exist");
        } catch (UserStoreConnectorException e) {
            log.error("Error checking existing admin user", e);
        }

        try {
            String userId = connector.addUser(attributeList);
            PasswordCallback passwordCallback = new PasswordCallback(UserStoreConstants.PASSWORD_URI, false);
            passwordCallback.setPassword(password);
            connector.addCredential(userId, passwordCallback);
            log.debug("added default user admin");
        } catch (UserStoreConnectorException e) {
            log.error("Error adding admin user", e);
        }
    }

}
