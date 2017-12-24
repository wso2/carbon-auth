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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.wso2.carbon.auth.oauth.AuthRequestHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dto.AuthResponseContext;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class AuthorizeApiServiceImplTest {
    @Mock
    AuthRequestHandler authRequestHandler;
    @Mock
    AuthResponseContext context;

    @Test
    public void authorizeGetTest() throws Exception {

        String responseType = "code";
        String clientId = "ASDFS585dDS8fd5";
        String redirectUri = "http://host";
        String scope = "default";
        String state = "active";
        Request request = PowerMockito.mock(Request.class);
        AuthResponseContext context = PowerMockito.mock(AuthResponseContext.class);
        AuthRequestHandler authRequestHandler = PowerMockito.mock(AuthRequestHandler.class);
        AuthorizeApiServiceImpl authorizeApiService = new AuthorizeApiServiceImpl(authRequestHandler);

        PowerMockito.when(context.getLocationHeaderValue()).thenReturn(redirectUri);
        PowerMockito.when(authRequestHandler.generateCode(Mockito.anyMap())).thenReturn(context);
        Response response = authorizeApiService
                .authorizeGet(responseType, clientId, redirectUri, scope, state, request);
        Assert.assertEquals(redirectUri, response.getStringHeaders().getFirst(OAuthConstants.LOCATION_HEADER));
    }
}
