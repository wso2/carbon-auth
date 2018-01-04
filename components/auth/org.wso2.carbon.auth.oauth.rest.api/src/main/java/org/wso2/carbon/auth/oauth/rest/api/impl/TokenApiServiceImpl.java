package org.wso2.carbon.auth.oauth.rest.api.impl;

import com.nimbusds.oauth2.sdk.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;
import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;
import org.wso2.msf4j.Request;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;


public class TokenApiServiceImpl extends TokenApiService {
    private static final Logger log = LoggerFactory.getLogger(TokenApiServiceImpl.class);
    private TokenRequestHandler tokenRequestHandler;

    public TokenApiServiceImpl(TokenRequestHandler tokenRequestHandler) {
        this.tokenRequestHandler = tokenRequestHandler;
    }

    @Override
    public Response tokenPost(String authorization, String grantType, String code, String redirectUri, String clientId,
            String refreshToken, String scope, String username, String password, Request request)
            throws NotFoundException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, grantType);
        queryParameters.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, clientId);
        queryParameters.put(OAuthConstants.REDIRECT_URI_QUERY_PARAM, redirectUri);
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scope);
        queryParameters.put(OAuthConstants.CODE_QUERY_PARAM, code);
        queryParameters.put(OAuthConstants.REFRESH_TOKEN_QUERY_PARAM, refreshToken);
        queryParameters.put(OAuthConstants.USERNAME, username);
        queryParameters.put(OAuthConstants.PASSWORD, password);

        try {
            AccessTokenContext context = tokenRequestHandler.generateToken(authorization, queryParameters);

            if (context.isSuccessful()) {
                return Response.ok().entity(context.getAccessTokenResponse()).build();
            } else {
                ErrorObject error = context.getErrorObject();
                return Response.status(error.getHTTPStatusCode()).build();
            }
        } catch (OAuthDAOException e) {
            log.error("DAO error while generating access token", e);
            return Response.status(e.getErrorHandler().getHttpStatusCode()).build();
        }
    }
}
