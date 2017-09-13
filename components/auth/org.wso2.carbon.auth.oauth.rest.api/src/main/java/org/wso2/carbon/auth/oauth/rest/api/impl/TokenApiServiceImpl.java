package org.wso2.carbon.auth.oauth.rest.api.impl;

import org.wso2.carbon.auth.oauth.rest.api.*;
import org.wso2.carbon.auth.oauth.rest.api.dto.*;


import java.util.List;
import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;

import java.io.InputStream;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class TokenApiServiceImpl extends TokenApiService {
    @Override
    public Response tokenPost(String authorization
, String grantType
, String code
, String redirectUri
, String clientId
, String refreshToken
, String scope
  ,Request request) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
