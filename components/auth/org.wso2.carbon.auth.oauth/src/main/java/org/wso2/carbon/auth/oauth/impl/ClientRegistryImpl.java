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
import org.wso2.carbon.auth.oauth.ClientRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ClientRegistry interface
 */
public class ClientRegistryImpl implements ClientRegistry {
    private Map<String, String> redirectUriStore = new HashMap<>();

    public String getRedirectUri(String clientId) {
        if (!StringUtils.isEmpty(clientId)) {
            String redirectUri =  redirectUriStore.get(clientId);

            if (redirectUri != null) {
                return redirectUri;
            }
        }

        return "";
    }

    public void registerRedirectUri(String clientId, String redirectUri) {
        redirectUriStore.put(clientId, redirectUri);
    }
}
