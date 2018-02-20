/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.auth.scim.impl.constants;

/**
 * Class to hold Identity SCIM Constants.
 */
public class SCIMCommonConstants {

    public static final String USERS = "Users";
    public static final String GROUPS = "Groups";
    public static final String SERVICE_PROVIDER_CONFIG = "ServiceProviderConfig";
    public static final String RESOURCE_TYPE = "ResourceType";
    public static final String DEFAULT = "default";

    public static final int USER = 1;
    public static final int GROUP = 2;

    public static final String USERS_LOCATION = "/api/identity/scim2/v1.0/Users";
    public static final String GROUPS_LOCATION = "/api/identity/scim2/v1.0/Groups";
    

    //todo: domain separator need to be defined in common place
    public static final String DOMAIN_SEPARATOR = "/";

    public static final String INTERNAL_ERROR_MESSAGE = "Internal error occurred.";

    public class HTTPStatus {
        public static final int CONFLICT = 409;
        public static final int NOT_FOUND = 404;
        public static final int BAD_REQUEST = 400;
    }
}

