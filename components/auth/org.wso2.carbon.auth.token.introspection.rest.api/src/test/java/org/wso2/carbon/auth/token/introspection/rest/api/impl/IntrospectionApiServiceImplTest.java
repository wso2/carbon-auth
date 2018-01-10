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
package org.wso2.carbon.auth.token.introspection.rest.api.impl;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.token.introspection.IntrospectionException;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionContext;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.token.introspection.impl.IntrospectionManagerImpl;
import org.wso2.carbon.auth.token.introspection.impl.TokenValidatorHandlerImpl;
import org.wso2.carbon.auth.token.introspection.rest.api.IntrospectApiService;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.IntrospectionResponseDTO;
import org.wso2.carbon.auth.token.introspection.rest.api.factories.IntrospectApiServiceFactory;

import javax.ws.rs.core.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IntrospectionManagerImpl.class, TokenValidatorHandlerImpl.class, IntrospectionContext.class,
         })
public class IntrospectionApiServiceImplTest {

    @Mock
    TokenValidatorHandlerImpl tokenValidatorHandler;
    @Mock
    IntrospectionContext context;

    @Test
    public void testIntrospect() throws Exception {
        IntrospectApiService service = IntrospectApiServiceFactory.getIntrospectApi();
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setActive(true);

        PowerMockito.mockStatic(TokenValidatorHandlerImpl.class);
        PowerMockito.mockStatic(IntrospectionContext.class);

        PowerMockito.whenNew(TokenValidatorHandlerImpl.class).withNoArguments().thenReturn(tokenValidatorHandler);
        PowerMockito.whenNew(IntrospectionContext.class).withNoArguments().thenReturn(context);
        PowerMockito.when(context.getIntrospectionResponse()).thenReturn(introspectionResponse);

        Response response = service.introspect("ASDF5F6sd587fdgfsakjdd", null);
        IntrospectionResponseDTO dto  = (IntrospectionResponseDTO) response.getEntity();
        Assert.assertTrue(dto.getActive());

        PowerMockito.when(tokenValidatorHandler, "validate", Mockito.any(IntrospectionContext.class))
                .thenThrow(IntrospectionException.class);
        response = service.introspect("inactive", null);
        dto = (IntrospectionResponseDTO) response.getEntity();
        Assert.assertFalse(dto.getActive());
    }
}
