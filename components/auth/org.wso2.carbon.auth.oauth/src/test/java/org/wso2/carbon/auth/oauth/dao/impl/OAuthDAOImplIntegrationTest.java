/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.oauth.dao.impl;

import com.nimbusds.oauth2.sdk.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.dao.impl.DAOFactory;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
import org.wso2.carbon.auth.oauth.Utils;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.dto.TokenState;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class OAuthDAOImplIntegrationTest extends AuthDAOIntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(OAuthDAOImplIntegrationTest.class);

    @BeforeClass
    public void init() throws Exception {

        super.init();
        super.setup();
        log.info("Data sources initialized");
    }

    @Test
    public void testGetRedirectUri() throws ClientRegistrationDAOException, OAuthDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        OAuthDAO oAuthDAO = new OAuthDAOImpl();
        Application application = new Application();
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setClientName("testGetRedirectUri");
        application.setCallBackUrl("https://localhost");
        applicationDAO.createApplication(application);
        Optional<Optional<String>> retrievedCallBackURl = oAuthDAO.getRedirectUri(application.getClientId());
        Assert.assertTrue(retrievedCallBackURl.isPresent());
        Assert.assertTrue(retrievedCallBackURl.get().isPresent());
        Assert.assertEquals(retrievedCallBackURl.get().get(), application.getCallBackUrl());
        Assert.assertTrue(oAuthDAO.isClientCredentialsValid(application.getClientId(), application.getClientSecret()));
        Assert.assertTrue(oAuthDAO.isClientCredentialsValid(application.getClientId(), application.getClientSecret()));
        Assert.assertFalse(oAuthDAO.isClientCredentialsValid("admin", "admin"));
    }

    @Test
    public void testGetRedirectUriWhileEmpty() throws ClientRegistrationDAOException, OAuthDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        OAuthDAO oAuthDAO = new OAuthDAOImpl();
        Application application = new Application();
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setClientName("testGetRedirectUri");
        applicationDAO.createApplication(application);
        Optional<Optional<String>> retrievedCallBackURl = oAuthDAO.getRedirectUri(application.getClientId());
        Assert.assertTrue(retrievedCallBackURl.isPresent());
        Assert.assertFalse(retrievedCallBackURl.get().isPresent());
    }

    @Test
    public void testAddAccessTokenInfo() throws OAuthDAOException, ClientRegistrationDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        OAuthDAO oAuthDAO = new OAuthDAOImpl();
        Application application = new Application();
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setClientName("testAddAccessTokenInfo");
        applicationDAO.createApplication(application);
        AccessTokenData accessTokenData = new AccessTokenData();
        accessTokenData.setAccessToken(UUID.randomUUID().toString());
        accessTokenData.setAccessTokenValidityPeriod(3600);
        accessTokenData.setAuthUser("admin");
        accessTokenData.setAccessTokenCreatedTime(Instant.now());
        accessTokenData.setClientId(application.getClientId());
        accessTokenData.setGrantType("password");
        accessTokenData.setHashedScopes(Utils.hashScopes(new Scope("read")));
        accessTokenData.setRefreshToken(UUID.randomUUID().toString());
        accessTokenData.setRefreshTokenCreatedTime(Instant.now());
        accessTokenData.setRefreshTokenValidityPeriod(15000L);
        accessTokenData.setScopes(Collections.singletonList("read"));
        accessTokenData.setTokenState(TokenState.ACTIVE);
        oAuthDAO.addAccessTokenInfo(accessTokenData);
        AccessTokenDTO accessTokenDTO = oAuthDAO.getTokenInfo(accessTokenData.getAccessToken());
        Assert.assertEquals(accessTokenData.getAccessTokenValidityPeriod(), accessTokenDTO.getValidityPeriod());
        Assert.assertEquals(accessTokenData.getRefreshTokenValidityPeriod(), accessTokenDTO
                .getRefreshTokenValidityPeriod());
        Assert.assertEquals(String.join(" ", accessTokenData.getScopes()), accessTokenDTO.getScopes());
        Assert.assertEquals(accessTokenData.getAuthUser(), accessTokenDTO.getAuthUser());
    }

    @Test
    public void testGetAccessTokenInfoFromRefreshToken() throws OAuthDAOException, ClientRegistrationDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        OAuthDAO oAuthDAO = new OAuthDAOImpl();
        Application application = new Application();
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setClientName("testGetAccessTokenInfoFromRefreshToken");
        applicationDAO.createApplication(application);
        AccessTokenData accessTokenData = new AccessTokenData();
        accessTokenData.setAccessToken(UUID.randomUUID().toString());
        accessTokenData.setAccessTokenValidityPeriod(3600);
        accessTokenData.setAuthUser("admin");
        accessTokenData.setAccessTokenCreatedTime(Instant.now());
        accessTokenData.setClientId(application.getClientId());
        accessTokenData.setGrantType("password");
        accessTokenData.setHashedScopes(Utils.hashScopes(new Scope("read")));
        accessTokenData.setRefreshToken(UUID.randomUUID().toString());
        accessTokenData.setRefreshTokenCreatedTime(Instant.now());
        accessTokenData.setRefreshTokenValidityPeriod(15000L);
        accessTokenData.setScopes(Collections.singletonList("read"));
        accessTokenData.setTokenState(TokenState.ACTIVE);
        oAuthDAO.addAccessTokenInfo(accessTokenData);
        AccessTokenDTO accessTokenDTO = oAuthDAO.getTokenInfo(accessTokenData.getRefreshToken(), accessTokenData
                .getClientId());
        Assert.assertEquals(accessTokenData.getAccessTokenValidityPeriod(), accessTokenDTO.getValidityPeriod());
        Assert.assertEquals(accessTokenData.getRefreshTokenValidityPeriod(), accessTokenDTO
                .getRefreshTokenValidityPeriod());
        Assert.assertEquals(String.join(" ", accessTokenData.getScopes()), accessTokenDTO.getScopes());
        Assert.assertEquals(accessTokenData.getAuthUser(), accessTokenDTO.getAuthUser());
    }

    @Test
    public void testGetAccessTokenInfoFromCosumerKeyAuth() throws OAuthDAOException, ClientRegistrationDAOException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        OAuthDAO oAuthDAO = new OAuthDAOImpl();
        Application application = new Application();
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setClientName("testGetAccessTokenInfoFromCosumerKeyAuth");
        applicationDAO.createApplication(application);
        AccessTokenData accessTokenData = new AccessTokenData();
        accessTokenData.setAccessToken(UUID.randomUUID().toString());
        accessTokenData.setAccessTokenValidityPeriod(3600);
        accessTokenData.setAuthUser("admin");
        accessTokenData.setAccessTokenCreatedTime(Instant.now());
        accessTokenData.setClientId(application.getClientId());
        accessTokenData.setGrantType("password");
        accessTokenData.setHashedScopes(Utils.hashScopes(new Scope("read")));
        accessTokenData.setRefreshToken(UUID.randomUUID().toString());
        accessTokenData.setRefreshTokenCreatedTime(Instant.now());
        accessTokenData.setRefreshTokenValidityPeriod(15000L);
        accessTokenData.setScopes(Collections.singletonList("read"));
        accessTokenData.setTokenState(TokenState.ACTIVE);
        oAuthDAO.addAccessTokenInfo(accessTokenData);
        AccessTokenDTO accessTokenDTO = oAuthDAO.getTokenInfo(accessTokenData.getAuthUser(), accessTokenData
                .getGrantType(), accessTokenData.getClientId(), accessTokenData.getHashedScopes());
        Assert.assertEquals(accessTokenData.getAccessTokenValidityPeriod(), accessTokenDTO.getValidityPeriod());
        Assert.assertEquals(accessTokenData.getRefreshTokenValidityPeriod(), accessTokenDTO
                .getRefreshTokenValidityPeriod());
        Assert.assertEquals(String.join(" ", accessTokenData.getScopes()), accessTokenDTO.getScopes());
        Assert.assertEquals(accessTokenData.getAuthUser(), accessTokenDTO.getAuthUser());
    }

    @Test
    public void testAddAndGetOauthCode() throws OAuthDAOException, ClientRegistrationDAOException, URISyntaxException {

        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        OAuthDAO oAuthDAO = new OAuthDAOImpl();
        Application application = new Application();
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setClientName("testAddAndGetOauthCode");
        application.setCallBackUrl("https://localhost");
        applicationDAO.createApplication(application);
        String authCode = UUID.randomUUID().toString();
        oAuthDAO.addAuthCodeInfo(authCode, application.getClientId(), "read", new URI(application.getCallBackUrl()));
        Assert.assertEquals(oAuthDAO.getScopeForAuthCode(authCode, application.getClientId(), new URI(application
                .getCallBackUrl())), "read");
    }

    @AfterClass
    public void cleanup() throws Exception {

        super.cleanup();
        log.info("Cleaned databases");
    }
}

