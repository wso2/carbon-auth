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

package org.wso2.carbon.auth.scope.registration.rest.api.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.SCOPE_NAME_1;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.createDefaultScopeDTO;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.getNewErroneousScopesApiServiceImpl;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.getNewMockedRequest;

public class ScopesApiServiceImplExceptionTest {
    @Test
    public void testDeleteScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewErroneousScopesApiServiceImpl();
        Response deleteResponse = scopesApiService.deleteScope(SCOPE_NAME_1, getNewMockedRequest());
        Assert.assertEquals(deleteResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testGetScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewErroneousScopesApiServiceImpl();
        Response getResponse = scopesApiService.getScope(SCOPE_NAME_1, getNewMockedRequest());
        Assert.assertEquals(getResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testGetScopes() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewErroneousScopesApiServiceImpl();
        Response getScopesResponse = scopesApiService.getScopes(0, 10, getNewMockedRequest());
        Assert.assertEquals(getScopesResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testIsScopeExists() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewErroneousScopesApiServiceImpl();
        Response checkScopeExistResponse = scopesApiService.isScopeExists(SCOPE_NAME_1, getNewMockedRequest());
        Assert.assertEquals(checkScopeExistResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testRegisterScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewErroneousScopesApiServiceImpl();
        Response registerScopeResponse = scopesApiService.registerScope(createDefaultScopeDTO(), getNewMockedRequest());
        Assert.assertEquals(registerScopeResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testUpdateScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewErroneousScopesApiServiceImpl();
        Response updateScopeResponse = scopesApiService
                .updateScope(createDefaultScopeDTO(), SCOPE_NAME_1, getNewMockedRequest());
        Assert.assertEquals(updateScopeResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
