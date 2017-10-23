package org.wso2.carbon.auth.scim.rest.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.auth.core.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.impl.SCIMManager;
import org.wso2.carbon.auth.scim.rest.api.*;
import org.wso2.carbon.auth.scim.rest.api.dto.*;


import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;

import java.io.InputStream;

import org.wso2.carbon.auth.scim.rest.api.util.ApiServiceUtils;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.GroupResourceManager;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;
import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class GroupsApiServiceImpl extends GroupsApiService {
    private static final Log LOG = LogFactory.getLog(GroupsApiServiceImpl.class);

    @Override
    public Response groupsGet(Integer startIndex
, Integer count
, String filter
  ,Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.listWithGET(userManager,  filter, startIndex, count,
                    null, null, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }
    @Override
    public Response groupsIdDelete(String id
  ,Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.delete(id, userManager);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }
    @Override
    public Response groupsIdGet(String id
  ,Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.get(id, userManager,  null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }
    @Override
    public Response groupsIdPut(String id, GroupDTO body
  ,Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.updateWithPUT(id, body.toString(), userManager,  null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }
    @Override
    public Response groupsPost(GroupDTO body
  ,Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            GroupResourceManager groupResourceManager = new GroupResourceManager();
            SCIMResponse scimResponse = groupResourceManager.create(body.toString(), userManager,  null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }
}
