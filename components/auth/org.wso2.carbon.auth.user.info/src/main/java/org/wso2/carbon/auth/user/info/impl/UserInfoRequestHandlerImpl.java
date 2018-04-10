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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.user.info.UserInfoResponseBuilder;
import org.wso2.carbon.auth.user.info.UserinfoRequestHandler;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;

/**
 * Implementation of TokenRequestHandler interface
 */
public class UserInfoRequestHandlerImpl implements UserinfoRequestHandler {

    private static final Log log = LogFactory.getLog(UserInfoRequestHandlerImpl.class);
    private IntrospectionManager introspectionManager;
    private UserInfoResponseBuilder userInfoResponseBuilder;

    public UserInfoRequestHandlerImpl(IntrospectionManager introspectionManager, UserInfoResponseBuilder
            userInfoResponseBuilder) {
        this.introspectionManager = introspectionManager;
        this.userInfoResponseBuilder = userInfoResponseBuilder;
    }

    @Override
    public String retrieveUserInfo(String authorization, String schema) throws UserInfoException {

        String token = retrieveToken(authorization, schema);
        IntrospectionResponse introspectionResponse = introspectionManager.introspect(token);

        if (!introspectionResponse.isActive()) {
            throw new UserInfoException("Invalid token", ExceptionCodes.INVALID_TOKEN);
        }

        validateScopes(introspectionResponse.getScope());

        return userInfoResponseBuilder.getResponseString(introspectionResponse);

    }

    private String retrieveToken(String authorization, String schema) throws UserInfoException {

        if (authorization == null) {
            throw new UserInfoException("Access token is missing");
        }

        String[] authzHeaderInfo = authorization.trim().split(" ");
        if (!"Bearer".equals(authzHeaderInfo[0])) {
            throw new UserInfoException("Bearer token is missing");
        }
        if (authzHeaderInfo.length == 1) {
            throw new UserInfoException("Access token is missing");
        }

        return authzHeaderInfo[1];
    }

    private void validateScopes(String scopes) throws UserInfoException {

        boolean validToken = false;

        if (scopes != null) {
            String scopeValues[] = scopes.split(" ");
            for (String scope : scopeValues) {
                if ("openid".equals(scope)) {
                    validToken = true;
                }
            }
        }

        if (!validToken) {
            throw new UserInfoException("Unsupported scope", ExceptionCodes.UNSUPPORTED_SCOPE);
        }
    }


}
