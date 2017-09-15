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

import org.wso2.carbon.auth.oauth.AuthCodeManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of AuthCodeManager interface
 */
public class AuthCodeManagerImpl implements AuthCodeManager {
    private Map<String, String> authCodeStore = new HashMap<>();

    public String generateCode(String clientId) {
        String code = UUID.randomUUID().toString();

        authCodeStore.put(clientId, code);

        return code;
    }

    public boolean isCodeValid(String code, String sentClientId) {
        String storedCode = authCodeStore.get(sentClientId);

        return storedCode != null && storedCode.equals(code);
    }
}
