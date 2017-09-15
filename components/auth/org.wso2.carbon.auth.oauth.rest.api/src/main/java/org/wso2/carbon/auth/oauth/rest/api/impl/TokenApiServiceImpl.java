package org.wso2.carbon.auth.oauth.rest.api.impl;

import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.rest.api.ApiResponseMessage;
import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;


import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;


import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;


public class TokenApiServiceImpl extends TokenApiService {
    //private TokenRequestHandler tokenRequestHandler;

    public TokenApiServiceImpl(TokenRequestHandler tokenRequestHandler) {
        //this.tokenRequestHandler = tokenRequestHandler;
    }

    @Override
    public Response tokenPost(String authorization, String grantType, String code, String redirectUri,
                              String clientId, String refreshToken, String scope, Request request)
            throws NotFoundException {
        //AuthCredentials credentials = tokenRequestHandler.parseAuthorizationHeader(authorization);

        //if (!credentials.isSuccessful()) {
        //
        //}

        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
