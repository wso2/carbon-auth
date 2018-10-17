/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of TokenRequestHandler interface
 */
public class TokenRequestHandlerImpl implements TokenRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(TokenRequestHandlerImpl.class);
    private OAuthDAO oauthDAO;
    private ApplicationDAO applicationDAO;
    private ClientLookup clientLookup;
    private GrantHandlerFactory grantHandlerFactory;

    public TokenRequestHandlerImpl(OAuthDAO oauthDAO, ApplicationDAO applicationDAO, GrantHandlerFactory
            grantHandlerFactory) {

        this.oauthDAO = oauthDAO;
        this.applicationDAO = applicationDAO;
        this.grantHandlerFactory = grantHandlerFactory;
        this.clientLookup = new ClientLookupImpl(oauthDAO);
    }

    protected TokenRequestHandlerImpl(OAuthDAO oauthDAO, ApplicationDAO applicationDAO, ClientLookup clientLookup,
                                      GrantHandlerFactory grantHandlerFactory) {

        this.oauthDAO = oauthDAO;
        this.applicationDAO = applicationDAO;
        this.clientLookup = clientLookup;
        this.grantHandlerFactory = grantHandlerFactory;
    }

    @Override
    public AccessTokenContext generateToken(String authorization, Map<String, String> queryParameters)
            throws AuthException {

        log.debug("Calling generateToken");
        AccessTokenContext context = new AccessTokenContext();
        boolean isAuthorized;
        String grantTypeValue = queryParameters.get(OAuthConstants.GRANT_TYPE_QUERY_PARAM);

        if (StringUtils.isBlank(grantTypeValue)) {
            String error = "Provided grant type is empty";
            log.debug(error);
            context.setErrorObject(OAuth2Error.INVALID_REQUEST);
            return context;
        }

        MutableBoolean haltExecution = new MutableBoolean(false);

        Optional<GrantHandler> grantHandler = grantHandlerFactory.createGrantHandler(grantTypeValue, context,
                oauthDAO, applicationDAO, haltExecution);

        if (haltExecution.isFalse()) {
            if (grantHandler.isPresent()) {
                Application application;
                String clientId = clientLookup.getClientId(authorization, context, queryParameters, haltExecution);
                //client id can be null if it not present in header or payload
                if (clientId == null) {
                    log.debug("Provided client id not valid.");
                    context.setErrorObject(OAuth2Error.INVALID_CLIENT);
                    return context;
                }

                try {
                    application = applicationDAO.getApplication(clientId);
                } catch (ClientRegistrationDAOException e) {
                    throw new OAuthDAOException("Error getting client information from the DB", e);
                }
                //application can be null if non exist client id is used
                if (application == null) {
                    log.debug("Application for the provided client id not exist.");
                    context.setErrorObject(OAuth2Error.INVALID_CLIENT);
                    return context;
                }

                OAuthConfiguration authConfigs;
                long defaultValidityPeriod;
                if (application.getApplicationAccessTokenExpiryTime() > 0) {
                    defaultValidityPeriod = application.getApplicationAccessTokenExpiryTime();
                } else if (queryParameters.get(OAuthConstants.VALIDITY_PERIOD_QUERY_PARAM) != null) {
                    defaultValidityPeriod = Long
                            .parseLong(queryParameters.get(OAuthConstants.VALIDITY_PERIOD_QUERY_PARAM));
                } else {
                    authConfigs = ServiceReferenceHolder.getInstance().getAuthConfigurations();
                    defaultValidityPeriod = authConfigs.getDefaultTokenValidityPeriod();
                }
                context.getParams().put(OAuthConstants.TOKEN_TYPE, application.getTokenType());
                context.getParams().put(OAuthConstants.CLIENT_ID, clientId);
                context.getParams().put(OAuthConstants.APPLICATION_OWNER, application.getAuthUser());
                context.getParams().put(OAuthConstants.GRANT_TYPE, grantTypeValue);
                context.getParams().put(OAuthConstants.VALIDITY_PERIOD, defaultValidityPeriod);
                context.getParams().put(OAuthConstants.AUDIENCES, application.getAudiences());
                isAuthorized = grantHandler.get().isAuthorizedClient(application, grantTypeValue);
                if (!isAuthorized) {
                    String error = "Grant type is not allowed for the application";
                    log.debug(error);
                    context.setErrorObject(OAuth2Error.UNSUPPORTED_GRANT_TYPE);
                    return context;
                }
                if (!grantHandler.get().validateGrant(authorization, context, queryParameters)) {
                    return context;
                }
                if (!grantHandler.get().validateScopes(context)) {
                    return context;
                }
                grantHandler.get().process(authorization, context, queryParameters);
            }
        }

        return context;
    }
}
