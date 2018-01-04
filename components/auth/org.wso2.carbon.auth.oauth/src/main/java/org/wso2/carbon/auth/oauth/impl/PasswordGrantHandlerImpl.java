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

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.Secret;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.mgt.impl.JDBCUserStoreManager;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * Password grant handler
 */
public class PasswordGrantHandlerImpl implements GrantHandler {
    private static final Logger log = LoggerFactory.getLogger(PasswordGrantHandlerImpl.class);
    private OAuthDAO oauthDAO;
    private ClientLookup clientLookup;

    PasswordGrantHandlerImpl(OAuthDAO oauthDAO) {
        this.oauthDAO = oauthDAO;
        clientLookup = new ClientLookupImpl(oauthDAO);
    }

    @Override
    public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters)
            throws OAuthDAOException {
        log.debug("Calling PasswordGrantHandlerImpl:process");
        try {
            ResourceOwnerPasswordCredentialsGrant request =
                    ResourceOwnerPasswordCredentialsGrant.parse(queryParameters);
            String scope = queryParameters.get(OAuthConstants.SCOPE_QUERY_PARAM);
            processPasswordGrantRequest(authorization, context, scope, request);
        } catch (ParseException e) {
            log.info("Error while parsing Password Grant request: ", e.getMessage());
            context.setErrorObject(e.getErrorObject());
        }
    }

    private void processPasswordGrantRequest(String authorization, AccessTokenContext context,
                                             @Nullable String scopeValue,
                                             ResourceOwnerPasswordCredentialsGrant request) throws OAuthDAOException {
        log.debug("calling processPasswordGrantRequest");
        MutableBoolean haltExecution = new MutableBoolean(false);

        clientLookup.getClientId(authorization, context, haltExecution);

        boolean authenticated = validateGrant(request);
        if (authenticated) {
            context.getParams().put("AUTH_USER", request.getUsername());
        } else {
            return;
        }
        //check CK empty
        //check CK state
        if (haltExecution.isTrue()) {
            return;
        }

        //TODO: Validate username and password sent in request
        Scope scope;

        if (scopeValue != null) {
            scope = new Scope(scopeValue);
        } else {
            scope = new Scope(OAuthConstants.SCOPE_DEFAULT);
        }

        TokenGenerator.generateAccessToken(scope, context);
    }

    private boolean validateGrant(ResourceOwnerPasswordCredentialsGrant request) {
        String username = request.getUsername();
        Secret password = request.getPassword();

        UserStoreManager jdbcUserStoreManager = new JDBCUserStoreManager();
        try {
            return jdbcUserStoreManager.doAuthenticate(username, password.getValue());
        } catch (UserStoreException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
