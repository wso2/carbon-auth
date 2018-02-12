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
package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.user.mgt.impl.JDBCUserStoreManager;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserStoreConnectorFactory.class, JDBCUserStoreManager.class })
public class TokenRequestHandlerImplTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(TokenRequestHandlerImplTest.class);
    @Mock
    UserStoreConnector userStoreConnector;
    @Mock
    JDBCUserStoreManager jdbcUserStoreManager;
    @Mock
    DefaultPasswordHandler defaultPasswordHandler;

    public TokenRequestHandlerImplTest() {
    }

    @Before
    public void setup() throws Exception {
        super.init();
        super.setup();

        UserStoreConfiguration userStoreConfiguration = new UserStoreConfiguration();
        UserStoreConfigurationService userStoreConfigurationService = new UserStoreConfigurationService(
                userStoreConfiguration);
        org.wso2.carbon.auth.user.mgt.internal.ServiceReferenceHolder.getInstance()
                .setUserStoreConfigurationService(userStoreConfigurationService);
        org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder.getInstance()
                .setUserStoreConfigurationService(userStoreConfigurationService);;

        log.info("setup TokenRequestHandlerImplTest");
    }

    @Test
    public void testGenerateToken() throws Exception {
        String ck = "a54404ea-d588-476f-87a0-ecd963a6b0d7";
        String cs = "90f7ce1a-3363-4cd4-b6f2-abdcff0f32f2";
        String authCode = "456846s5-3123-cer4-bio2-abdcff0f98f2";
        String userId = "admin12345";
        String hashedPassword = "SSFSSFDSD5456";
        String redirectUri = "https://localhost/redirect";
        String scopes = "default";
        String authorization = "Basic " + Base64.getEncoder().encodeToString(((ck + ":" + cs).getBytes()));
        String username = "admin";
        String password = "admin";
        Map info = new HashMap();
        info.put(UserStoreConstants.PASSWORD, hashedPassword);
        info.put(UserStoreConstants.ITERATION_COUNT, 1);
        info.put(UserStoreConstants.KEY_LENGTH, 1);

        OAuthDAO clientDAO = DAOFactory.getClientDAO();
        ApplicationDAO applicationDAO = org.wso2.carbon.auth.client.registration.dao.impl.DAOFactory
                .getApplicationDAO();
        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(clientDAO, applicationDAO);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.USERNAME, username);
        queryParameters.put(OAuthConstants.PASSWORD, password);

        PowerMockito.mockStatic(UserStoreConnectorFactory.class);

        PowerMockito.when(jdbcUserStoreManager.doAuthenticate(username, password)).thenReturn(true);
        PowerMockito.when(UserStoreConnectorFactory.getUserStoreConnector()).thenReturn(userStoreConnector);
        PowerMockito.when(userStoreConnector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, username))
                .thenReturn(userId);
        PowerMockito.when(userStoreConnector.getUserPasswordInfo(userId)).thenReturn(info);
        PowerMockito.when(jdbcUserStoreManager.doAuthenticate(username, password)).thenReturn(true);
        PowerMockito.when(defaultPasswordHandler
                .hashPassword(Mockito.any(char[].class), Mockito.any(String.class), Mockito.any(String.class))).
                thenReturn(hashedPassword);

        PowerMockito.whenNew(JDBCUserStoreManager.class).withNoArguments().thenReturn(jdbcUserStoreManager);
        PowerMockito.whenNew(DefaultPasswordHandler.class).withNoArguments().thenReturn(defaultPasswordHandler);

        // adding auth app details to the DB
        String query = "INSERT INTO AUTH_OAUTH2_APPLICATION " + "(CLIENT_ID, CLIENT_SECRET, APP_NAME, OAUTH_VERSION,"
                + " REDIRECT_URI, GRANT_TYPES) VALUES ('" + ck + "'," + "'" + cs
                + "','sampleApp','2.0','url','password refresh_token client_credentials authorization_code') ";
        super.executeOnAuthDb(query);

        //check no such grant
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, "noSuch");
        AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_REQUEST, accessTokenContext.getErrorObject());

        //check empty grant
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, "");
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_REQUEST, accessTokenContext.getErrorObject());

        // check for password
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.PASSWORD.getValue());
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Tokens tokens = accessTokenContext.getAccessTokenResponse().getTokens();
        Assert.assertNotNull(tokens);
        Assert.assertNotNull(tokens.getAccessToken());
        Assert.assertNotNull(tokens.getRefreshToken());

        // check for password: unauthenticated user
        info.put(UserStoreConstants.PASSWORD, "wrongPass");
        PowerMockito.when(userStoreConnector.getUserPasswordInfo(userId)).thenReturn(info);
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.PASSWORD.getValue());
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertFalse(accessTokenContext.isSuccessful());

        info.put(UserStoreConstants.PASSWORD, hashedPassword);
        PowerMockito.when(userStoreConnector.getUserPasswordInfo(userId)).thenReturn(info);

        // check for not null scope
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scopes);
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.PASSWORD.getValue());
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        tokens = accessTokenContext.getAccessTokenResponse().getTokens();
        Assert.assertNotNull(tokens);
        Assert.assertNotNull(tokens.getAccessToken());
        Assert.assertNotNull(tokens.getRefreshToken());

        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, null);

        // check for password: wrong user info
        queryParameters.put(OAuthConstants.USERNAME, null);
        accessTokenContext = tokenRequestHandler.generateToken("Basic notvalid", queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_CLIENT, accessTokenContext.getErrorObject());

        queryParameters.put(OAuthConstants.USERNAME, username);
        // check for client credentials
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.CLIENT_CREDENTIALS.getValue());
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        tokens = accessTokenContext.getAccessTokenResponse().getTokens();
        Assert.assertNotNull(tokens);
        Assert.assertNotNull(tokens.getAccessToken());
        Assert.assertNotNull(tokens.getRefreshToken());

        // scope not null
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scopes);
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        tokens = accessTokenContext.getAccessTokenResponse().getTokens();
        Assert.assertNotNull(tokens);
        Assert.assertNotNull(tokens.getAccessToken());
        Assert.assertNotNull(tokens.getRefreshToken());

        // check for auth code
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, GrantType.AUTHORIZATION_CODE.getValue());
        queryParameters.put(OAuthConstants.CODE_QUERY_PARAM, authCode);
        queryParameters.put(OAuthConstants.REDIRECT_URI_QUERY_PARAM, redirectUri);

        query = "INSERT INTO AUTH_OAUTH2_AUTHORIZATION_CODE"
                + "(CLIENT_ID, AUTHORIZATION_CODE, REDIRECT_URI, SCOPE) VALUES('" + ck + "','" + authCode + "','"
                + redirectUri + "','" + scopes + "')";
        super.executeOnAuthDb(query);
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        tokens = accessTokenContext.getAccessTokenResponse().getTokens();
        Assert.assertNotNull(tokens);
        Assert.assertNotNull(tokens.getAccessToken());
        Assert.assertNotNull(tokens.getRefreshToken());

        // check missing Authorization header
        accessTokenContext = tokenRequestHandler.generateToken("", queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_CLIENT, accessTokenContext.getErrorObject());

        // check wrong Authorization header
        accessTokenContext = tokenRequestHandler.generateToken("Bearer", queryParameters);
        Assert.assertNull(accessTokenContext.getAccessTokenResponse());

        // check non exist client
        String noClientBeader = "Basic " + Base64.getEncoder().encodeToString(("asd:asd").getBytes());
        accessTokenContext = tokenRequestHandler.generateToken(noClientBeader, queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_CLIENT, accessTokenContext.getErrorObject());

        // check for auth code: no such scope
        queryParameters.put(OAuthConstants.REDIRECT_URI_QUERY_PARAM, "noSuchUrl");
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_REQUEST, accessTokenContext.getErrorObject());

        // check for auth code: null code
        queryParameters.put(OAuthConstants.CODE_QUERY_PARAM, null);
        accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertNull(accessTokenContext.getAccessTokenResponse());

    }

    @After
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }
}
