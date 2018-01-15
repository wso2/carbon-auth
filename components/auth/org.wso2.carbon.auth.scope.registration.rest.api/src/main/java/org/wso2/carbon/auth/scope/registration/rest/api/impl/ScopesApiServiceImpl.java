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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.util.RestApiUtil;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManager;
import org.wso2.carbon.auth.scope.registration.rest.api.ApiResponseMessage;
import org.wso2.carbon.auth.scope.registration.rest.api.NotFoundException;
import org.wso2.carbon.auth.scope.registration.rest.api.ScopesApiService;

import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.util.ScopeMappingUtil;
import org.wso2.msf4j.Request;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.Response;

public class ScopesApiServiceImpl extends ScopesApiService {

    private static final Logger log = LoggerFactory.getLogger(ScopesApiServiceImpl.class);
    private ScopeManager scopeManager;

    public ScopesApiServiceImpl(ScopeManager scopeManager) {
        this.scopeManager = scopeManager;
    }

    @Override
    public Response deleteScope(String name, Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response getScope(String name, Request request) throws NotFoundException {
        Scope scope;
        try {
            scope = scopeManager.getScope(name);
            if (scope == null) {
                //todo: proper error response
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            ScopeDTO scopeDTO = ScopeMappingUtil.scopeModelToDTO(scope);
            return Response.ok().entity(scopeDTO).build();
        } catch (ScopeDAOException e) {
            log.error("Error while retrieving scope " + name, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestApiUtil.getInternalServerErrorDTO()).build();
        }
    }

    @Override
    public Response getScopes(Integer offset, Integer limit, Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response isScopeExists(String name, Request request) throws NotFoundException {

        boolean isScopeExists;

        try {
            isScopeExists = scopeManager.isScopeExists(name);
        } catch (ScopeDAOException e) {
            log.error("Error while checking existance of scope " + name, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestApiUtil.getInternalServerErrorDTO()).build();
        }
        if (isScopeExists) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response registerScope(ScopeDTO scope, Request request) throws NotFoundException {
        Scope addedScope;
        Scope scopeToAdd = ScopeMappingUtil.scopeDTOToModel(scope);
        try {
            addedScope = scopeManager.registerScope(scopeToAdd);
            URI location = new URI(RestApiConstants.RESOURCE_PATH_SCOPE.replace(RestApiConstants.SCOPENAME_PARAM,
                    scope.getName()));
            return Response.created(location).entity(addedScope).build();
        } catch (ScopeDAOException | URISyntaxException e) {
            log.error("Error while registering scope " + scope.getName(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestApiUtil.getInternalServerErrorDTO()).build();
        }
    }

    @Override
    public Response updateScope(ScopeDTO scope, String name, Request request) throws NotFoundException {
        return null;
    }
}
