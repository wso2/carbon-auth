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

package org.wso2.carbon.auth.rest.api.commons.authenticators;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.carbon.auth.rest.api.commons.services.TestDCRMService;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;

import java.lang.reflect.Method;

public class Oauth2AuthenticatorTest {

    @Test
    public void testAuthenticateWithValidToken() throws NoSuchMethodException, RestAPIAuthSecurityException {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        Oauth2Authenticator oauth2Authenticator = new Oauth2Authenticator(userNameMapper, introspectionManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Bearer YWRtaW46YWRtaW4=");
        Method method = TestDCRMService.class.getMethod("getApplication", null);
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);
        introspectionResponse.setUsername("admin");
        Mockito.when(introspectionManager.introspect("YWRtaW46YWRtaW4=")).thenReturn(introspectionResponse);
        Assert.assertTrue(oauth2Authenticator.authenticate(request, response, method));
    }

    @Test
    public void testAuthenticateWithInValidToken() throws NoSuchMethodException, RestAPIAuthSecurityException {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        Oauth2Authenticator oauth2Authenticator = new Oauth2Authenticator(userNameMapper, introspectionManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Bearer YWRtaW46YWRtaW4=");
        Method method = TestDCRMService.class.getMethod("getApplication", null);
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(false);
        Mockito.when(introspectionManager.introspect("YWRtaW46YWRtaW4=")).thenReturn(introspectionResponse);
        Assert.assertFalse(oauth2Authenticator.authenticate(request, response, method));
    }

    @Test
    public void testAuthenticateWithInValidHeader() throws NoSuchMethodException, RestAPIAuthSecurityException {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        Oauth2Authenticator oauth2Authenticator = new Oauth2Authenticator(userNameMapper, introspectionManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Bearer");
        Method method = TestDCRMService.class.getMethod("getApplication", null);
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(false);
        Mockito.when(introspectionManager.introspect("YWRtaW46YWRtaW4=")).thenReturn(introspectionResponse);
        try {
            oauth2Authenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Missing 'Authorization : Bearer' header in the request."));
        }
    }

    @Test
    public void testAuthenticateWithBasicToken() throws NoSuchMethodException, RestAPIAuthSecurityException {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        Oauth2Authenticator oauth2Authenticator = new Oauth2Authenticator(userNameMapper, introspectionManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaW4=");
        Method method = TestDCRMService.class.getMethod("getApplication", null);
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);
        introspectionResponse.setUsername("admin");
        Mockito.when(introspectionManager.introspect("YWRtaW46YWRtaW4=")).thenReturn(introspectionResponse);
        try {
            oauth2Authenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Missing Authorization header in the request."));
        }
        Mockito.verify(introspectionManager, Mockito.times(0)).introspect(Mockito.anyString());
    }

    @Test
    public void testAuthenticateWithValidTokenWhileExceptionThrownFromPseudo() throws NoSuchMethodException,
            RestAPIAuthSecurityException, AuthException {

        IntrospectionManager introspectionManager = Mockito.mock(IntrospectionManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        Oauth2Authenticator oauth2Authenticator = new Oauth2Authenticator(userNameMapper, introspectionManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Bearer YWRtaW46YWRtaW4=");
        Method method = TestDCRMService.class.getMethod("getApplication", null);
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);
        introspectionResponse.setUsername("admin");
        Mockito.when(userNameMapper.getLoggedInPseudoNameFromUserID("admin")).thenThrow(new AuthException());
        Mockito.when(introspectionManager.introspect("YWRtaW46YWRtaW4=")).thenReturn(introspectionResponse);
        try {
            oauth2Authenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Error while creating PseudoName"));
        }
    }
}
