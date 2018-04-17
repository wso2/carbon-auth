/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenGenerator;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

/**
 * Abstract token issuer and persisting
 */
public class TokenIssuer {
    private static final Logger log = LoggerFactory.getLogger(TokenIssuer.class);

    static void generateAccessToken(Scope scope, AccessTokenContext context) {
        context.getParams().put(OAuthConstants.SCOPES, scope);
        OAuthConfiguration configuration = ServiceReferenceHolder.getInstance().getAuthConfigurations();
        String tokenGenClassName = configuration.getTokenGenerator();
        Class<?> tokenGenClass = null;
        TokenGenerator tokenGenImpl = null;
        try {
            tokenGenClass = Class.forName(tokenGenClassName);
            tokenGenImpl = (TokenGenerator) tokenGenClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Error instantiation class " + tokenGenClass, e);
            context.setErrorObject(OAuth2Error.SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            log.error("Requested grant type implementation not found", e);
            context.setErrorObject(OAuth2Error.SERVER_ERROR);
        }

        tokenGenImpl.generateAccessToken(context);
    }
}
