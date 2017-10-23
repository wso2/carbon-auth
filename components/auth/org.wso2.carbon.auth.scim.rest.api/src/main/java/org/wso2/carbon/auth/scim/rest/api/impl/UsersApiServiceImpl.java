package org.wso2.carbon.auth.scim.rest.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.auth.core.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.impl.SCIMManager;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;
import org.wso2.carbon.auth.scim.rest.api.UsersApiService;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserSearchDTO;
import org.wso2.carbon.auth.scim.rest.api.util.ApiServiceUtils;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.UserResourceManager;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class UsersApiServiceImpl extends UsersApiService {
    private static final Log LOG = LogFactory.getLog(UsersApiServiceImpl.class);

    @Override
    public Response usersGet(Integer startIndex, Integer count, String filter, Request request)
                                                                                    throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.listWithGET(userManager, filter, startIndex, count,
                    null, null, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersIdDelete(String id, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.delete(id, userManager);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersIdGet(String id, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.get(id, userManager, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersIdPut(String id, UserDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.updateWithPUT(id, body.toString(), userManager, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersPost(UserDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.create(body.toString(), userManager, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response usersSearchPost(UserSearchDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            UserResourceManager userResourceManager = new UserResourceManager();
            SCIMResponse scimResponse = userResourceManager.listWithPOST(body.toString(), userManager);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager.");
        }
        return Response.serverError().build();
    }
}
