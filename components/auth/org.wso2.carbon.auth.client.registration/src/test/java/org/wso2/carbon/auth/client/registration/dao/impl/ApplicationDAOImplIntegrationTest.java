/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.client.registration.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.Constants;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;

import java.util.Arrays;
import java.util.UUID;

public class ApplicationDAOImplIntegrationTest extends AuthDAOIntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(ApplicationDAOImplIntegrationTest.class);

    @BeforeClass
    public void init() throws Exception {

        super.init();
        super.setup();
        log.info("Data sources initialized");
    }

    @Test
    public void testAddAndGetApplication() throws ClientRegistrationDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        Application application = new Application();
        application.setClientName("app1");
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setApplicationAccessTokenExpiryTime(3600L);
        application.setAuthUser("admin");
        application.setOauthVersion("2");
        application.setAudiences(Arrays.asList("a", "b"));
        Application retrievedApplication = applicationDAO.createApplication(application);
        Assert.assertEquals(application.getClientId(), retrievedApplication.getClientId());
        Assert.assertEquals(application.getClientSecret(), retrievedApplication.getClientSecret());
        Assert.assertEquals(Constants.DEFAULT_TOKEN_TYPE, retrievedApplication.getTokenType());
        Assert.assertEquals(retrievedApplication.getAudiences().size(), 2);
        Assert.assertEquals(retrievedApplication.getAudiences(), application.getAudiences());
        retrievedApplication.setClientSecret(UUID.randomUUID().toString());
        Application updatedApplication = applicationDAO.updateApplication(application.getClientId(),
                retrievedApplication);
        Assert.assertNotEquals(updatedApplication.getClientSecret(), retrievedApplication.getClientSecret());
        applicationDAO.deleteApplication(application.getClientId());
        Assert.assertNull(applicationDAO.getApplication(application.getClientId()));
    }

    @Test
    public void testAddAndGetApplicationWithCallBackURl() throws ClientRegistrationDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        Application application = new Application();
        application.setClientName("app2");
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setCallBackUrl("https://localhost");
        application.setTokenType("JWT");
        application.setAuthUser("admin");
        application.setOauthVersion("2");
        Application retrievedApplication = applicationDAO.createApplication(application);
        Assert.assertEquals(application.getClientId(), retrievedApplication.getClientId());
        Assert.assertEquals(application.getClientSecret(), retrievedApplication.getClientSecret());
        Assert.assertEquals("JWT", retrievedApplication.getTokenType());
        retrievedApplication.setApplicationAccessTokenExpiryTime(null);
        Application updatedApplication = applicationDAO.updateApplication(application.getClientId(),
                retrievedApplication);
        applicationDAO.deleteApplication(application.getClientId());
        Assert.assertNull(applicationDAO.getApplication(application.getClientId()));
    }
}
