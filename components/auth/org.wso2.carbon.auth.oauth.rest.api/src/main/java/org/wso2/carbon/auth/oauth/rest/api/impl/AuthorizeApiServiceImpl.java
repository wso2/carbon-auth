package org.wso2.carbon.auth.oauth.rest.api.impl;

import com.nimbusds.oauth2.sdk.ErrorObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.AuthCodeManager;
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
    private AuthCodeManager authCodeManager;

    public AuthorizeApiServiceImpl(AuthCodeManager authCodeManager) {
        this.authCodeManager = authCodeManager;
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

        AuthResponseContext requestState = authCodeManager.generateCode(queryParameters);

        if (requestState.isSuccessful()) {
            String parsedUri = requestState.getRedirectUri().toString();
            return generateAuthResponse(parsedUri, requestState.getAuthCode(), requestState.getState());
        } else {
            String parsedUri = requestState.getRedirectUri().toString();
            ErrorObject errorObject = requestState.getErrorObject();
            return generateErrorResponse(parsedUri, errorObject.getCode(), requestState.getState());
        }
    }

    private Response generateErrorResponse(String redirectUri, String errorCondition, String state) {
        if (!StringUtils.isEmpty(redirectUri)) {
            String locationHeader = redirectUri + '?'
                    + OAuthConstants.ERROR_QUERY_PARAM + '=' + errorCondition;

            if (!StringUtils.isEmpty(state)) {
                locationHeader += '&' + OAuthConstants.STATE_QUERY_PARAM + '=' + state;
            }

            return Response.status(Response.Status.FOUND).
                    header(OAuthConstants.LOCATION_HEADER, locationHeader).build();
        }

        throw new IllegalStateException("Valid redirectUri has not been provided");
    }

    private Response generateAuthResponse(String redirectUri, String code, String state) {
        if (!StringUtils.isEmpty(redirectUri)) {
            String locationHeader = redirectUri + '?'
                    + OAuthConstants.CODE_QUERY_PARAM + '=' + code;

            if (!StringUtils.isEmpty(state)) {
                locationHeader += '&' + OAuthConstants.STATE_QUERY_PARAM + '=' + state;
            }

            return Response.status(Response.Status.FOUND).
                    header(OAuthConstants.LOCATION_HEADER, locationHeader).build();
        }

        throw new IllegalStateException("Valid redirectUri has not been provided");
    }
}
