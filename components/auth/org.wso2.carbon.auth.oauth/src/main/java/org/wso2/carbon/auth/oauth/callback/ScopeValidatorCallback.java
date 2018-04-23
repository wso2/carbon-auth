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
package org.wso2.carbon.auth.oauth.callback;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.Scope;

/**
 * Contain the information to validate requested scopes
 */
public class ScopeValidatorCallback {
    private boolean isSuccessful;
    private ErrorObject errorObject;
    private String authUser;
    private Scope requestedScopes;
    private Scope approvedScope;

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public Scope getRequestedScopes() {
        return requestedScopes;
    }

    public void setRequestedScopes(Scope requestedScopes) {
        this.requestedScopes = requestedScopes;
    }

    public Scope getApprovedScope() {
        return approvedScope;
    }

    public void setApprovedScope(Scope approvedScope) {
        this.approvedScope = approvedScope;
    }

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
}
