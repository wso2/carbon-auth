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
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.junit.Test;
import org.mockito.Mockito;
import org.testng.Assert;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.msf4j.Request;

import java.util.UUID;
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
        Request request = Mockito.mock(Request.class);
        TokenRequestHandler tokenRequestHandler = Mockito.mock(TokenRequestHandler.class);
        TokenApiServiceImpl tokenApiService = new TokenApiServiceImpl(tokenRequestHandler);
        AccessTokenContext context = Mockito.mock(AccessTokenContext.class);
        Mockito.when(context.isSuccessful()).thenReturn(true);
        Mockito.when(tokenRequestHandler.generateToken(Mockito.anyString(), Mockito.anyMap())).thenReturn(context);
        AccessTokenResponse accessTokenResponse = Mockito.mock(AccessTokenResponse.class);
        Tokens tokens = new Tokens(new BearerAccessToken(UUID.randomUUID().toString(), 3600L, new Scope("abcd")), new
                RefreshToken("avcd"));
        Mockito.when(accessTokenResponse.getTokens()).thenReturn(tokens);
        Mockito.when(context.getAccessTokenResponse()).thenReturn(accessTokenResponse);
        Response response = tokenApiService.tokenPost(authorization, grantType, code, redirectUri, clientId,
                clientSecret, refreshToken, scope,
                username, password, validityPeriod, request);
        Assert.assertEquals(response.getStatus(), 200);
        Mockito.when(context.isSuccessful()).thenReturn(false);
        ErrorObject errorObject = OAuth2Error.SERVER_ERROR;
        Mockito.when(context.getErrorObject()).thenReturn(errorObject);
        response = tokenApiService
                .tokenPost(authorization, grantType, code, redirectUri, clientId, clientSecret, refreshToken, scope,
                        username, password, validityPeriod, request);
        Assert.assertEquals(response.getStatus(), OAuth2Error.SERVER_ERROR.getHTTPStatusCode());
        Mockito.when(tokenRequestHandler.generateToken(Mockito.anyString(), Mockito.anyMap())).thenThrow(new
                AuthException("Error", ExceptionCodes.DAO_EXCEPTION));
        response = tokenApiService
                .tokenPost(authorization, grantType, code, redirectUri, clientId, clientSecret, refreshToken, scope,
                        username, password, validityPeriod, request);
        Assert.assertEquals(response.getStatus(), OAuth2Error.SERVER_ERROR.getHTTPStatusCode());
    }
}
