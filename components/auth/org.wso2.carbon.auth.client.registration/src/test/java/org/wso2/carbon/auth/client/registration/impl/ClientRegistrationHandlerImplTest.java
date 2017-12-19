/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.client.registration.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.SampleTestObjectCreator;
import org.wso2.carbon.auth.client.registration.TestUtil;
import org.wso2.carbon.auth.client.registration.dto.ClientRegistrationResponse;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;

public class ClientRegistrationHandlerImplTest extends AuthDAOIntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationHandlerImplTest.class);

    public ClientRegistrationHandlerImplTest() {
    }
    
    @BeforeClass
    public void init() throws Exception {
        super.init();
        log.info("Data sources initialized");
    }

    @BeforeMethod
    public void setup() throws Exception {
        super.setup();
        log.info("Created databases");
    }

    @AfterClass
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }

    @Test
    public void testAddGetUpdateDeleteApplication() throws Exception {
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();

        //add the application and verification
        Application application = SampleTestObjectCreator.createDefaultApplication();
        handler.registerApplication(application);
        
        ClientRegistrationResponse registrationResponse = handler.getApplication(application.getClientId());
        Application appFromDB = registrationResponse.getApplication();
        Assert.assertEquals(appFromDB, application, TestUtil.printDiff(appFromDB, application));
        
        //update the application and verification
        application.setClientName("updatedName");
        ClientRegistrationResponse updateResponse = handler.updateApplication(application.getClientId(), application);
        Assert.assertTrue(updateResponse.isSuccessful());
        Assert.assertEquals(updateResponse.getApplication(), application);

        ClientRegistrationResponse getResponse = handler.getApplication(application.getClientId());
        appFromDB = getResponse.getApplication();
        Assert.assertEquals(appFromDB, application, TestUtil.printDiff(appFromDB, application));

        //deleting the application and verification
        ClientRegistrationResponse deleteResponse = handler.deleteApplication(application.getClientId());
        Assert.assertTrue(deleteResponse.isSuccessful());

        getResponse = handler.getApplication(application.getClientId());
        Assert.assertFalse(getResponse.isSuccessful());
    }

    @Test
    public void testUpdateInvalidApplication() throws Exception {
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
        //update an invalid application and verification
        Application application = SampleTestObjectCreator.createDefaultApplication();
        application.setClientId("invalid_id");
        ClientRegistrationResponse updateResponse = handler.updateApplication(application.getClientId(), application);
        Assert.assertFalse(updateResponse.isSuccessful());
        Assert.assertNotNull(updateResponse.getErrorObject());
    }

    @Test
    public void testAddApplicationWithNullCallbackGrantTypes() throws Exception {
        ClientRegistrationHandler handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();

        //add the application and verification
        Application application = SampleTestObjectCreator.createDefaultApplication();
        application.setGrantTypes(null);
        application.setCallBackUrl(null);
        handler.registerApplication(application);

        ClientRegistrationResponse registrationResponse = handler.getApplication(application.getClientId());
        Application appFromDB = registrationResponse.getApplication();
        Assert.assertEquals(appFromDB, application, TestUtil.printDiff(appFromDB, application));
    }
}
