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
package org.wso2.carbon.auth.user.store.connector;

import org.junit.Assert;
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
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
import org.wso2.carbon.auth.user.store.claim.ClaimConstants;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.io.File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceReferenceHolder.class })
public class UserStoreConnectorFactoryTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(UserStoreConnectorFactoryTest.class);
    @Mock
    ServiceReferenceHolder serviceReferenceHolder;
    @Mock
    UserStoreConfigurationService userStoreConfigurationService;
    @Mock
    DataSourceService dataSourceService;

    @Before
    public void setup() throws Exception {
        super.init();
        super.setup();
        log.info("setup UserStoreConnectorFactoryTest");
    }

    @Test
    public void testGetUserStoreConnector() throws Exception {
        System.setProperty(ClaimConstants.CARBON_RUNTIME_DIR_PROP_NAME,
                System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                        + "resources" + File.separator + "runtime.home" + File.separator);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        //user store config
        Mockito.when(serviceReferenceHolder.getUserStoreConfigurationService())
                .thenReturn(userStoreConfigurationService);
        UserStoreConfiguration userStoreConfiguration = new UserStoreConfiguration();
        userStoreConfiguration.setConnectorType(UserStoreConstants.JDBC_CONNECTOR_TYPE);
        Mockito.when(userStoreConfigurationService.getUserStoreConfiguration()).thenReturn(userStoreConfiguration);
    
        //data sources
        Mockito.when(serviceReferenceHolder.getDataSourceService())
                .thenReturn(dataSourceService);
        Mockito.when(dataSourceService.getDataSource(Constants.DATASOURCE_WSO2UM_DB))
                .thenReturn(this.umDataSource.getDatasource());
    
        UserStoreConnector connector = UserStoreConnectorFactory.getUserStoreConnector();
        Assert.assertNotNull(connector);
    
        userStoreConfiguration.setConnectorType("noSuchConnector");
        try {
            UserStoreConnectorFactory.getUserStoreConnector();
            Assert.fail("Failure expected when trying to get an undefined connector, but didn't.");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getMessage().contains("not defined"));
        }
    }
}
