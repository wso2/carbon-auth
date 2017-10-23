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
import org.wso2.charon3.core.protocol.endpoints.MeResourceManager;
import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class MeApiServiceImpl extends MeApiService {
    private static final Log LOG = LogFactory.getLog(GroupsApiServiceImpl.class);

    @Override
    public Response meDelete( Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.delete(userUniqueId, userManager);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }
    @Override
    public Response meGet( Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.get(userUniqueId, userManager, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response mePost(UserDTO body, Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.create(body.toString(), userManager, null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();    }

    @Override
    public Response mePut(UserDTO body, Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.updateWithPUT(userUniqueId, body.toString(), userManager,
                    null, null);
            return ApiServiceUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            LOG.error("Error in initializing the CarbonAuthUserManager");
        }
        return Response.serverError().build();    }

    private String getUserId(Request request) {
        Object authzUser = request.getProperty("authzUser");
        String userUniqueId = null;
        if (authzUser instanceof String) {
            userUniqueId = (String) authzUser;
        } else {
            LOG.error("User id not found in the request.");
        }
        return userUniqueId;
    }
}
