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

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.Scope;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.Constants;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenGenerator;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

public class TokenIssuerTest {

    @Test
    public void testGenerateToken() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        TokenIssuer.generateAccessToken(new Scope("default"), context);
        Assert.assertTrue(context.isSuccessful());
    }

    @Test
    public void testRenewAccessTokenPerRequest() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        TokenIssuer.renewAccessTokenPerRequest(context);
        Assert.assertFalse(TokenIssuer.renewAccessTokenPerRequest(context));
    }

    @Test
    public void testRenewAccessTokenPerRequestWithDifferentTokenType() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        oAuthConfiguration.getTokenGenerators().put("abc", DummyTokenGenerator.class.getName());
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        context.getParams().put(OAuthConstants.TOKEN_TYPE, "abc");
        TokenIssuer.renewAccessTokenPerRequest(context);
        Assert.assertTrue(TokenIssuer.renewAccessTokenPerRequest(context));
    }

    @Test
    public void testRenewAccessTokenInstantiationException() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        oAuthConfiguration.getTokenGenerators().put("abc", Dummy2TokenGenerator.class.getName());
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        context.getParams().put(OAuthConstants.TOKEN_TYPE, "abc");
        TokenIssuer.renewAccessTokenPerRequest(context);
        Assert.assertFalse(TokenIssuer.renewAccessTokenPerRequest(context));
        Assert.assertEquals(context.getErrorObject(), OAuth2Error.SERVER_ERROR);
        TokenIssuer.generateAccessToken(new Scope("a"), context);
    }

    @Test
    public void testRenewAccessTokenPerRequestNonExistingTokenGenerationImplementation() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        oAuthConfiguration.getTokenGenerators().put("abc", "aa.aa.aa");
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        context.getParams().put(OAuthConstants.TOKEN_TYPE, "abc");
        TokenIssuer.renewAccessTokenPerRequest(context);
        Assert.assertFalse(TokenIssuer.renewAccessTokenPerRequest(context));
        Assert.assertEquals(context.getErrorObject(), OAuth2Error.SERVER_ERROR);
    }

    @Test
    public void testRenewAccessTokenPerRequestWithNonExistingTokenType() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        context.getParams().put(OAuthConstants.TOKEN_TYPE, "bcd");
        TokenIssuer.renewAccessTokenPerRequest(context);
        Assert.assertFalse(TokenIssuer.renewAccessTokenPerRequest(context));
        Assert.assertEquals(context.getErrorObject(), OAuth2Error.SERVER_ERROR);
    }

    @Test
    public void testGenerateTokenWithNonExistingTokenType() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.TOKEN_TYPE, "abc");
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        TokenIssuer.generateAccessToken(new Scope("default"), context);
        Assert.assertFalse(context.isSuccessful());
        Assert.assertEquals(context.getErrorObject(), OAuth2Error.SERVER_ERROR);

    }

    @Test
    public void testGenerateTokenWithNonExistingImplementation() {

        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        oAuthConfiguration.getTokenGenerators().replace(Constants.DEFAULT_TOKEN_TYPE, "org.wso2.carbon.auth" +
                ".ClientImpl");
        ServiceReferenceHolder.getInstance().setConfig(oAuthConfiguration);
        AccessTokenContext context = new AccessTokenContext();
        context.getParams().put(OAuthConstants.VALIDITY_PERIOD, 3600L);
        TokenIssuer.generateAccessToken(new Scope("default"), context);
        Assert.assertFalse(context.isSuccessful());
        Assert.assertEquals(context.getErrorObject(), OAuth2Error.SERVER_ERROR);

    }

    static class DummyTokenGenerator implements TokenGenerator {

        @Override
        public void generateAccessToken(AccessTokenContext context) {

        }

        @Override
        public boolean renewAccessTokenPerRequest() {

            return true;
        }
    }

    static class Dummy2TokenGenerator implements TokenGenerator {

        private String a;

        public Dummy2TokenGenerator(String a) {

            this.a = a;
        }

        @Override
        public void generateAccessToken(AccessTokenContext context) {

        }

        @Override
        public boolean renewAccessTokenPerRequest() {

            return true;
        }
    }

}
