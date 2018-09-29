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
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.Utils;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.dto.TokenState;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PassWordGrantHandlerTest {

    private PasswordGrantHandlerImpl passwordGrantHandler;
    private OAuthDAO oauthDAO;
    private ApplicationDAO applicationDAO;
    private UserNameMapper userNameMapper;
    String clientId = "JgUsk2mQ_WL0ffmpRSpHDJWFjvEa";
    String clientSecret = "KQd8QXgV3bG1nFOGRDf7ib6HJu4a";
    String username = "admin";
    String password = "admin";
    String scope = "scope1";
    String authorization;
    ClientLookup clientLookup;
    AccessTokenContext context;
    Map<String, String> queryParameters;
    UserStoreManager userStoreManager;

    @BeforeTest
    public void init() throws UserStoreException {
        ServiceReferenceHolder.getInstance().setConfig(new OAuthConfiguration());

        oauthDAO = Mockito.mock(OAuthDAO.class);
        clientLookup = Mockito.mock(ClientLookup.class);
        userNameMapper = Mockito.mock(UserNameMapper.class);
        authorization = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        context = new AccessTokenContext();
        queryParameters = new HashMap<>();
        userStoreManager = Mockito.mock(UserStoreManager.class);
        passwordGrantHandler = new PasswordGrantHandlerImpl();
        passwordGrantHandler.init(userNameMapper, oauthDAO, userStoreManager, applicationDAO);
    }

    @Test
    public void testProcessWithValidateGrant() throws AuthException {

        authorization = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        Application application = new Application();
        application.setGrantTypes(GrantType.PASSWORD + " " + GrantType.REFRESH_TOKEN + " " + GrantType
                .CLIENT_CREDENTIALS);
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.PASSWORD.getValue());
        queryParameters.put(OAuthConstants.USERNAME, username);
        queryParameters.put(OAuthConstants.PASSWORD, password);
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scope);
        Mockito.when(clientLookup
                .getClientId(Mockito.anyString(), Mockito.any(AccessTokenContext.class), Mockito.any(Map.class),
                        Mockito.any(MutableBoolean.class))).thenReturn(clientId);
        Mockito.when(userStoreManager.doAuthenticate(username, username)).thenReturn(true);
        Mockito.when(userNameMapper.getLoggedInPseudoNameFromUserID(username)).thenReturn("");
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        context.getParams().put(OAuthConstants.FILTERED_SCOPES, new Scope(scope));
        passwordGrantHandler.validateGrant(authorization, context, queryParameters);
        passwordGrantHandler.process(authorization, context, queryParameters);
        Assert.assertTrue(context.isSuccessful());

        AccessTokenDTO accessTokenData = new AccessTokenDTO();
        accessTokenData.setAccessToken(UUID.randomUUID().toString());
        accessTokenData.setRefreshToken(UUID.randomUUID().toString());
        accessTokenData.setScopes("default");
        accessTokenData.setTimeCreated(System.currentTimeMillis());
        accessTokenData.setRefreshTokenCreatedTime(System.currentTimeMillis());
        accessTokenData.setValidityPeriod(9900);
        accessTokenData.setRefreshTokenValidityPeriod(9900);
        accessTokenData.setTokenState(TokenState.ACTIVE.toString());

        //token generate when previous token is not expired
        String hashedscopes = Utils.hashScopes(new Scope(scope));
        String uid = UUID.randomUUID().toString();
        Mockito.when(userNameMapper.getLoggedInPseudoNameFromUserID(username)).thenReturn(uid);
        Mockito.when(oauthDAO.getTokenInfo(uid, GrantType.PASSWORD.getValue(), clientId, hashedscopes))
                .thenReturn(accessTokenData);
        context.getParams().put(OAuthConstants.GRANT_TYPE, GrantType.PASSWORD.getValue());
        context.getParams().put(OAuthConstants.CLIENT_ID, clientId);
        context.getParams().put(OAuthConstants.FILTERED_SCOPES, new Scope(scope));
        passwordGrantHandler.process(authorization, context, queryParameters);
        Assert.assertTrue(context.isSuccessful());
        Tokens tokens = context.getAccessTokenResponse().getTokens();
        Assert.assertEquals(tokens.getAccessToken().getValue(), accessTokenData.getAccessToken());
        Assert.assertEquals(tokens.getAccessToken().getLifetime(), accessTokenData.getValidityPeriod());

        //token generate when previous token is not expired and different scope
        String newScope = "newScope";
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, newScope);
        context.getParams().put(OAuthConstants.FILTERED_SCOPES, new Scope(newScope));
        Mockito.when(oauthDAO.getTokenInfo(username, GrantType.PASSWORD.getValue(), clientId, newScope))
                .thenReturn(null);
        context.getParams().put(OAuthConstants.GRANT_TYPE, GrantType.PASSWORD.getValue());
        context.getParams().put(OAuthConstants.CLIENT_ID, clientId);
        passwordGrantHandler.process(authorization, context, queryParameters);
        Assert.assertTrue(context.isSuccessful());
        tokens = context.getAccessTokenResponse().getTokens();
        Assert.assertNotEquals(tokens.getAccessToken().getValue(), accessTokenData.getAccessToken());
        Assert.assertNotEquals(tokens.getRefreshToken().getValue(), accessTokenData.getRefreshToken());

        //token generate when previous token is expired
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scope);
        long currentTime = System.currentTimeMillis();
        accessTokenData.setTimeCreated(currentTime - 5000000);
        accessTokenData.setValidityPeriod(3600);
        Mockito.when(oauthDAO.getTokenInfo(username, GrantType.PASSWORD.getValue(), clientId, scope))
                .thenReturn(accessTokenData);
        passwordGrantHandler.process(authorization, context, queryParameters);
        Assert.assertTrue(context.isSuccessful());
        tokens = context.getAccessTokenResponse().getTokens();
        Assert.assertNotNull(tokens.getAccessToken().getValue());
        Assert.assertNotEquals(tokens.getAccessToken().getValue(), accessTokenData.getAccessToken());
        Assert.assertEquals(tokens.getAccessToken().getLifetime(), accessTokenData.getValidityPeriod());

        //check parse exception path
        queryParameters.remove(OAuthConstants.USERNAME);
        passwordGrantHandler.validateGrant(authorization, context, queryParameters);
        passwordGrantHandler.process(authorization, context, queryParameters);
        Assert.assertEquals(context.getErrorObject(), OAuth2Error.INVALID_REQUEST);
    }
}
