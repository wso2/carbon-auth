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

package org.wso2.carbon.auth.rest.api.authenticators.dto;

import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for Authentication Interceptor
 */
@Configuration(namespace = "wso2.carbon.authenticator", description = "Security Configuration Parameters")
public class SecurityConfiguration {

    @Element(description = "list of web clients (eg: 127.0.0.1:9443) to allow make requests to allow any web client)")
    private List<String> allowedHosts = Collections.singletonList("*");

    @Element(description = "Mapping of authenticator")
    private Map<String, Map<String, String>> authenticator = new HashMap<>();

    public List<String> getAllowedHosts() {

        return allowedHosts;
    }

    public Map<String, Map<String, String>> getAuthenticator() {

        return authenticator;
    }
}
