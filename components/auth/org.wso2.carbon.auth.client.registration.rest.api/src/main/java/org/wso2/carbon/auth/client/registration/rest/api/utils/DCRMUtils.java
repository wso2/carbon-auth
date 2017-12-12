/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.client.registration.rest.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class DCRMUtils {

    private static final Logger log = LoggerFactory.getLogger(DCRMUtils.class);

    public static boolean isRedirectionUriValid(String redirectUri) {

        if (log.isDebugEnabled()) {
            log.debug("Validating uri: " + redirectUri);
        }

        if (isBlank(redirectUri)) {
            log.error("The redirection URI is either null or blank.");
            return false;
        }

        try {
            //Trying to parse the URI, just to verify the URI syntax is correct.
            new URI(redirectUri);
        } catch (URISyntaxException e) {
            String errorMessage = "The redirection URI: " + redirectUri + ", is not a valid URI.";
            log.error(errorMessage, e);
            return false;
        }
        return true;
    }

    public static boolean isBlank(String input) {
        if (StringUtils.isBlank(input) || "null".equals(input.trim())) {
            return true;
        } else {
            return false;
        }
    }
}
