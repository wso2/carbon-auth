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
package org.wso2.carbon.auth.token.introspection.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DAOFactory.class })
public class IntrospectionManagerImplTest {

    @Mock
    TokenDAO tokenDAO;
    AccessTokenDTO accessTokenDTO;
    IntrospectionManager introspectionManager;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(DAOFactory.class);
        PowerMockito.when(DAOFactory.getTokenDAO()).thenReturn(tokenDAO);

        accessTokenDTO = new AccessTokenDTO();
        int tokenID = 1;
        String accessToken = "asdsadsd";
        String refreshToken = "asdsadsadsa";
        String consumerKey = "cZfN6jnDKfCB0ddkJntZo7LMWRwa";
        String authUser = "admin";
        String userDomain = "primary";
        long timeCreated = System.currentTimeMillis();
        long refreshTokenCreatedTime = System.currentTimeMillis();
        int validityPeriod = 3600;
        int refreshTokenValidityPeriod = 3600;
        String tokenScopeHash = "HASH";
        String tokenState = "active";
        String userType = "user and application";
        String grantType = "password";

        accessTokenDTO.setTokenID(tokenID);
        accessTokenDTO.setAccessToken(accessToken);
        accessTokenDTO.setRefreshToken(refreshToken);
        accessTokenDTO.setConsumerKey(consumerKey);
        accessTokenDTO.setAuthUser(authUser);
        accessTokenDTO.setUserDomain(userDomain);
        accessTokenDTO.setTimeCreated(timeCreated);
        accessTokenDTO.setRefreshTokenCreatedTime(refreshTokenCreatedTime);
        accessTokenDTO.setValidityPeriod(validityPeriod);
        accessTokenDTO.setRefreshTokenValidityPeriod(refreshTokenValidityPeriod);
        accessTokenDTO.setTokenScopeHash(tokenScopeHash);
        accessTokenDTO.setTokenState(tokenState);
        accessTokenDTO.setUserType(userType);
        accessTokenDTO.setGrantType(grantType);

        introspectionManager = new IntrospectionManagerImpl();
    }

    @Test
    public void testIntrospectEmptyToken() throws Exception {
        IntrospectionResponse response = introspectionManager.introspect("");
        Assert.assertFalse(response.isActive());
    }

    @Test
    public void testIntrospectExpiredToken() throws Exception {
        PowerMockito.when(tokenDAO.getTokenInfo(accessTokenDTO.getAccessToken())).thenReturn(accessTokenDTO);
        accessTokenDTO.setTimeCreated(System.currentTimeMillis() - (4000 * 1000));
        IntrospectionResponse response = introspectionManager.introspect(accessTokenDTO.getAccessToken());
        Assert.assertFalse(response.isActive());
    }

    @Test
    public void testIntrospectInfiniteToken() throws Exception {
        accessTokenDTO.setValidityPeriod(-1);
        PowerMockito.when(tokenDAO.getTokenInfo(accessTokenDTO.getAccessToken())).thenReturn(accessTokenDTO);
        IntrospectionResponse response = introspectionManager.introspect(accessTokenDTO.getAccessToken());
        Assert.assertTrue(response.isActive());
    }

    @Test
    public void testIntrospectNullFromDao() throws Exception {
        PowerMockito.when(tokenDAO.getTokenInfo(accessTokenDTO.getAccessToken())).thenReturn(null);

        IntrospectionResponse response = introspectionManager.introspect(accessTokenDTO.getAccessToken());
        Assert.assertFalse(response.isActive());
    }

    @Test
    public void testIntrospect() throws Exception {
        PowerMockito.when(tokenDAO.getTokenInfo(accessTokenDTO.getAccessToken())).thenReturn(accessTokenDTO);

        IntrospectionResponse response = introspectionManager.introspect(accessTokenDTO.getAccessToken());
        Assert.assertTrue(response.isActive());
    }
}
