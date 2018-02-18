package org.wso2.carbon.auth.scim.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scim.SCIMManager;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.rest.api.MeApiService;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMCharonInitializer;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMRESTAPIUtils;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.MeResourceManager;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

/**
 * REST API implementation class for logged in user
 *
 */
public class MeApiServiceImpl extends MeApiService {
    private static final Logger log = LoggerFactory.getLogger(GroupsApiServiceImpl.class);

    public MeApiServiceImpl() {
        SCIMCharonInitializer.initializeOnceSCIMConfigs();
    }

    @Override
    public Response meDelete(Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.delete(userUniqueId, userManager);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response meGet(Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.get(userUniqueId, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response mePost(UserDTO body, Request request) throws NotFoundException {
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.create(body.toString(), userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    @Override
    public Response mePut(UserDTO body, Request request) throws NotFoundException {
        String userUniqueId = getUserId(request);
        UserManager userManager;
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.updateWithPUT(userUniqueId, body.toString(), userManager,
                    null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager");
        }
        return Response.serverError().build();
    }

    private String getUserId(Request request) {
        Object authzUser = request.getProperty("authzUser");
        String userUniqueId = null;
        if (authzUser instanceof String) {
            userUniqueId = (String) authzUser;
        } else {
            log.error("User id not found in the request.");
        }
        return userUniqueId;
    }
}
