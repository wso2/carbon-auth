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

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of TokenRequestHandler interface
 */
public class TokenRequestHandlerImpl implements TokenRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(TokenRequestHandlerImpl.class);
    private ClientDAO clientDAO;

    public TokenRequestHandlerImpl(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public AccessTokenContext generateToken(String authorization, Map<String, String> queryParameters) {
        log.debug("Calling generateToken");
        AccessTokenContext context = new AccessTokenContext();

        String grantTypeValue = queryParameters.get(OAuthConstants.GRANT_TYPE_QUERY_PARAM);

        MutableBoolean haltExecution = new MutableBoolean(false);

        Optional<GrantHandler> grantHandler = GrantHandlerFactory.createGrantHandler(grantTypeValue, context,
                clientDAO, haltExecution);

        if (haltExecution.isFalse()) {
            grantHandler.ifPresent(handler -> handler.process(authorization, context, queryParameters));
        }

        return context;
    }
}
