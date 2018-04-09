package org.wso2.carbon.auth.oauth.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;
import org.wso2.carbon.auth.oauth.rest.api.UserinfoApiService;
import org.wso2.carbon.auth.rest.api.commons.dto.ErrorDTO;
import org.wso2.carbon.auth.rest.api.commons.util.RestApiUtil;
import org.wso2.carbon.auth.user.info.UserinfoRequestHandler;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

/**
 * Userinfo API implementation class
 */
public class UserinfoApiServiceImpl extends UserinfoApiService {

    private UserinfoRequestHandler userinfoRequestHandler;
    private static final Logger log = LoggerFactory.getLogger(UserinfoApiServiceImpl.class);

    public UserinfoApiServiceImpl(UserinfoRequestHandler userinfoRequestHandler) {
        this.userinfoRequestHandler = userinfoRequestHandler;
    }

    @Override
    public Response userinfoGet(String authorization, String schema, Request request) throws NotFoundException {

        String userInfo = null;
        try {
            userInfo = userinfoRequestHandler.retrieveUserInfo(authorization, schema);

        } catch (UserInfoException e) {
            String errorMessage = "Error while retrieving user information";
            ErrorDTO errorDTO = RestApiUtil.getErrorDTO(e.getErrorHandler());
            log.error(errorMessage, e);
            return Response.status(e.getErrorHandler().getHttpStatusCode()).entity(errorDTO).build();
        }

        return Response.ok().entity(userInfo).build();
    }


}
