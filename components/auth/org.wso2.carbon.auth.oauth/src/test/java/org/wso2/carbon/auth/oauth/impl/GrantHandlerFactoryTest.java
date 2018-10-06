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
package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Map;
import java.util.Optional;

public class GrantHandlerFactoryTest {

    private String grantTypeValue;
    private AccessTokenContext context;
    private OAuthDAO oauthDAO;
    private ApplicationDAO applicationDAO;
    private MutableBoolean haltExecution;
    private UserNameMapper userNameMapper;
    private UserStoreManager userStoreManager;

    @Before
    public void setup() throws Exception {

        context = new AccessTokenContext();
        oauthDAO = Mockito.mock(OAuthDAO.class);
        applicationDAO = Mockito.mock(ApplicationDAO.class);
        haltExecution = new MutableBoolean(false);
        userNameMapper = Mockito.mock(UserNameMapper.class);
        userStoreManager = Mockito.mock(UserStoreManager.class);
        ServiceReferenceHolder.getInstance().setConfig(new OAuthConfiguration());
    }

    @Test
    public void testCreateGrantHandler() throws Exception {

        //test with non exist grant impl class
        ServiceReferenceHolder.getInstance().getAuthConfigurations().getGrantTypes()
                .put(GrantType.PASSWORD.getValue(), "org.wso2.carbon.apim.noSuchGrantClass");
        grantTypeValue = GrantType.PASSWORD.getValue();
        Optional<GrantHandler> handler = new GrantHandlerFactory(userStoreManager, userNameMapper)
                .createGrantHandler(grantTypeValue, context, oauthDAO, applicationDAO, haltExecution);
        Assert.assertFalse(handler.isPresent());
        Assert.assertEquals(OAuth2Error.UNSUPPORTED_GRANT_TYPE, context.getErrorObject());

        //test with Instantiation Exception class
        ServiceReferenceHolder.getInstance().getAuthConfigurations().getGrantTypes()
                .put(GrantType.PASSWORD.getValue(), InstantiationExceptionImpl.class.getName());
        grantTypeValue = GrantType.PASSWORD.getValue();
        handler = new GrantHandlerFactory(userStoreManager, userNameMapper)
                .createGrantHandler(grantTypeValue, context, oauthDAO, applicationDAO, haltExecution);
        Assert.assertFalse(handler.isPresent());
        Assert.assertEquals(OAuth2Error.UNSUPPORTED_GRANT_TYPE, context.getErrorObject());
    }

    //test class for grantHandler implementation
    class InstantiationExceptionImpl implements GrantHandler {

        @Override
        public boolean validateGrant(String authorization, AccessTokenContext context, Map<String, String>
                queryParameters) throws AuthException {

            return true;
        }

        @Override
        public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
                throws AuthException {
            //test method
        }

        @Override
        public void init(UserNameMapper userNameMapper, OAuthDAO oauthDAO, UserStoreManager userStoreManager,
                         ApplicationDAO applicationDAO) {
            //test method
        }
    }
}
