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

package org.wso2.carbon.auth.scim.rest.api.util;

import org.wso2.carbon.auth.scim.rest.api.SCIMRESTAPIConstants;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SCIMCharonInitializer {

    private static boolean initialized = false;
    
    /**
     * Initialize SCIM charon configurations
     */
    public static void initializeOnceSCIMConfigs() {
        if (!initialized) {
            synchronized (SCIMCharonInitializer.class) {
                // register endpoint URLs in AbstractResourceEndpoint since they are called with in the API
                registerEndpointURLs();
                // register the charon related configurations
                registerCharonConfigs();
                initialized = true;
            }
        }
    }

    /**
     * Register endpoint URLs in AbstractResourceEndpoint.
     */
    private static void registerEndpointURLs() {
        Map<String, String> endpointURLs = new HashMap<>();
        endpointURLs.put(SCIMConstants.USER_ENDPOINT, SCIMRESTAPIConstants.USERS_URL);
        endpointURLs.put(SCIMConstants.GROUP_ENDPOINT, SCIMRESTAPIConstants.GROUPS_URL);
        //register endpoint URLs in AbstractResourceEndpoint since they are called with in the API
        AbstractResourceManager.setEndpointURLMap(endpointURLs);
    }

    /*
     * This create the basic operational configurations for charon
     */
    private static void registerCharonConfigs() {
        //config charon
        //this values will be used in /ServiceProviderConfigResource endpoint
        CharonConfiguration.getInstance().setDocumentationURL(SCIMRESTAPIConstants.DOCUMENTATION_URL);
        CharonConfiguration.getInstance().setBulkSupport(false,
                SCIMRESTAPIConstants.MAX_OPERATIONS,
                SCIMRESTAPIConstants.MAX_PAYLOAD_SIZE);
        CharonConfiguration.getInstance().setSortSupport(false);
        CharonConfiguration.getInstance().setETagSupport(false);
        CharonConfiguration.getInstance().setChangePasswordSupport(true);
        CharonConfiguration.getInstance().setFilterSupport(true, SCIMRESTAPIConstants.MAX_RESULTS);
        CharonConfiguration.getInstance().setPatchSupport(false);
        CharonConfiguration.getInstance().setCountValueForPagination(SCIMRESTAPIConstants.COUNT_FOR_PAGINATION);

        Object[] auth1 = { SCIMRESTAPIConstants.AUTHENTICATION_SCHEMES_NAME_1,
                SCIMRESTAPIConstants.AUTHENTICATION_SCHEMES_DESCRIPTION_1,
                SCIMRESTAPIConstants.AUTHENTICATION_SCHEMES_SPEC_URI_1,
                SCIMRESTAPIConstants.AUTHENTICATION_SCHEMES_DOCUMENTATION_URL_1,
                SCIMRESTAPIConstants.AUTHENTICATION_SCHEMES_TYPE_1,
                SCIMRESTAPIConstants.AUTHENTICATION_SCHEMES_PRIMARY_1 };
        ArrayList<Object[]> authList = new ArrayList<Object[]>();
        authList.add(auth1);
        CharonConfiguration.getInstance().setAuthenticationSchemes(authList);
    }
}
