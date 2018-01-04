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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.AuthConfiguration;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceReferenceHolder.class })
public class UserStoreConnectorFactoryTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(UserStoreConnectorFactoryTest.class);
    @Mock
    UserStoreConfiguration userStoreConfiguration;
    @Mock
    ServiceReferenceHolder serviceReferenceHolder;
    @Mock
    AuthConfiguration authConfiguration;

    @Before
    public void setup() throws Exception {
        super.init();
        super.setup();
        log.info("setup UserStoreConnectorFactoryTest");
    }

    @Test
    public void testGetUserStoreConnector() throws Exception {
        UserStoreConnector connector = UserStoreConnectorFactory.getUserStoreConnector();
        Assert.assertNotNull(connector);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        PowerMockito.when(serviceReferenceHolder.getAuthConfiguration()).thenReturn(authConfiguration);
        PowerMockito.when(authConfiguration.getUserStoreConfiguration()).thenReturn(userStoreConfiguration);
        PowerMockito.when(userStoreConfiguration.getConnectorType()).thenReturn("noSuchConnector");
        connector = UserStoreConnectorFactory.getUserStoreConnector();
        Assert.assertNotNull(connector);

        UserStoreConnectorFactory userStoreConnectorFactory = new UserStoreConnectorFactory();
        Assert.assertNotNull(userStoreConnectorFactory);
    }
}
