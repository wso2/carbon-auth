package org.wso2.carbon.auth.scim.rest.api.impl;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scim.SCIMManager;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.rest.api.GroupsApiService;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupDTO;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMRESTAPIUtils;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.GroupResourceManager;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

/**
 * REST API implementation class for SCIM groups
 * 
 */
public class GroupsApiServiceImpl extends GroupsApiService {
    private static final Logger LOG = LoggerFactory.getLogger(GroupsApiServiceImpl.class);

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
            LOG.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
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
            LOG.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
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
            LOG.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
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
            LOG.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
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
            LOG.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }
}
