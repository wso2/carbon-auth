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

import org.wso2.carbon.auth.oauth.ScopeValidator;
import org.wso2.carbon.auth.oauth.callback.ScopeValidatorCallback;
import org.wso2.carbon.auth.oauth.exception.OAuthScopeException;

/**
 * Default implemented role based scope validator
 */
public class RoleBasedScopeValidator implements ScopeValidator {

    public RoleBasedScopeValidator() {
    }

    @Override
    public void process(ScopeValidatorCallback callback) throws OAuthScopeException {
        //todo: validate scope against user roles
        callback.setApprovedScope(callback.getRequestedScopes());
    }
}
