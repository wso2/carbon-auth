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
import org.wso2.carbon.auth.scope.registration.rest.api.dto.PaginationDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeListDTO;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.createAdditionalScopeDTO;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.createAnotherScopeDTO;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.createDefaultScopeDTO;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.getNewMockedRequest;
import static org.wso2.carbon.auth.scope.registration.rest.api.ScopeTestObjectCreator.getNewScopesApiServiceImpl;

/**
 * Test class for ScopesApiServiceImpl
 */
public class ScopesApiServiceImplTest {

    @Test
    public void testAddGetUpdateDeleteScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();

        //register a new Scope
        ScopeDTO scopeDTO = createDefaultScopeDTO();
        Response scopeAddResponse = scopesApiService.registerScope(scopeDTO, getNewMockedRequest());
        Assert.assertEquals(scopeAddResponse.getStatus(), Response.Status.CREATED.getStatusCode());
        Assert.assertTrue(scopeDTO.equals(scopeAddResponse.getEntity()));

        //validate the new scope exists
        Response scopeExistsResponse = scopesApiService.isScopeExists(scopeDTO.getName(), getNewMockedRequest());
        Assert.assertEquals(scopeExistsResponse.getStatus(), Response.Status.OK.getStatusCode());

        //validate the added scope's contents
        Response scopeGetResponse = scopesApiService.getScope(scopeDTO.getName(), getNewMockedRequest());
        Assert.assertEquals(scopeGetResponse.getStatus(), Response.Status.OK.getStatusCode());
        Assert.assertTrue(scopeDTO.equals(scopeGetResponse.getEntity()));

        //update the scope and validate
        ScopeDTO scopeDTOUpdated = createDefaultScopeDTO();
        scopeDTOUpdated.setDescription("scope1-description-updated");

        Response scopeUpdateResponse = scopesApiService
                .updateScope(scopeDTOUpdated, scopeDTO.getName(), getNewMockedRequest());
        Assert.assertTrue(scopeDTOUpdated.equals(scopeUpdateResponse.getEntity()));
        Assert.assertEquals(scopeUpdateResponse.getStatus(), Response.Status.OK.getStatusCode());

        //delete the scope and validate
        Response scopeDeleteResponse = scopesApiService.deleteScope(scopeDTO.getName(), getNewMockedRequest());
        Assert.assertEquals(scopeDeleteResponse.getStatus(), Response.Status.NO_CONTENT.getStatusCode());

        scopeGetResponse = scopesApiService.getScope(scopeDTO.getName(), getNewMockedRequest());
        Assert.assertEquals(scopeGetResponse.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testRegisterExistingScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();

        //register a new Scope
        ScopeDTO scopeDTO = createDefaultScopeDTO();
        Response scopeAddResponse = scopesApiService.registerScope(scopeDTO, getNewMockedRequest());
        Assert.assertEquals(scopeAddResponse.getStatus(), Response.Status.CREATED.getStatusCode());
        Assert.assertTrue(scopeDTO.equals(scopeAddResponse.getEntity()));

        //register same scope again and validate
        scopeAddResponse = scopesApiService.registerScope(scopeDTO, getNewMockedRequest());
        Assert.assertEquals(scopeAddResponse.getStatus(), Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    public void testGetInvalidScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();
        Response scopeGetResponse = scopesApiService.getScope("invalid-name", getNewMockedRequest());
        Assert.assertEquals(scopeGetResponse.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testUpdateInvalidScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();
        Response updateResponse = scopesApiService
                .updateScope(createDefaultScopeDTO(), "invalid-name", getNewMockedRequest());
        Assert.assertEquals(updateResponse.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteInvalidScope() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();
        Response scopeDeleteResponse = scopesApiService.deleteScope("invalid-scope", getNewMockedRequest());
        Assert.assertEquals(scopeDeleteResponse.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testCheckInvalidScopeExists() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();
        //validate the new scope exists
        Response scopeExistsResponse = scopesApiService.isScopeExists("invalid-scope", getNewMockedRequest());
        Assert.assertEquals(scopeExistsResponse.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetScopes() throws Exception {
        ScopesApiServiceImpl scopesApiService = getNewScopesApiServiceImpl();

        //register a new Scope
        ScopeDTO scopeDTO = createDefaultScopeDTO();
        Response scopeAddResponse = scopesApiService.registerScope(scopeDTO, getNewMockedRequest());
        Assert.assertEquals(scopeAddResponse.getStatus(), Response.Status.CREATED.getStatusCode());
        Assert.assertTrue(scopeDTO.equals(scopeAddResponse.getEntity()));

        //register another new Scope
        ScopeDTO scopeDTO2 = createAdditionalScopeDTO();
        scopeAddResponse = scopesApiService.registerScope(scopeDTO2, getNewMockedRequest());
        Assert.assertEquals(scopeAddResponse.getStatus(), Response.Status.CREATED.getStatusCode());
        Assert.assertTrue(scopeDTO2.equals(scopeAddResponse.getEntity()));
        
        //register another new Scope
        ScopeDTO scopeDTO3 = createAnotherScopeDTO();
        scopeAddResponse = scopesApiService.registerScope(scopeDTO3, getNewMockedRequest());
        Assert.assertEquals(scopeAddResponse.getStatus(), Response.Status.CREATED.getStatusCode());
        Assert.assertTrue(scopeDTO3.equals(scopeAddResponse.getEntity()));
        
        //get scopes and validate
        Response scopesGetResponse = scopesApiService.getScopes(1, 1, getNewMockedRequest());
        Assert.assertEquals(scopesGetResponse.getStatus(), Response.Status.OK.getStatusCode());
        Assert.assertTrue(scopesGetResponse.getEntity() instanceof ScopeListDTO);
        ScopeListDTO listDTO = (ScopeListDTO) scopesGetResponse.getEntity();
        Assert.assertEquals(listDTO.getCount(), Integer.valueOf(1));
        PaginationDTO paginationDTO = listDTO.getPagination();
        Assert.assertEquals(paginationDTO.getLimit(), Integer.valueOf(1));
        Assert.assertEquals(paginationDTO.getOffset(), Integer.valueOf(1));
        Assert.assertEquals(paginationDTO.getTotal(), Integer.valueOf(3));
    }
}
