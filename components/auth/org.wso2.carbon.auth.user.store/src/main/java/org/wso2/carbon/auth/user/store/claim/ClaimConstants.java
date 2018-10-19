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

package org.wso2.carbon.auth.user.store.claim;

import java.io.File;

/**
 * Claim related constants
 */
public class ClaimConstants {
    public static final String DEFAULT_CARBON_DIALECT = "http://wso2.org/claims";
    public static final String LOCAL_CLAIM_DIALECT_URI = DEFAULT_CARBON_DIALECT;
    public static final String CLAIM_CONFIG_FILE_PATH = File.separator + "conf" + File.separator + "claims.json";
    public static final String CARBON_RUNTIME_DIR_PROP_NAME = "wso2.runtime.path";
    public static final String DISPLAY_NAME_PROPERTY = "DisplayName";
    public static final String DESCRIPTION_PROPERTY = "Description";
    public static final String REQUIRED_PROPERTY = "Required";
    public static final String DISPLAY_ORDER_PROPERTY = "DisplayOrder";
    public static final String SUPPORTED_BY_DEFAULT_PROPERTY = "SupportedByDefault";
    public static final String REGULAR_EXPRESSION_PROPERTY = "RegEx";
    public static final String READ_ONLY_PROPERTY = "ReadOnly";

    public static final String DEFAULT_ATTRIBUTE = "DefaultAttribute";
    public static final String PRIMARY_DEFAULT_DOMAIN_NAME = "PRIMARY";

    public static final String SCIM_CORE_CLAIM_DIALECT = "urn:ietf:params:scim:schemas:core:2.0";
    public static final String SCIM_USER_CLAIM_DIALECT = "urn:ietf:params:scim:schemas:core:2.0:User";

    public static final String SCIM_CLAIM_CONFIG_DIALECTS = "Dialects";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_URI = "dialectURI";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM = "Claim";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_URI = "ClaimURI";

    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DISPLAY_NAME = "DisplayName";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_ATTRIBUTE_ID = "AttributeID";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DESCRIPTION = "Description";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DISPLAY_ORDER = "DisplayOrder";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_REQUIRED = "Required";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_SUPPORTED_BY_DEFAULT = "SupportedByDefault";
    public static final String SCIM_CLAIM_CONFIG_DIALECT_CLAIM_MAPPED_LOCAL_CLAIM = "MappedLocalClaim";

    public static final String SCIM_CLAIM_PROP_CLAIM_DISPLAY_NAME = "DisplayName";
    public static final String SCIM_CLAIM_PROP_CLAIM_DESCRIPTION = "Description";
    public static final String SCIM_CLAIM_PROP_CLAIM_DISPLAY_ORDER = "DisplayOrder";
    public static final String SCIM_CLAIM_PROP_CLAIM_REQUIRED = "Required";
    public static final String SCIM_CLAIM_PROP_CLAIM_SUPPORTED_BY_DEFAULT = "SupportedByDefault";
}
