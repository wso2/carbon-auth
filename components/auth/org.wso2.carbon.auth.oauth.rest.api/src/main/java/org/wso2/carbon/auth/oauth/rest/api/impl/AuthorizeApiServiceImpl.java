package org.wso2.carbon.auth.oauth.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.AuthRequestHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dto.AuthResponseContext;
import org.wso2.carbon.auth.oauth.rest.api.AuthorizeApiService;
import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;
import org.wso2.msf4j.Request;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;

public class AuthorizeApiServiceImpl extends AuthorizeApiService {
    private static final Logger log = LoggerFactory.getLogger(AuthorizeApiServiceImpl.class);
    private AuthRequestHandler authRequestHandler;

    public AuthorizeApiServiceImpl(AuthRequestHandler authRequestHandler) {
        this.authRequestHandler = authRequestHandler;
    }

    @Override
    public Response authorizeGet(String responseType, String clientId, String redirectUri,
                                 String scope, String state, Request request) throws NotFoundException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.RESPONSE_TYPE_QUERY_PARAM, responseType);
        queryParameters.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, clientId);
        queryParameters.put(OAuthConstants.REDIRECT_URI_QUERY_PARAM, redirectUri);
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scope);
        queryParameters.put(OAuthConstants.STATE_QUERY_PARAM, state);

        AuthResponseContext context = authRequestHandler.generateCode(queryParameters);

        return generateResponse(context);
    }

    private Response generateResponse(AuthResponseContext context) {
        String locationHeader = context.getLocationHeaderValue();

        return Response.status(Response.Status.FOUND).
                header(OAuthConstants.LOCATION_HEADER, locationHeader).build();
    }
}
