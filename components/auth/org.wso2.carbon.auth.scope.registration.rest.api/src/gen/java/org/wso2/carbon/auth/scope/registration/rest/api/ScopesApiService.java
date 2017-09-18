package org.wso2.carbon.auth.scope.registration.rest.api;

import org.wso2.carbon.auth.scope.registration.rest.api.*;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.scope.registration.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeToUpdateDTO;

import java.util.List;
import org.wso2.carbon.auth.scope.registration.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class ScopesApiService {
    public abstract Response deleteScope(String name
 , Request request) throws NotFoundException;
    public abstract Response getScope(String name
 , Request request) throws NotFoundException;
    public abstract Response getScopes(Integer startIndex
 ,Integer count
 , Request request) throws NotFoundException;
    public abstract Response isScopeExists(String name
 , Request request) throws NotFoundException;
    public abstract Response registerScope(ScopeDTO scope
 , Request request) throws NotFoundException;
    public abstract Response updateScope(ScopeToUpdateDTO scope
 ,String name
 , Request request) throws NotFoundException;
}
