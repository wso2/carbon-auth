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

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.TokenRequestHandler;
import org.wso2.carbon.auth.oauth.dto.AuthCredentials;

import java.nio.charset.Charset;
import java.util.Base64;

public class TokenRequestHandlerImpl implements TokenRequestHandler {

    @Override
    public AuthCredentials parseAuthorizationHeader(String authorization) {
        AuthCredentials credentials = new AuthCredentials();

        if (!StringUtils.isEmpty(authorization)) {
            authorization = authorization.trim();
            if (authorization.indexOf(OAuthConstants.AUTH_TYPE_BASIC) == 0) {
                String encodedValue = authorization.substring(OAuthConstants.AUTH_TYPE_BASIC.length() - 1).trim();

                Base64.Decoder decoder = Base64.getDecoder();
                String decodedValue = new String(decoder.decode(encodedValue), Charset.defaultCharset());
                String[] splits = decodedValue.split(":");

                if (splits.length == 2) {
                    credentials.setSuccessful(true);
                    credentials.setConsumerKey(splits[0]);
                    credentials.setConsumerSecret(splits[1]);
                }
            }
        }

        return credentials;
    }
}
