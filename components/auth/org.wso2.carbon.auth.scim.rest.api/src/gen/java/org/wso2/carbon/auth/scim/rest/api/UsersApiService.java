package org.wso2.carbon.auth.scim.rest.api;

import org.wso2.carbon.auth.scim.rest.api.*;
import org.wso2.carbon.auth.scim.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.scim.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserListDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserSearchDTO;

import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class UsersApiService {
    public abstract Response usersGet(Integer startIndex
 ,Integer count
 ,String filter
  ,Request request) throws NotFoundException;
    public abstract Response usersIdDelete(String id
  ,Request request) throws NotFoundException;
    public abstract Response usersIdGet(String id
  ,Request request) throws NotFoundException;
    public abstract Response usersIdPut(String id
 ,UserDTO body
  ,Request request) throws NotFoundException;
    public abstract Response usersPost(UserDTO body
  ,Request request) throws NotFoundException;
    public abstract Response usersSearchPost(UserSearchDTO body
  ,Request request) throws NotFoundException;
}
