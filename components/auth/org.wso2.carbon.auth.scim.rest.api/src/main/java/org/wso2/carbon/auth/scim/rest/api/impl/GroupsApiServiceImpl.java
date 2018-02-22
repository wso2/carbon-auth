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

package org.wso2.carbon.auth.scim.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scim.SCIMManager;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.rest.api.GroupsApiService;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupDTO;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMCharonInitializer;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMRESTAPIUtils;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.GroupResourceManager;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.auth.scim.rest.api.SCIMRESTAPIConstants.ERROR_SCIM_INITIALISATION;

/**
 * REST API implementation class for SCIM groups
 * 
 */
public class GroupsApiServiceImpl extends GroupsApiService {
    private static final Logger log = LoggerFactory.getLogger(GroupsApiServiceImpl.class);

    public GroupsApiServiceImpl() {
        SCIMCharonInitializer.initializeOnceSCIMConfigs();
    }

    @Override
    public Response groupsGet(Integer startIndex, Integer count, String filter, Request request)
            throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.listWithGET(userManager, filter, startIndex, count,
                    null, null, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error(ERROR_SCIM_INITIALISATION, e);
            return SCIMRESTAPIUtils.getSCIMInternalErrorResponse();
        }
    }

    @Override
    public Response groupsIdDelete(String id, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.delete(id, userManager);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error(ERROR_SCIM_INITIALISATION, e);
            return SCIMRESTAPIUtils.getSCIMInternalErrorResponse();
        }
    }

    @Override
    public Response groupsIdGet(String id, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.get(id, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error(ERROR_SCIM_INITIALISATION, e);
            return SCIMRESTAPIUtils.getSCIMInternalErrorResponse();
        }
    }

    @Override
    public Response groupsIdPut(String id, GroupDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            String bodyJsonString = SCIMRESTAPIUtils.getSerializedJsonStringFromBody(body);
            SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, bodyJsonString, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error(ERROR_SCIM_INITIALISATION, e);
            return SCIMRESTAPIUtils.getSCIMInternalErrorResponse();
        }
    }

    @Override
    public Response groupsPost(GroupDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            String bodyJsonString = SCIMRESTAPIUtils.getSerializedJsonStringFromBody(body);
            SCIMResponse scimResponse = groupResourceManager.create(bodyJsonString, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error(ERROR_SCIM_INITIALISATION, e);
            return SCIMRESTAPIUtils.getSCIMInternalErrorResponse();
        }
    }
}
