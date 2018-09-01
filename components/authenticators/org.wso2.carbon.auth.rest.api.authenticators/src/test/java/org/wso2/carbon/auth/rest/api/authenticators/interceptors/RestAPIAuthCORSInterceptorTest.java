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
import org.wso2.carbon.auth.rest.api.authenticators.dto.RestAPIInfo;
import org.wso2.carbon.auth.rest.api.authenticators.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.rest.api.authenticators.services.MockRestApi;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.internal.MSF4JConstants;

import java.io.IOException;
import java.lang.reflect.Method;

public class RestAPIAuthCORSInterceptorTest {

    @BeforeClass
    public void setup() throws IOException {

        String swagger = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream
                ("scim-api.yaml"));
        Swagger swaggerModel = new Swagger20Parser().parse(swagger);
        RestAPIInfo restAPIInfo = new RestAPIInfo(swaggerModel.getBasePath(), swaggerModel, swagger);
        ServiceReferenceHolder.getInstance().getSwaggerDefinitionMap().put(restAPIInfo.getBasePath(), restAPIInfo);

    }

    @Test
    public void testCorsConfigurationWithOptions() throws NoSuchMethodException {

        RestAPIAuthCORSInterceptor restAPIAuthCORSInterceptor = new RestAPIAuthCORSInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.setHeader(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.when(response.setStatus(Mockito.anyInt())).thenReturn(response);
        Mockito.when(request.getHttpMethod()).thenReturn(RestAPIConstants.HTTP_OPTIONS);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Assert.assertFalse(restAPIAuthCORSInterceptor.interceptRequest(request, response));
    }

    @Test
    public void testCorsConfigurationWithOptionsOrigin() throws NoSuchMethodException {

        RestAPIAuthCORSInterceptor restAPIAuthCORSInterceptor = new RestAPIAuthCORSInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.setHeader(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.when(response.setStatus(Mockito.anyInt())).thenReturn(response);
        Mockito.when(request.getHttpMethod()).thenReturn(RestAPIConstants.HTTP_OPTIONS);
        Mockito.when(request.getHeader(RestAPIConstants.ORIGIN_HEADER)).thenReturn("https://localhost");
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Assert.assertFalse(restAPIAuthCORSInterceptor.interceptRequest(request, response));
    }


    @Test
    public void testCorsConfigurationWithNonOptions() throws NoSuchMethodException {

        RestAPIAuthCORSInterceptor restAPIAuthCORSInterceptor = new RestAPIAuthCORSInterceptor();
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.setHeader(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.when(response.setStatus(Mockito.anyInt())).thenReturn(response);
        Mockito.when(request.getHttpMethod()).thenReturn("GET");
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Me");
        Method method = MockRestApi.class.getMethod("meGet", Request.class);
        Mockito.when(request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME)).thenReturn(method);
        Assert.assertTrue(restAPIAuthCORSInterceptor.interceptRequest(request, response));
    }
}
