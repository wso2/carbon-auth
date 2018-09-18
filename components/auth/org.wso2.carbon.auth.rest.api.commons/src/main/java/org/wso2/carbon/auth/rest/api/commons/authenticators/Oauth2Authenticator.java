/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.rest.api.commons.authenticators;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.core.impl.UserNameMapperFactory;
import org.wso2.carbon.auth.rest.api.authenticators.RestAPIConstants;
import org.wso2.carbon.auth.rest.api.authenticators.api.RESTAPIAuthenticator;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.ExceptionCodes;
import org.wso2.carbon.auth.rest.api.authenticators.exceptions.RestAPIAuthSecurityException;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.token.introspection.impl.IntrospectionManagerImpl;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Oauth2 Authenticator for Oauth APIs
 */
public class Oauth2Authenticator implements RESTAPIAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(Oauth2Authenticator.class);

    private UserNameMapper userNameMapper;
    private IntrospectionManager introspectionManager;

    protected Oauth2Authenticator(UserNameMapper userNameMapper, IntrospectionManager introspectionManager) {

        this.userNameMapper = userNameMapper;
        this.introspectionManager = introspectionManager;
    }

    public Oauth2Authenticator() {

        introspectionManager = new IntrospectionManagerImpl();
        userNameMapper = UserNameMapperFactory.getInstance().getUserNameMapper();
    }

    @Override
    public boolean authenticate(Request request, Response responder, Method method) throws
            RestAPIAuthSecurityException {

        String authHeader = request.getHeader(RestApiConstants.AUTHORIZATION_HTTP_HEADER);
        if (authHeader.toLowerCase(Locale.US).startsWith(RestApiConstants.AUTH_TYPE_BEARER.toLowerCase(Locale.US))) {
            String header = authHeader.substring(RestApiConstants.AUTH_TYPE_BEARER.length()).trim();
            if (StringUtils.isNotEmpty(header)) {
                IntrospectionResponse introspectionResponse = introspectionManager.introspect(header);
                boolean authenticated = introspectionResponse.isActive();
                if (authenticated) {
                    request.setProperty(RestAPIConstants.LOGGED_IN_USER, introspectionResponse.getUsername());
                    try {
                        request.setProperty(RestAPIConstants.LOGGED_IN_PSEUDO_USER, userNameMapper
                                .getLoggedInPseudoNameFromUserID(introspectionResponse.getUsername()));
                    } catch (AuthException e) {
                        log.error("Error while creating PseudoName", e);
                        throw new RestAPIAuthSecurityException("Error while creating PseudoName", ExceptionCodes
                                .INTERNAL_ERROR);
                    }
                    return true;
                }
                return false;
            } else {
                throw new RestAPIAuthSecurityException("Missing 'Authorization : Bearer' header in the request.`",
                        ExceptionCodes.MALFORMED_AUTHORIZATION_HEADER_OAUTH);
            }

        } else {
            throw new RestAPIAuthSecurityException("Missing Authorization header in the request.`",
                    ExceptionCodes.MALFORMED_AUTHORIZATION_HEADER_OAUTH);

        }
    }
}
