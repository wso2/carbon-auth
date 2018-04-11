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
package org.wso2.carbon.auth.oauth.rest.api.impl;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.testng.Assert;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class TokenApiServiceImplTest {
    @Test
    public void tokenPostTest() throws Exception {
        String authorization = "Basic 1234564asdsad";
        String grantType = "password";
        String code = "123asd546asd";
        String redirectUri = "https://redirect";
        String clientId = "ASD123asd4560";
        String refreshToken = "12asd5sa4dsad";
        String scope = "default";
        String username = "admin";
        String password = "admin";
        String clientSecret = "GDKDKFYSLSALDFKSPODM";
        long validityPeriod = 3600;
        String tokenResponse = "{'AccessToken':''a1s2d3f4g5g6}";
        Request request = PowerMockito.mock(Request.class);
        TokenRequestHandler tokenRequestHandler = PowerMockito.mock(TokenRequestHandler.class);
        TokenApiServiceImpl tokenApiService = new TokenApiServiceImpl(tokenRequestHandler);
        AccessTokenContext context = PowerMockito.mock(AccessTokenContext.class);

        PowerMockito.when(tokenRequestHandler.generateToken(Mockito.anyString(), Mockito.anyMap())).thenReturn(context);
        PowerMockito.when(context.isSuccessful()).thenReturn(true);
        AccessTokenResponse accessTokenResponse = PowerMockito.mock(AccessTokenResponse.class);
        Tokens tokens = PowerMockito.mock(Tokens.class);
        PowerMockito.when(accessTokenResponse.getTokens()).thenReturn(tokens);
        PowerMockito.when(tokens.toString()).thenReturn(tokenResponse);
        PowerMockito.when(context.getAccessTokenResponse()).thenReturn(accessTokenResponse);

        /*
        Response response = tokenApiService
                .tokenPost(authorization, grantType, code, redirectUri, clientId, refreshToken, scope, username,
                        password, request)thenReturn;
        String content = (String) response.getEntity();
        Assert.assertEquals(tokenResponse, content);
        */

        PowerMockito.when(context.isSuccessful()).thenReturn(false);
        ErrorObject errorObject = OAuth2Error.SERVER_ERROR;
        PowerMockito.when(context.getErrorObject()).thenReturn(errorObject);
        Response response = tokenApiService
                .tokenPost(authorization, grantType, code, redirectUri, clientId, clientSecret, refreshToken, scope,
                        username, password, validityPeriod, request);
        Assert.assertEquals(response.getStatus(), OAuth2Error.SERVER_ERROR.getHTTPStatusCode());

    }
}
