package org.wso2.carbon.auth.oauth.rest.api;

import org.wso2.carbon.auth.oauth.rest.api.*;
import org.wso2.carbon.auth.oauth.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.oauth.rest.api.dto.ErrorDTO;

import java.util.List;
import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class UserinfoApiService {
    public abstract Response userinfoGet(String authorization
 ,String schema
  ,Request request) throws NotFoundException;
}
