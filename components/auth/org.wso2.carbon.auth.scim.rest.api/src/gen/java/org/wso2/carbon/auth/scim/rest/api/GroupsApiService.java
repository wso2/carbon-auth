package org.wso2.carbon.auth.scim.rest.api;

import org.wso2.carbon.auth.scim.rest.api.*;
import org.wso2.carbon.auth.scim.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.scim.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupListDTO;

import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class GroupsApiService {
    public abstract Response groupsGet(Integer startIndex
 ,Integer count
 ,String filter
  ,Request request) throws NotFoundException;
    public abstract Response groupsIdDelete(String id
  ,Request request) throws NotFoundException;
    public abstract Response groupsIdGet(String id
  ,Request request) throws NotFoundException;
    public abstract Response groupsIdPut(String id
 ,GroupDTO body
  ,Request request) throws NotFoundException;
    public abstract Response groupsPost(GroupDTO body
  ,Request request) throws NotFoundException;
}
