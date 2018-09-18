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

package org.wso2.carbon.auth.rest.api.authenticators.interceptors;

import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.api.RESTAPIAuthenticator;
import org.wso2.carbon.auth.rest.api.authenticators.dto.RestAPIInfo;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.carbon.auth.rest.api.authenticators.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.rest.api.authenticators.services.MockRestApi;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.internal.MSF4JConstants;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RESTAPISecurityInterceptorTest {

    @BeforeClass
    public void setup() throws IOException {

        String swagger = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream
                ("scim-api.yaml"));
        Swagger swaggerModel = new Swagger20Parser().parse(swagger);
        RestAPIInfo restAPIInfo = new RestAPIInfo(swaggerModel.getBasePath(), swaggerModel, swagger);
        ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap().put(restAPIInfo.getBasePath(), restAPIInfo);
        ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap().put("/identity/token", null);
        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
    }

    @Test
    public void testSwaggerDefinitionService() throws NoSuchMethodException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.setStatus(Mockito.anyInt())).thenReturn(response);
        Mockito.when(response.setEntity(Mockito.any())).thenReturn(response);
        Mockito.when(response.setMediaType(Mockito.anyString())).thenReturn(response);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me?swagger.yaml");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Assert.assertFalse(restapiSecurityInterceptor.interceptRequest(request, response));
        Mockito.verify(response, Mockito.times(1)).setStatus(200);
        Mockito.verify(response, Mockito.times(1)).setMediaType("text/x-yaml");

    }

    @Test
    public void testSwaggerDefinitionServiceWithNothavingSwagger() throws NoSuchMethodException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.setStatus(Mockito.anyInt())).thenReturn(response);
        Mockito.when(response.setEntity(Mockito.any())).thenReturn(response);
        Mockito.when(response.setMediaType(Mockito.anyString())).thenReturn(response);
        Mockito.when(request.getUri()).thenReturn("/identity/token?swagger.yaml");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Assert.assertFalse(restapiSecurityInterceptor.interceptRequest(request, response));
        Mockito.verify(response, Mockito.times(1)).setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
                .getStatusCode());
    }

    @Test
    public void testAuthentication() throws NoSuchMethodException, RestAPIAuthSecurityException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaWa4x");
        Map<String, String> restapiAuthenticatorMap = new HashMap<>();
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_BASIC, DummyRestApiAuthenticator.class.getName());
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_OAUTH2, DummyRestApiAuthenticator.class.getName());
        ServiceReferenceHolder.getInstance().getSecurityConfiguration().getAuthenticator().put
                ("/api/identity/scim2/v1.0", restapiAuthenticatorMap);
        Assert.assertTrue(restapiSecurityInterceptor.interceptRequest(request, response));
    }

    @Test
    public void testAuthenticationFailure() throws NoSuchMethodException, RestAPIAuthSecurityException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaWa4x");
        Map<String, String> restapiAuthenticatorMap = new HashMap<>();
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_BASIC, DummyFalseRestApiAuthenticator.class.getName());
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_OAUTH2, DummyFalseRestApiAuthenticator.class.getName());
        ServiceReferenceHolder.getInstance().getSecurityConfiguration().getAuthenticator().put
                ("/api/identity/scim2/v1.0", restapiAuthenticatorMap);
        Assert.assertFalse(restapiSecurityInterceptor.interceptRequest(request, response));
    }

    @Test
    public void testAuthenticationFailureWithException() throws NoSuchMethodException, RestAPIAuthSecurityException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Basic YWRtaW46YWRtaWa4x");
        Map<String, String> restapiAuthenticatorMap = new HashMap<>();
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_BASIC, DummyExceptionRestApiAuthenticator.class
                .getName());
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_OAUTH2, DummyExceptionRestApiAuthenticator.class
                .getName());
        ServiceReferenceHolder.getInstance().getSecurityConfiguration().getAuthenticator().put
                ("/api/identity/scim2/v1.0", restapiAuthenticatorMap);
        Mockito.when(response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                .thenReturn(response);
        Mockito.when(response.setEntity(Mockito.any())).thenReturn(response);
        Assert.assertFalse(restapiSecurityInterceptor.interceptRequest(request, response));
    }

    @Test
    public void testAuthenticationForBasicAuthAuthenticatedAPIWithBearerHeader() throws NoSuchMethodException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Mockito.when(request.getHeader(RestAPIConstants.AUTHORIZATION)).thenReturn("Bearer YWRtaW46YWRtaWa4x");
        Map<String, String> restapiAuthenticatorMap = new HashMap<>();
        RESTAPIAuthenticator restapiAuthenticator = Mockito.mock(RESTAPIAuthenticator.class);
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_BASIC, restapiAuthenticator.getClass().getName());
        ServiceReferenceHolder.getInstance().getSecurityConfiguration().getAuthenticator().put
                ("/api/identity/scim2/v1.0", restapiAuthenticatorMap);
        Assert.assertTrue(restapiSecurityInterceptor.interceptRequest(request, response));
    }

    @Test
    public void testAuthenticationForBasicAuthenticationAPIWithoutHeader() throws NoSuchMethodException {

        RESTAPISecurityInterceptor restapiSecurityInterceptor = new RESTAPISecurityInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Map<String, String> restapiAuthenticatorMap = new HashMap<>();
        RESTAPIAuthenticator restapiAuthenticator = Mockito.mock(RESTAPIAuthenticator.class);
        restapiAuthenticatorMap.put(RestAPIConstants.AUTH_TYPE_BASIC, restapiAuthenticator.getClass().getName());
        ServiceReferenceHolder.getInstance().getSecurityConfiguration().getAuthenticator().put
                ("/api/identity/scim2/v1.0", restapiAuthenticatorMap);
        Assert.assertFalse(restapiSecurityInterceptor.interceptRequest(request, response));
        Mockito.verify(response, Mockito.times(1)).setStatus(401);
    }
}
