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

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO that stores context information related to the Access Token
 */
public class AccessTokenContext {
    private boolean isSuccessful;
    private ErrorObject errorObject;
    private AccessTokenResponse accessTokenResponse;
    private Map<String, String> params = new HashMap<>();

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public ErrorObject getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(ErrorObject errorObject) {
        this.errorObject = errorObject;
    }

    public AccessTokenResponse getAccessTokenResponse() {
        return accessTokenResponse;
    }

    public void setAccessTokenResponse(AccessTokenResponse accessTokenResponse) {
        this.accessTokenResponse = accessTokenResponse;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }
}
