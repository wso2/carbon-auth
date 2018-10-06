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

import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TokenRequestHandlerImplTest {

    private static final Logger log = LoggerFactory.getLogger(TokenRequestHandlerImplTest.class);
    private GrantHandlerFactory grantHandlerFactory;
    OAuthDAO oauthDAO;
    ApplicationDAO applicationDAO;
    ClientLookup clientLookup;
    GrantHandler grantHandler;

    @BeforeMethod
    public void setup() {

        ServiceReferenceHolder.getInstance().setConfig(new OAuthConfiguration());
        grantHandlerFactory = Mockito.mock(GrantHandlerFactory.class);
        log.info("setup TokenRequestHandlerImplTest");
        oauthDAO = Mockito.mock(OAuthDAO.class);
        applicationDAO = Mockito.mock(ApplicationDAO.class);
        clientLookup = Mockito.mock(ClientLookup.class);
        grantHandler = Mockito.mock(GrantHandler.class);

    }

    @Test
    public void testGenerateTokenValid() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        String clientId = UUID.randomUUID().toString();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(clientId);
        Application application = new Application();
        application.setApplicationAccessTokenExpiryTime(123L);
        Mockito.when(applicationDAO.getApplication(clientId)).thenReturn(application);
        Mockito.when(grantHandler.isAuthorizedClient(application, "password")).thenReturn(true);
        Mockito.when(grantHandler.validateGrant(Mockito.anyString(), Mockito.any(AccessTokenContext.class), Mockito
                .anyMap())).thenReturn(true);
        Mockito.when(grantHandler.validateScopes(Mockito.any(AccessTokenContext.class))).thenReturn(true);
        AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
    }

    @Test
    public void testGenerateTokenInvalid() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();

        //check empty grant
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, "");
        AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(OAuth2Error.INVALID_REQUEST, accessTokenContext.getErrorObject());

    }

    @Test
    public void testGenerateTokenInvalidClientId() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(null);
        AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.INVALID_CLIENT);
    }

    @Test
    public void testGenerateTokenInvalidApp() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        String clientId = UUID.randomUUID().toString();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(clientId);
        Mockito.when(applicationDAO.getApplication(clientId)).thenReturn(null);
        AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.INVALID_CLIENT);
    }

    @Test
    public void testGenerateTokenWhileThrowsExceptionWhileRetrievingAppingfo() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        String clientId = UUID.randomUUID().toString();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(clientId);
        Mockito.when(applicationDAO.getApplication(clientId)).thenThrow(new ClientRegistrationDAOException("ss"));
        try {
            AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
            Assert.fail();
        } catch (AuthException e) {
            Assert.assertTrue(e.getMessage().contains("Error getting client information from the DB"));
        }
    }

    @Test
    public void testGenerateTokenValidationFailsAtValidateGrant() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        String clientId = UUID.randomUUID().toString();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(clientId);
        Application application = new Application();
        application.setApplicationAccessTokenExpiryTime(123L);
        Mockito.when(applicationDAO.getApplication(clientId)).thenReturn(application);
        Mockito.when(grantHandler.isAuthorizedClient(application, "password")).thenReturn(true);
        Mockito.when(grantHandler.validateGrant(Mockito.anyString(), Mockito.any(AccessTokenContext.class), Mockito
                .anyMap())).thenReturn(false);
        tokenRequestHandler.generateToken(authorization, queryParameters);
        Mockito.verify(grantHandler, Mockito.never()).validateScopes(Mockito.any(AccessTokenContext.class));
        Mockito.verify(grantHandler, Mockito.never()).process(Mockito.anyString(), Mockito.any(AccessTokenContext
                .class), Mockito.anyMap());
    }

    @Test
    public void testGenerateTokenValidationFailsAtValidateScopes() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        String clientId = UUID.randomUUID().toString();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(clientId);
        Application application = new Application();
        application.setApplicationAccessTokenExpiryTime(123L);
        Mockito.when(applicationDAO.getApplication(clientId)).thenReturn(application);
        Mockito.when(grantHandler.isAuthorizedClient(application, "password")).thenReturn(true);
        Mockito.when(grantHandler.validateGrant(Mockito.anyString(), Mockito.any(AccessTokenContext.class), Mockito
                .anyMap())).thenReturn(true);
        Mockito.when(grantHandler.validateScopes(Mockito.any(AccessTokenContext.class))).thenReturn(false);
        tokenRequestHandler.generateToken(authorization, queryParameters);
        Mockito.verify(grantHandler, Mockito.never()).process(Mockito.anyString(), Mockito.any(AccessTokenContext
                .class), Mockito.anyMap());
    }

    @Test
    public void testGenerateTokenValidationFailsAtIsAuthorized() throws Exception {

        String authorization = "Basic ";

        TokenRequestHandler tokenRequestHandler = new TokenRequestHandlerImpl(oauthDAO, applicationDAO,
                clientLookup, grantHandlerFactory);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, OAuthConstants.PASSWORD);
        AccessTokenContext context = new AccessTokenContext();
        String clientId = UUID.randomUUID().toString();
        Mockito.when(grantHandlerFactory.createGrantHandler("password", context, oauthDAO, applicationDAO, new
                MutableBoolean(false))).thenReturn(Optional.of(grantHandler));
        Mockito.when(clientLookup.getClientId(authorization, context, queryParameters, new MutableBoolean()))
                .thenReturn(clientId);
        Application application = new Application();
        application.setApplicationAccessTokenExpiryTime(123L);
        Mockito.when(applicationDAO.getApplication(clientId)).thenReturn(application);
        Mockito.when(grantHandler.isAuthorizedClient(application, "password")).thenReturn(false);
        AccessTokenContext accessTokenContext = tokenRequestHandler.generateToken(authorization, queryParameters);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.UNSUPPORTED_GRANT_TYPE);
    }
}
