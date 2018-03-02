/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.auth.core.impl;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.dao.UserMappingDAO;
import org.wso2.carbon.auth.core.exception.AuthDAOException;
import org.wso2.carbon.auth.core.exception.AuthException;

import java.util.UUID;

public class UserNameMapperImplTest {

    private UserMappingDAO userMappingDAO;
    String userId = "admin1";
    String pseudoName = UUID.randomUUID().toString();
    UserNameMapperImpl userNameMapper;

    @BeforeMethod
    public void init() {
        userMappingDAO = Mockito.mock(UserMappingDAO.class);
        userNameMapper = new UserNameMapperImpl(userMappingDAO);

    }

    @Test
    public void testGetLoggedInUserIDFromPseudoName() throws AuthDAOException, AuthException {
        Mockito.when(userMappingDAO.getPseudoNameByUserID(userId)).thenReturn(pseudoName);
        Assert.assertEquals(userNameMapper.getLoggedInPseudoNameFromUserID(userId), pseudoName);
    }

    @Test
    public void testGetLoggedInUserIDFromPseudoNameForAdmin() throws AuthDAOException, AuthException {
        Mockito.when(userMappingDAO.getPseudoNameByUserID("admin")).thenReturn("admin");
        Assert.assertEquals(userNameMapper.getLoggedInPseudoNameFromUserID("admin"), "admin");
    }

    @Test
    public void testGetLoggedInPseudoNameFromUserID() throws AuthDAOException, AuthException {
        Mockito.when(userMappingDAO.getUserIDByPseudoName(pseudoName)).thenReturn(userId);
        Assert.assertEquals(userNameMapper.getLoggedInUserIDFromPseudoName(pseudoName), userId);
    }

    @Test
    public void testGetLoggedInPseudoNameFromUserIDForAdmin() throws AuthDAOException, AuthException {
        Mockito.when(userMappingDAO.getUserIDByPseudoName("admin")).thenReturn("admin");
        Assert.assertEquals(userNameMapper.getLoggedInUserIDFromPseudoName("admin"), "admin");
    }

    @Test(expectedExceptions = AuthException.class)
    public void testGetLoggedInPseudoNameFromUserIDWhileDatabaseErrorOccured() throws AuthException, AuthDAOException {
        Mockito.when(userMappingDAO.getPseudoNameByUserID(userId)).thenThrow(AuthDAOException.class);
        userNameMapper.getLoggedInPseudoNameFromUserID(userId);
    }

    @Test(expectedExceptions = AuthException.class)
    public void testGetLoggedInUserIDFromPseudoNameWhileDatabaseErrorOccured() throws AuthException, AuthDAOException {
        Mockito.when(userMappingDAO.getUserIDByPseudoName(pseudoName)).thenThrow(AuthDAOException.class);
        userNameMapper.getLoggedInUserIDFromPseudoName(pseudoName);
    }
}
