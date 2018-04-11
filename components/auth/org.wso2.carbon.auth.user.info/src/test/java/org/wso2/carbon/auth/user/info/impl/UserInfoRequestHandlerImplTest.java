/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.user.info.impl;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.user.info.UserInfoResponseBuilder;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;

public class UserInfoRequestHandlerImplTest {


    @Test
    public void testRetrieveUserInfo() throws Exception {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        String token = "123-test-789";
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);
        introspectionResponse.setScope(UserInfoConstants.OPENID);

        Mockito.when(introspectionManager.introspect(token)).thenReturn(introspectionResponse);
        Mockito.when(userInfoResponseBuilder.getResponseString(introspectionResponse)).thenReturn("user-info");
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);
        String authHeaderValue = OAuthConstants.AUTH_TYPE_BEARER + " " + token;
        String userInfo = userInfoRequestHandler.retrieveUserInfo(authHeaderValue, UserInfoConstants.OPENID);

        Assert.assertNotNull(userInfo);
    }

    @Test
    public void testRetrieveUserInfoForInactiveToken() {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        String token = "123-test-789";
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(false);

        Mockito.when(introspectionManager.introspect(token)).thenReturn(introspectionResponse);
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);
        String authHeaderValue = OAuthConstants.AUTH_TYPE_BEARER + " " + token;

        try {
            userInfoRequestHandler.retrieveUserInfo(authHeaderValue, UserInfoConstants.OPENID);
            Assert.fail("For inactive token, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.INVALID_TOKEN);
        }
    }

    @Test
    public void testRetrieveTokenWhenAuthHeaderIsNull() {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);

        try {
            userInfoRequestHandler.retrieveUserInfo(null, UserInfoConstants.OPENID);
            Assert.fail("When Authorization header is not present, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.INVALID_REQUEST);
        }
    }

    @Test
    public void testRetrieveTokenWhenBearerIsMissing() {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);
        String token = "123-test-789";

        try {
            userInfoRequestHandler.retrieveUserInfo(token, UserInfoConstants.OPENID);
            Assert.fail("When Bearer is not present, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.INVALID_REQUEST);
        }
    }


    @Test
    public void testRetrieveTokenWhenTokenValueIsMissing() {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);

        try {
            userInfoRequestHandler.retrieveUserInfo(OAuthConstants.AUTH_TYPE_BEARER, UserInfoConstants.OPENID);
            Assert.fail("When token value is not present, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.INVALID_REQUEST);
        }
    }

    @Test
    public void testValidateScopesForNullScopes() {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        String token = "123-test-789";
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);
        introspectionResponse.setScope(null);

        Mockito.when(introspectionManager.introspect(token)).thenReturn(introspectionResponse);
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);
        String authHeaderValue = OAuthConstants.AUTH_TYPE_BEARER + " " + token;

        try {
            userInfoRequestHandler.retrieveUserInfo(authHeaderValue, UserInfoConstants.OPENID);
            Assert.fail("For invalid scope, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.UNSUPPORTED_SCOPE);
        }
    }

    @Test
    public void testValidateScopesForInvalidScope() {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserInfoResponseBuilder userInfoResponseBuilder = Mockito.mock(UserInfoResponseBuilder.class);
        String token = "123-test-789";
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);
        introspectionResponse.setScope("test");

        Mockito.when(introspectionManager.introspect(token)).thenReturn(introspectionResponse);
        UserInfoRequestHandlerImpl userInfoRequestHandler = new UserInfoRequestHandlerImpl
                (introspectionManager, userInfoResponseBuilder);
        String authHeaderValue = OAuthConstants.AUTH_TYPE_BEARER + " " + token;

        try {
            userInfoRequestHandler.retrieveUserInfo(authHeaderValue, UserInfoConstants.OPENID);
            Assert.fail("For invalid scope, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.UNSUPPORTED_SCOPE);
        }
    }

}
