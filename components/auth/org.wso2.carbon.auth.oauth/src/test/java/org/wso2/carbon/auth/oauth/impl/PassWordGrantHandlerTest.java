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
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PassWordGrantHandlerTest {
    private PasswordGrantHandlerImpl passwordGrantHandler;
    private OAuthDAO oauthDAO;
    private UserNameMapper userNameMapper;
    String clientId = "JgUsk2mQ_WL0ffmpRSpHDJWFjvEa";
    String clientSecret = "KQd8QXgV3bG1nFOGRDf7ib6HJu4a";
    String scope = "scope1";
    String authorization;
    ClientLookup clientLookup;
    AccessTokenContext context;
    Map<String, String> queryParameters;
    UserStoreManager userStoreManager;

    @BeforeTest
    public void init() throws UserStoreException {
        oauthDAO = Mockito.mock(OAuthDAO.class);
        clientLookup = Mockito.mock(ClientLookup.class);
        userNameMapper = Mockito.mock(UserNameMapper.class);
        authorization = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        context = new AccessTokenContext();
        queryParameters = new HashMap<>();
        userStoreManager = Mockito.mock(UserStoreManager.class);
        passwordGrantHandler = new PasswordGrantHandlerImpl(oauthDAO, userNameMapper, clientLookup, userStoreManager);
    }

    @Test
    public void testProcessWithValidateGrant() throws AuthException {
        authorization = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        Application application = new Application();
        application.setGrantTypes(GrantType.PASSWORD + " " + GrantType.REFRESH_TOKEN + " " + GrantType
                .CLIENT_CREDENTIALS);
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.PASSWORD.getValue());
        queryParameters.put(OAuthConstants.USERNAME, "admin");
        queryParameters.put(OAuthConstants.PASSWORD, "admin");
        Mockito.when(clientLookup.getClientId(Mockito.anyString(), Mockito.any(AccessTokenContext.class), Mockito.any
                (MutableBoolean.class))).thenReturn(clientId);
        Mockito.when(userStoreManager.doAuthenticate("admin", "admin")).thenReturn(true);
        passwordGrantHandler.process(authorization, context, queryParameters);
        passwordGrantHandler.process(authorization, context, queryParameters);
    }
}
