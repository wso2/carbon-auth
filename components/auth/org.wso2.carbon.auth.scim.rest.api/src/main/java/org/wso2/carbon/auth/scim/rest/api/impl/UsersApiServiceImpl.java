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

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scim.SCIMManager;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;
import org.wso2.carbon.auth.scim.rest.api.SCIMRESTAPIConstants;
import org.wso2.carbon.auth.scim.rest.api.UsersApiService;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserSearchDTO;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMCharonInitializer;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMRESTAPIUtils;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.msf4j.Request;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 * REST API implementation class for SCIM users
 */
public class UsersApiServiceImpl extends UsersApiService {
    private static final Logger log = LoggerFactory.getLogger(GroupsApiServiceImpl.class);

    public UsersApiServiceImpl() {
        SCIMCharonInitializer.initializeOnceSCIMConfigs();
    }

    @Override
    public Response usersGet(Integer startIndex, Integer count, String filter, Request request)
            throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.listWithGET(userManager, filter, startIndex, count,
                    null, null, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersIdDelete(String id, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersIdGet(String id, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.get(id, userManager, null, null);

            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersIdPut(String id, UserDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            Gson gson = new Gson();
            String bodyJsonString = gson.toJson(body);
            SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, bodyJsonString, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersPost(UserDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            Gson gson = new Gson();
            String bodyJsonString = gson.toJson(body);
            SCIMResponse scimResponse = userResourceManager.create(bodyJsonString, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersSearchPost(UserSearchDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.listWithPOST(body.toString(), userManager);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager.");
        }
        return Response.serverError().build();
    }
}
