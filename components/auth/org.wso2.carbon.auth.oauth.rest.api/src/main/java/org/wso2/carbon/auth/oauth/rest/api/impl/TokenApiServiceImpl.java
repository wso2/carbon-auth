package org.wso2.carbon.auth.oauth.rest.api.impl;

import com.nimbusds.oauth2.sdk.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.rest.api.NotFoundException;
import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;
import org.wso2.carbon.auth.oauth.rest.api.dto.TokenResponseDTO;
import org.wso2.carbon.auth.oauth.rest.api.utils.TokenMappingUtil;
import org.wso2.msf4j.Request;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 * Token API implementation class
 * 
 */
public class TokenApiServiceImpl extends TokenApiService {
    private static final Logger log = LoggerFactory.getLogger(TokenApiServiceImpl.class);
    private TokenRequestHandler tokenRequestHandler;

    public TokenApiServiceImpl(TokenRequestHandler tokenRequestHandler) {
        this.tokenRequestHandler = tokenRequestHandler;
    }

    @Override
    public Response tokenPost(String grantType, String authorization, String code, String redirectUri, String clientId,
            String clientSecret, String refreshToken, String scope, String username, String password,
            Long validityPeriod, Request request) throws NotFoundException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.GRANT_TYPE_QUERY_PARAM, grantType);
        queryParameters.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, clientId);
        queryParameters.put(OAuthConstants.CLIENT_SECRET_QUERY_PARAM, clientSecret);
        queryParameters.put(OAuthConstants.REDIRECT_URI_QUERY_PARAM, redirectUri);
        queryParameters.put(OAuthConstants.SCOPE_QUERY_PARAM, scope);
        queryParameters.put(OAuthConstants.CODE_QUERY_PARAM, code);
        queryParameters.put(OAuthConstants.REFRESH_TOKEN_QUERY_PARAM, refreshToken);
        queryParameters.put(OAuthConstants.USERNAME, username);
        queryParameters.put(OAuthConstants.PASSWORD, password);
        queryParameters.put(OAuthConstants.VALIDITY_PERIOD_QUERY_PARAM,
                validityPeriod == null ? null : String.valueOf(validityPeriod));

        try {
            AccessTokenContext context = tokenRequestHandler.generateToken(authorization, queryParameters);
            if (context.isSuccessful()) {
                TokenResponseDTO tokenResponseDTO = TokenMappingUtil
                        .tokenResponseToDTO(context.getAccessTokenResponse());
                return Response.ok().entity(tokenResponseDTO).build();
            } else {
                ErrorObject error = context.getErrorObject();
                return Response.status(error.getHTTPStatusCode()).build();
            }
        } catch (org.wso2.carbon.auth.core.exception.AuthException e) {
            log.error("DAO error while generating access token", e);
            return Response.status(e.getErrorHandler().getHttpStatusCode()).build();
        }
    }
}
