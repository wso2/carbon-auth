package org.wso2.carbon.auth.oauth.impl;
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.impl.ClientRegistrationFactory;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
//import org.wso2.carbon.auth.oauth.IntegrationTestBase;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;

import java.util.Calendar;
import java.util.UUID;

public class TokenManagerImplTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(AuthRequestHandlerTest.class);

    @Before
    public void setup() throws Exception {
        super.init();
        super.setup();
        log.info("setup TokenManagerImplTest");
    }

    @Test
    public void testGetTokenInfo() throws Exception {
        ClientRegistrationHandler defaultClientRegistrationHandler = ClientRegistrationFactory.getInstance()
                .getClientRegistrationHandler();
        Application application = new Application();
        application.setClientName("tokenGet-app");
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setGrantTypes("password");
        application.setCallBackUrl("http://localhost/callback");
        application.setOauthVersion("2");
        defaultClientRegistrationHandler.registerApplication(application);


        TokenManagerImpl tokenManager = new TokenManagerImpl();
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        String clientID = application.getClientId();
        String authUser = "admin";
        String userDomain = "default";
        long timeCreated = Calendar.getInstance().getTimeInMillis();
        long refreshTokenCreatedTime = Calendar.getInstance().getTimeInMillis();
        int validityPeriod = 3600;
        int refreshTokenValidityPeriod = 3600;
        String tokenScopeHash = "hash";
        String tokenState = "active";
        String userType = "application user";
        String grantType = application.getGrantTypes();

        tokenManager.storeToken(accessToken, refreshToken, clientID, authUser, userDomain, timeCreated,
                refreshTokenCreatedTime, validityPeriod, refreshTokenValidityPeriod, tokenScopeHash, tokenState,
                userType, grantType);

        AccessTokenDTO accessTokenDTO = tokenManager.getTokenInfo(accessToken);
        Assert.assertEquals(accessToken, accessTokenDTO.getAccessToken());
        Assert.assertEquals(refreshToken, accessTokenDTO.getRefreshToken());
        Assert.assertEquals(authUser, accessTokenDTO.getAuthUser());
        Assert.assertEquals(timeCreated, accessTokenDTO.getTimeCreated());
        Assert.assertEquals(refreshTokenCreatedTime, accessTokenDTO.getRefreshTokenCreatedTime());
        Assert.assertEquals(validityPeriod, accessTokenDTO.getValidityPeriod());
        Assert.assertEquals(refreshTokenValidityPeriod, accessTokenDTO.getRefreshTokenValidityPeriod());
        Assert.assertEquals(tokenScopeHash, accessTokenDTO.getTokenScopeHash());
        Assert.assertEquals(tokenState, accessTokenDTO.getTokenState());
        Assert.assertEquals(userType, accessTokenDTO.getUserType());
        Assert.assertEquals(grantType, accessTokenDTO.getGrantType());
    }

    @After
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }
}
