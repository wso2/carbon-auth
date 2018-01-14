package org.wso2.carbon.auth.scope.registration.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.rest.api.commons.util.RestApiUtil;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManager;
import org.wso2.carbon.auth.scope.registration.rest.api.ApiResponseMessage;
import org.wso2.carbon.auth.scope.registration.rest.api.NotFoundException;
import org.wso2.carbon.auth.scope.registration.rest.api.ScopesApiService;

import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.msf4j.Request;

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
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response getScopes(Integer offset, Integer limit, Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(RestApiUtil.getInternalServerErrorDTO()).build();
    }

    @Override
    public Response isScopeExists(String name, Request request) throws NotFoundException {

        boolean isScopeExists = false;

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
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response updateScope(ScopeDTO scope, String name, Request request) throws NotFoundException {
        return null;
    }
}
