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

package org.wso2.carbon.auth.rest.api.commons.util;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.dto.ErrorDTO;
import org.wso2.carbon.auth.rest.api.commons.services.TestDCRMService;
import org.wso2.carbon.auth.rest.api.commons.services.TestIntrospectService;
import org.wso2.carbon.auth.rest.api.commons.services.TestScimUsersApiService;
import org.wso2.carbon.auth.rest.api.commons.services.TestScopeRegistrationApiService;
import org.wso2.carbon.auth.rest.api.commons.services.TestTokenApiService;
import org.wso2.msf4j.Request;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Test class for RestApiUtil
 */
public class RestApiUtilTest {
    @Test
    public void testGetLoggedInUsername() throws Exception {
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getProperty("LOGGED_IN_USER")).thenReturn("admin");
        String loggedInUser = RestApiUtil.getLoggedInUsername(request);
        Assert.assertEquals(loggedInUser, "admin");
    }

    @Test
    public void testGetInternalServerErrorDTO() throws Exception {
        ErrorDTO dto = RestApiUtil.getInternalServerErrorDTO();
        Assert.assertEquals(dto.getCode(), Long.valueOf(ExceptionCodes.INTERNAL_ERROR.getErrorCode()));
        Assert.assertEquals(dto.getDescription(), ExceptionCodes.INTERNAL_ERROR.getErrorDescription());
        Assert.assertEquals(dto.getMessage(), ExceptionCodes.INTERNAL_ERROR.getErrorMessage());
    }

    @Test
    public void testGetErrorDTO() throws Exception {
        ErrorDTO dto = RestApiUtil.getErrorDTO(ExceptionCodes.INTERNAL_ERROR);
        Assert.assertEquals(dto.getCode(), Long.valueOf(ExceptionCodes.INTERNAL_ERROR.getErrorCode()));
        Assert.assertEquals(dto.getDescription(), ExceptionCodes.INTERNAL_ERROR.getErrorDescription());
        Assert.assertEquals(dto.getMessage(), ExceptionCodes.INTERNAL_ERROR.getErrorMessage());
    }

    @Test
    public void testGetPaginationParams() throws Exception {
        Map<String, Integer> resultMap = RestApiUtil.getPaginationParams(10, 10, 1);
        Assert.assertNotNull(resultMap);
        Assert.assertTrue(resultMap.isEmpty());

        resultMap = RestApiUtil.getPaginationParams(-1, 10, 1);
        Assert.assertNotNull(resultMap);
        Assert.assertTrue(resultMap.isEmpty());

        resultMap = RestApiUtil.getPaginationParams(10, 10, 15);
        Assert.assertNotNull(resultMap);
        //since there are no next page
        Assert.assertFalse(resultMap.containsKey(RestApiConstants.PAGINATION_NEXT_OFFSET));
        Assert.assertFalse(resultMap.containsKey(RestApiConstants.PAGINATION_NEXT_LIMIT));

        //previous page
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) == 0L);
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT) == 10L);

        resultMap = RestApiUtil.getPaginationParams(0, 10, 30);
        Assert.assertNotNull(resultMap);
        //next page
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_NEXT_OFFSET) == 10);
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_NEXT_LIMIT) == 10);

        //since there are no previous page
        Assert.assertFalse(resultMap.containsKey(RestApiConstants.PAGINATION_PREVIOUS_OFFSET));
        Assert.assertFalse(resultMap.containsKey(RestApiConstants.PAGINATION_PREVIOUS_LIMIT));

        resultMap = RestApiUtil.getPaginationParams(10, 10, 30);
        Assert.assertNotNull(resultMap);
        //next page
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_NEXT_OFFSET) == 20);
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_NEXT_LIMIT) == 10);

        //previous page
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) == 0L);
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT) == 10L);

        resultMap = RestApiUtil.getPaginationParams(5, 10, 30);
        Assert.assertNotNull(resultMap);
        //next page
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_NEXT_OFFSET) == 15);
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_NEXT_LIMIT) == 10);

        //previous page
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) == 0L);
        Assert.assertTrue(resultMap.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT) == 10L);
    }

    @Test
    public void testGetAllowedOrigin() {
        Assert.assertEquals(RestApiUtil.getAllowedOrigin("http://localhost:9443"), "http://localhost:9443");
    }

    @Test
    public void testGetDefinedMethodHeadersInSwaggerContentForDCRM() throws AuthException, NoSuchMethodException {
        Method method = TestDCRMService.class.getMethod("getApplication", null);
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/oauth2/dcr/v1.0/register/abcde-ddd");
        Assert.assertEquals(RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method), "GET, PUT, DELETE");
    }

    @Test
    public void testGetDefinedMethodHeadersInSwaggerContentForIntrospect() throws AuthException, NoSuchMethodException {
        Method method = TestIntrospectService.class.getMethod("introspect");
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/oauth2/introspect/v1.0/introspect");
        Assert.assertEquals(RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method), "POST");
    }

    @Test
    public void testGetDefinedMethodHeadersInSwaggerContentForTokenApi() throws AuthException, NoSuchMethodException {
        Method method = TestTokenApiService.class.getMethod("tokenPost");
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getUri()).thenReturn("/api/auth/oauth2/v1.0/token");
        Assert.assertEquals(RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method), "POST");
    }

    @Test
    public void testGetDefnedHeadersInSwaggerContentForScimApi() throws AuthException, NoSuchMethodException {
        Method method = TestScimUsersApiService.class.getMethod("usersIdDelete");
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getUri()).thenReturn("/api/identity/scim2/v1.0/Users/abceeee");
        Assert.assertEquals(RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method), "GET, PUT, DELETE");
    }

    @Test
    public void testGetDefnedHeadersInSwaggerContentForScopeApi() throws AuthException, NoSuchMethodException {
        Method method = TestScopeRegistrationApiService.class.getMethod("deleteScope");
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getUri()).thenReturn("/api/auth/scope-registration/v1.0/scopes/1234-122");
        Assert.assertEquals(RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method), "GET, PUT, "
                + "DELETE, " + "HEAD");
    }

    @Test
    public void testGetDefnedHeadersInSwaggerContentForInvalidContext() throws NoSuchMethodException {
        Method method = TestScopeRegistrationApiService.class.getMethod("deleteScope");
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getUri()).thenReturn("/api/auth/token/intro");

        try {
            RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method);
            Assert.fail("Fail due to didn't throw Exception");
        } catch (AuthException e) {
            Assert.assertTrue(e.getMessage().contains("Error while parsing the swagger definition"));

        }
    }

}
