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
package org.wso2.carbon.auth.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.dao.impl.UserMappingDAOImpl;
import org.wso2.carbon.auth.core.exception.AuthDAOException;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;

public class UserMappingDaoIntegrationTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(UserMappingDaoIntegrationTest.class);
    public static final String USER_NAME = "admin";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        super.setup();
        log.info("Data sources initialized");
    }

    @Test
    public void testGetPseudoNameByUserID() throws AuthDAOException {
        UserMappingDAOImpl userMappingDAO = new UserMappingDAOImpl();
        String pseudoName = userMappingDAO.getPseudoNameByUserID(USER_NAME);
        Assert.assertNotNull(pseudoName);
        String retrievedUserName = userMappingDAO.getUserIDByPseudoName(pseudoName);
        Assert.assertEquals(retrievedUserName, USER_NAME);
        String retrievedPseudoName = userMappingDAO.getPseudoNameByUserID(USER_NAME);
        Assert.assertEquals(retrievedPseudoName, pseudoName);

    }

    @Test
    public void testGetPseudoNameByUserIDExceptionPath() throws Exception {
        super.cleanup();
        UserMappingDAOImpl userMappingDAO = new UserMappingDAOImpl();
        try {
            userMappingDAO.getPseudoNameByUserID(USER_NAME);
            Assert.assertTrue(false);
        } catch (AuthDAOException e) {
            Assert.assertTrue(true);
        }
    }


    @Test
    public void testGetUserIDByPseudoNameExceptionPath() throws Exception {
        super.cleanup();
        UserMappingDAOImpl userMappingDAO = new UserMappingDAOImpl();
        try {
            userMappingDAO.getUserIDByPseudoName(USER_NAME);
            Assert.assertTrue(false);
        } catch (AuthDAOException e) {
            Assert.assertTrue(true);
        }
    }

    @AfterClass
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }
}
