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

import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * Client Credentials grant handler
 */
public class ClientCredentialsGrantHandlerImpl implements GrantHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsGrantHandlerImpl.class);
    private ClientLookup clientLookup;

    ClientCredentialsGrantHandlerImpl(OAuthDAO oauthDAO) {
        clientLookup = new ClientLookupImpl(oauthDAO);
    }

    @Override
    public void process(String authorization, AccessTokenContext context, Map<String, String> queryParameters) {
        log.debug("Calling ClientCredentialsGrantHandlerImpl:process");
        try {
            ClientCredentialsGrant request = ClientCredentialsGrant.parse(queryParameters);
            String scope = queryParameters.get(OAuthConstants.SCOPE_QUERY_PARAM);
            processClientCredentialsGrantRequest(authorization, context, scope, request);
        } catch (ParseException e) {
            log.info("Error while parsing Client Credentials Grant request: ", e.getMessage());
            context.setErrorObject(e.getErrorObject());
        }
    }

    private void processClientCredentialsGrantRequest(String authorization, AccessTokenContext context,
                                             @Nullable String scopeValue, ClientCredentialsGrant request) {
        log.debug("Calling processClientCredentialsGrantRequest");
        MutableBoolean haltExecution = new MutableBoolean(false);

        clientLookup.getClientId(authorization, context, haltExecution);

        if (haltExecution.isTrue()) {
            return;
        }
        Scope scope;

        if (scopeValue != null) {
            scope = new Scope(scopeValue);
        } else {
            scope = new Scope(OAuthConstants.SCOPE_DEFAULT);
        }

        TokenGenerator.generateAccessToken(scope, context);
    }
}
