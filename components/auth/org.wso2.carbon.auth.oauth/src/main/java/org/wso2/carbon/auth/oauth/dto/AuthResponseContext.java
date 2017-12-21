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

package org.wso2.carbon.auth.oauth.dto;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.oauth.OAuthConstants;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO that stores response context information of the Authorization code grant flow
 */
public class AuthResponseContext {
    private URI redirectUri;
    private Map<String, String> queryParams = new HashMap<>();

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setRedirectUri(URI redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setErrorObject(ErrorObject errorObject) {
        queryParams.put(OAuthConstants.ERROR_QUERY_PARAM, errorObject.getCode());
    }

    public void setAuthCode(String authCode) {
        queryParams.put(OAuthConstants.CODE_QUERY_PARAM, authCode);
    }

    public void setState(State state) {
        if (state != null) {
            queryParams.put(OAuthConstants.STATE_QUERY_PARAM, state.getValue());
        }
    }

    public void setAccessToken(BearerAccessToken accessToken) {
        queryParams.put(OAuthConstants.ACCESS_TOKEN_QUERY_PARAM, accessToken.getValue());
    }

    public void setTokenType(AccessTokenType tokenType) {
        queryParams.put(OAuthConstants.TOKEN_TYPE_QUERY_PARAM, tokenType.getValue());
    }

    public void setExpiresIn(Long expiresIn) {
        queryParams.put(OAuthConstants.EXPERIES_IN_QUERY_PARAM, expiresIn.toString());
    }

    public void setScope(Scope scope) {
        queryParams.put(OAuthConstants.SCOPE_QUERY_PARAM, scope.toString());
    }

    public String getLocationHeaderValue() {
        if (redirectUri != null) {
            String uri = redirectUri.toString();

            if (!StringUtils.isEmpty(uri)) {
                StringBuilder locationHeader = new StringBuilder(uri + '?');

                boolean isFirstIterationDone = false;

                for (Map.Entry queryParam : queryParams.entrySet()) {
                    if (!isFirstIterationDone) {
                        isFirstIterationDone = true;
                    } else {
                        locationHeader.append('&');
                    }

                    locationHeader.append(queryParam.getKey());
                    locationHeader.append('=');
                    locationHeader.append(queryParam.getValue());
                }

                try {
                    return URLEncoder.encode(locationHeader.toString(), StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Unsupported encoding error for location header value", e);
                }
            }
        }

        throw new IllegalStateException("Valid redirectUri has not been provided");
    }
}
