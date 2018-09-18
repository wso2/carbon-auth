/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.wso2.carbon.auth.rest.api.commons.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Authenticator for authenticate Basic authentication Header
 */
public class BasicAuthenticator implements RESTAPIAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(BasicAuthenticator.class);
    private UserNameMapper userNameMapper;
    private UserStoreManager userStoreManager;

    protected BasicAuthenticator(UserNameMapper userNameMapper, UserStoreManager userStoreManager) {

        this.userNameMapper = userNameMapper;
        this.userStoreManager = userStoreManager;
    }

    public BasicAuthenticator() {

        this.userNameMapper = UserNameMapperFactory.getInstance().getUserNameMapper();
        this.userStoreManager = ServiceReferenceHolder.getInstance().getUserStoreManager();

    }

    @Override
    public boolean authenticate(Request request, Response responder, Method method) throws
            RestAPIAuthSecurityException {

        String authHeader = request.getHeader(RestApiConstants.AUTHORIZATION_HTTP_HEADER);
        if (authHeader != null) {
            String authEncoded = authHeader.substring(RestApiConstants.AUTH_TYPE_BASIC.length()).trim();
            if (StringUtils.isNotEmpty(authEncoded)) {
                byte[] decodedByte = authEncoded.getBytes(Charset.forName(RestApiConstants.CHARSET_UTF_8));
                String authDecoded = new String(Base64.getDecoder().decode(decodedByte),
                        Charset.forName(RestApiConstants.CHARSET_UTF_8));
                String[] authParts = authDecoded.split(":");
                String username = authParts[0];
                String password = authParts[1];
                try {
                    boolean authenticated = userStoreManager.doAuthenticate(username, password);
                    if (authenticated) {
                        request.setProperty(RestAPIConstants.LOGGED_IN_USER, username);
                        request.setProperty(RestAPIConstants.LOGGED_IN_PSEUDO_USER, userNameMapper
                                .getLoggedInPseudoNameFromUserID(username));
                        return true;
                    }
                    return false;
                } catch (UserStoreException e) {
                    log.error("Error while authenticating user ", e);
                    throw new RestAPIAuthSecurityException("Error while authenticating user");
                } catch (AuthException e) {
                    log.error("Error while creating PseudoName", e);
                    throw new RestAPIAuthSecurityException("Error while creating PseudoName", ExceptionCodes
                            .INTERNAL_ERROR);
                }
            } else {
                throw new RestAPIAuthSecurityException("Missing 'Authorization : Basic' header in the request.`",
                        ExceptionCodes.MALFORMED_AUTHORIZATION_HEADER_BASIC);
            }

        } else {
            throw new RestAPIAuthSecurityException("Missing Authorization header in the request.`",
                    ExceptionCodes.MALFORMED_AUTHORIZATION_HEADER_BASIC);

        }
    }
}
