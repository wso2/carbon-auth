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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.mgt.UserStoreManagerFactory;
import org.wso2.carbon.auth.user.mgt.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.jdbc.JDBCUserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.ldap.LDAPUserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceReferenceHolder.class, UserStoreConnectorFactory.class })
public class UserStoreManagerFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserStoreManagerFactoryTest.class);

    @Mock
    ServiceReferenceHolder serviceReferenceHolder;
    @Mock
    JDBCUserStoreConnector jdbcUserStoreConnector;
    @Mock
    LDAPUserStoreConnector ldapUserStoreConnector;
    @Mock
    UserStoreConfiguration userStoreConfiguration;
    
    @Before
    public void init() {
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(UserStoreConnectorFactory.class);

        UserStoreConfigurationService configurationService = new UserStoreConfigurationService(userStoreConfiguration);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getUserStoreConfigurationService())
                .thenReturn(configurationService);
    }

    @Test
    public void testGetUserStoreManager() throws Exception {
        PowerMockito.when(userStoreConfiguration.getConnectorType()).thenReturn(UserStoreConstants.JDBC_CONNECTOR_TYPE);
        PowerMockito.when(UserStoreConnectorFactory.getUserStoreConnector()).thenReturn(jdbcUserStoreConnector);
        UserStoreManager manager = UserStoreManagerFactory.getUserStoreManager();
        Assert.assertTrue(manager instanceof JDBCUserStoreManager);

        PowerMockito.when(userStoreConfiguration.getConnectorType()).thenReturn(UserStoreConstants.LDAP_CONNECTOR_TYPE);
        PowerMockito.when(UserStoreConnectorFactory.getUserStoreConnector()).thenReturn(ldapUserStoreConnector);
        manager = UserStoreManagerFactory.getUserStoreManager();
        Assert.assertTrue(manager instanceof LDAPUserStoreManager);

        PowerMockito.when(userStoreConfiguration.getConnectorType()).thenReturn("NoSuchConnector");
        PowerMockito.when(UserStoreConnectorFactory.getUserStoreConnector()).thenReturn(jdbcUserStoreConnector);
        manager = UserStoreManagerFactory.getUserStoreManager();
        Assert.assertTrue(manager instanceof JDBCUserStoreManager);
    }
}
