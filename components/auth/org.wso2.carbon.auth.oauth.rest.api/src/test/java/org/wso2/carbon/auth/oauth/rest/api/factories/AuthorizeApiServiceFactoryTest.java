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
package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.rest.api.AuthorizeApiService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DAOFactory.class })
public class AuthorizeApiServiceFactoryTest {

    @Test
    public void getAuthorizeApiTest() throws Exception {
        AuthorizeApiServiceFactory authorizeApiServiceFactory = new AuthorizeApiServiceFactory();
        Assert.assertNotNull(authorizeApiServiceFactory);

        PowerMockito.mockStatic(DAOFactory.class);
        OAuthDAO clientDAO = PowerMockito.mock(OAuthDAO.class);
        PowerMockito.when(DAOFactory.getClientDAO()).thenReturn(clientDAO);
        AuthorizeApiService authorizeApi = AuthorizeApiServiceFactory.getAuthorizeApi();
        Assert.assertNotNull(authorizeApi);

        PowerMockito.when(DAOFactory.getClientDAO()).thenThrow(OAuthDAOException.class);
        try {
            authorizeApi = AuthorizeApiServiceFactory.getAuthorizeApi();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
        }
    }
}
