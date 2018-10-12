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

package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.Scope;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.configuration.models.AuthConfiguration;
import org.wso2.carbon.auth.core.configuration.models.KeyManagerConfiguration;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenGenerator;
import org.wso2.carbon.auth.oauth.Utils;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.io.File;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

public class JWTTokenGeneratorTest {

    @Test
    public void testGenerateJWTToken() throws AuthException, ParseException {

        OAuthConfiguration authConfiguration = new OAuthConfiguration();
        authConfiguration.setSignatureAlgorithm("NONE");
        ServiceReferenceHolder.getInstance().setConfig(authConfiguration);
        TokenGenerator tokenGenerator = new JWTTokenGenerator();
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        accessTokenContext.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        accessTokenContext.getParams().put(OAuthConstants.SCOPES, new Scope("default"));
        accessTokenContext.getParams().put(OAuthConstants.AUTH_USER, "admin");
        accessTokenContext.getParams().put(OAuthConstants.CLIENT_ID, "abcd-1234");
        tokenGenerator.generateAccessToken(accessTokenContext);
        Assert.assertNotNull(accessTokenContext.getAccessTokenResponse());
        AccessTokenResponse accessTokenResponse = accessTokenContext.getAccessTokenResponse();
        Assert.assertNotNull(accessTokenResponse.getTokens().getBearerAccessToken().getValue());
        String jwtToken = accessTokenResponse.getTokens().getBearerAccessToken().getValue();
        Assert.assertTrue(jwtToken.split("/.").length >= 1);
        PlainJWT jwt = PlainJWT.parse(jwtToken);
        Assert.assertEquals(jwt.getHeader().getAlgorithm().getName(), "none");
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        Assert.assertEquals(jwtClaimsSet.getIssuer(), "https://localhost:9443/oauth2/token");
        Assert.assertEquals(jwtClaimsSet.getAudience().get(0), "abcd-1234");
        Assert.assertEquals(jwtClaimsSet.getClaim("scope"), "default");
    }

    @Test
    public void testGenerateJWTTokenWithSign() throws AuthException, ParseException, JOSEException {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        KeyManagerConfiguration keyManagerConfiguration = new KeyManagerConfiguration();
        keyManagerConfiguration.setKeyStoreLocation("src" + File.separator + "test" + File.separator + "resources" +
                File.separator + "wso2carbon.jks");
        AuthConfiguration authConfiguration = new AuthConfiguration();
        authConfiguration.setKeyManagerConfigs(keyManagerConfiguration);
        ServiceReferenceHolder.getInstance().setAuthConfiguration(authConfiguration);
        ServiceReferenceHolder.getInstance().setPrivateKey(Utils.extractPrivateKeyFromCertificate());
        ServiceReferenceHolder.getInstance().setPublicKey(Utils.extractPublicKeyFromCertificate());
        TokenGenerator tokenGenerator = new JWTTokenGenerator();
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        accessTokenContext.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        accessTokenContext.getParams().put(OAuthConstants.SCOPES, new Scope("default"));
        accessTokenContext.getParams().put(OAuthConstants.AUTH_USER, "admin");
        accessTokenContext.getParams().put(OAuthConstants.CLIENT_ID, "abcd-1234");
        tokenGenerator.generateAccessToken(accessTokenContext);
        Assert.assertNotNull(accessTokenContext.getAccessTokenResponse());
        AccessTokenResponse accessTokenResponse = accessTokenContext.getAccessTokenResponse();
        Assert.assertNotNull(accessTokenResponse.getTokens().getBearerAccessToken().getValue());
        String jwtToken = accessTokenResponse.getTokens().getBearerAccessToken().getValue();
        Assert.assertTrue(jwtToken.split("/.").length >= 1);
        SignedJWT jwt = SignedJWT.parse(jwtToken);
        Assert.assertEquals(jwt.getHeader().getAlgorithm().getName(), "RS256");
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        Assert.assertEquals(jwtClaimsSet.getIssuer(), "https://localhost:9443/oauth2/token");
        Assert.assertEquals(jwtClaimsSet.getAudience().get(0), "abcd-1234");
        Assert.assertEquals(jwtClaimsSet.getClaim("scope"), "default");
        JWSVerifier jwsVerifier = new RSASSAVerifier((RSAPublicKey) ServiceReferenceHolder.getInstance().getPublicKey
                ().getPublicKey());
        Assert.assertTrue(jwt.verify(jwsVerifier));
    }

    @Test
    public void testGenerateJWTTokenWithUnsupportedSignatureAlgorithm() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        oAuthConfiguration.setSignatureAlgorithm("abcd");
        try {
            TokenGenerator tokenGenerator = new JWTTokenGenerator();
            Assert.fail();
        } catch (AuthException e) {
            Assert.assertTrue(e.getMessage().contains("Unsupported Signature Algorithm"));
        }
    }

}
