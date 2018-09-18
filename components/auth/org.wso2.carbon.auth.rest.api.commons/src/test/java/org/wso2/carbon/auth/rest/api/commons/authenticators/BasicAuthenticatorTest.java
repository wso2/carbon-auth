/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.rest.api.commons.authenticators;

import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.carbon.auth.rest.api.commons.services.TestDCRMService;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;

import java.lang.reflect.Method;

public class BasicAuthenticatorTest {

    @Test
    public void testAuthenticateWithCorrectCredentials() throws NoSuchMethodException,
            RestAPIAuthSecurityException, UserStoreException {

        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userNameMapper, userStoreManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaW4=");
        Method method = new TestDCRMService().getClass().getMethod("getApplication", null);
        Mockito.when(userStoreManager.doAuthenticate("admin", "admin")).thenReturn(true);
        Assert.assertTrue(basicAuthenticator.authenticate(request, response, method));

    }

    @Test
    public void testAuthenticateWithIncorrectCredentials() throws NoSuchMethodException, RestAPIAuthSecurityException,
            UserStoreException {

        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userNameMapper, userStoreManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaW4x");
        Method method = new TestDCRMService().getClass().getMethod("getApplication", null);
        Mockito.when(userStoreManager.doAuthenticate("admin", "admin")).thenReturn(true);
        Assert.assertFalse(basicAuthenticator.authenticate(request, response, method));

    }

    @Test
    public void testAuthenticateWithUserStoreException() throws NoSuchMethodException, UserStoreException {

        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userNameMapper, userStoreManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaW4=");
        Method method = new TestDCRMService().getClass().getMethod("getApplication", null);
        Mockito.when(userStoreManager.doAuthenticate("admin", "admin")).thenThrow(new UserStoreException());
        try {
            basicAuthenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Error while authenticating user"));
        }
    }

    @Test
    public void testAuthenticateWithAuthException() throws NoSuchMethodException, AuthException {

        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userNameMapper, userStoreManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaW4=");
        Method method = new TestDCRMService().getClass().getMethod("getApplication", null);
        Mockito.when(userStoreManager.doAuthenticate("admin", "admin")).thenReturn(true);
        Mockito.when(userNameMapper.getLoggedInPseudoNameFromUserID("admin")).thenThrow(new AuthException(""));
        try {
            basicAuthenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Error while creating PseudoName"));
        }
    }

    @Test
    public void testAuthenticateInvalidHeader() throws NoSuchMethodException {

        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userNameMapper, userStoreManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic ");
        Method method = new TestDCRMService().getClass().getMethod("getApplication", null);
        try {
            basicAuthenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Missing 'Authorization : Basic' header in the request.`"));
        }
    }

    @Test
    public void testAuthenticateMissingHeader() throws NoSuchMethodException {

        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        UserNameMapper userNameMapper = Mockito.mock(UserNameMapper.class);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userNameMapper, userStoreManager);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Method method = new TestDCRMService().getClass().getMethod("getApplication", null);
        try {
            basicAuthenticator.authenticate(request, response, method);
            Assert.fail();
        } catch (RestAPIAuthSecurityException e) {
            Assert.assertTrue(e.getMessage().contains("Missing Authorization header in the request.`"));
        }
    }

}
