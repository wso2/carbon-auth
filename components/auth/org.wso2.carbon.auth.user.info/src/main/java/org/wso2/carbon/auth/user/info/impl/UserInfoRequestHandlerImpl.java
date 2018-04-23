/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.user.info.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.user.info.UserInfoResponseBuilder;
import org.wso2.carbon.auth.user.info.UserinfoRequestHandler;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.carbon.auth.user.info.util.UserInfoUtil;

/**
 * Implementation of TokenRequestHandler interface
 */
public class UserInfoRequestHandlerImpl implements UserinfoRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(UserInfoRequestHandlerImpl.class);
    private IntrospectionManager introspectionManager;

    public UserInfoRequestHandlerImpl(IntrospectionManager introspectionManager) {
        this.introspectionManager = introspectionManager;
    }

    /**
     * @see UserinfoRequestHandler#retrieveUserInfo(String)
     */
    @Override
    public String retrieveUserInfo(String authorization) throws UserInfoException {

        String token = retrieveToken(authorization);
        IntrospectionResponse introspectionResponse = introspectionManager.introspect(token);

        if (!introspectionResponse.isActive()) {
            throw new UserInfoException("The access token is not active.", ExceptionCodes.INVALID_TOKEN);
        }

        if (!areScopesValid(introspectionResponse.getScope())) {
            throw new UserInfoException("Openid scope is missing.", ExceptionCodes.INVALID_TOKEN);
        }

        UserInfoResponseBuilder userInfoResponseBuilder = UserInfoUtil.getUserInfoResponseBuilder();
        return userInfoResponseBuilder.getResponseString(introspectionResponse);
    }

    /**
     * Retrieve token
     *
     * @param authorization Authorization header value
     * @return access token
     * @throws UserInfoException if failed to retrieve access token
     */
    private String retrieveToken(String authorization) throws UserInfoException {

        log.debug("Retrieving token from Authorization header value: {}", authorization);
        if (authorization == null) {
            throw new UserInfoException("Authorization header value is missing", ExceptionCodes.INVALID_TOKEN);
        }

        String[] authzHeaderInfo = authorization.trim().split(" ");
        if (!OAuthConstants.AUTH_TYPE_BEARER.equals(authzHeaderInfo[0])) {
            throw new UserInfoException("Bearer token is missing", ExceptionCodes.INVALID_TOKEN);
        }
        if (authzHeaderInfo.length == 1) {
            throw new UserInfoException("Access token is missing", ExceptionCodes.INVALID_TOKEN);
        }

        return authzHeaderInfo[1];
    }

    /**
     * Validate openid scope. Token's scopes should have openid scope.
     *
     * @param scopes Scope values
     * @return true if openid scope is present in given scopes
     * @throws UserInfoException if failed to validate scopes
     */
    private boolean areScopesValid(String scopes) throws UserInfoException {

        log.debug("Validating scopes values: {}", scopes);
        if (scopes != null) {
            String scopeValues[] = scopes.split(" ");
            for (String scope : scopeValues) {
                if (UserInfoConstants.OPENID.equals(scope)) {
                    return true;
                }
            }
        }

        return false;
    }

}
