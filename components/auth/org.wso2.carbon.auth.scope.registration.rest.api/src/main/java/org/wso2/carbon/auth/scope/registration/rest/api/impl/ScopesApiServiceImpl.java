package org.wso2.carbon.auth.scope.registration.rest.api.impl;

import org.wso2.carbon.auth.scope.registration.rest.api.*;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.*;


import java.util.List;
import org.wso2.carbon.auth.scope.registration.rest.api.NotFoundException;

import java.io.InputStream;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class ScopesApiServiceImpl extends ScopesApiService {
    @Override
    public Response deleteScope(String name
 , Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response getScope(String name
 , Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response getScopes(Integer startIndex
, Integer count
 , Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response isScopeExists(String name
 , Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response registerScope(scopeDTO scope
 , Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response updateScope(scopeToUpdateDTO scope
, String name
 , Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
