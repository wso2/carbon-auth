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

package org.wso2.carbon.auth.user.store.configuration;

import org.wso2.carbon.auth.user.store.configuration.models.Uniqueness;

import static org.wso2.charon3.core.schema.SCIMConstants.CommonSchemaConstants;
import static org.wso2.charon3.core.schema.SCIMConstants.GroupSchemaConstants;
import static org.wso2.charon3.core.schema.SCIMConstants.UserSchemaConstants;

/**
 * This enum class holds default attributes that needs to be added to the DB during the startup
 */
public enum DefaultAttributes {

    //meta attributes (common for all types of resources (User/Group)
    META_ID(CommonSchemaConstants.ID, CommonSchemaConstants.ID_URI, "ID", true, ".*", Uniqueness.GLOBAL),
    META_CREATED(CommonSchemaConstants.CREATED, CommonSchemaConstants.CREATED_URI, "Created Time", true, ".*", Uniqueness.NONE),
    META_LAST_MODIFIED(CommonSchemaConstants.LAST_MODIFIED, CommonSchemaConstants.LAST_MODIFIED_URI,
            "Last Modified Time", true, ".*", Uniqueness.NONE),
    META_RESOURCE_TYPE(CommonSchemaConstants.RESOURCE_TYPE, CommonSchemaConstants.RESOURCE_TYPE_URI, "Resource Type",
            true, ".*", Uniqueness.NONE),

    //User attributes
    USER_USER_NAME(UserSchemaConstants.USER_NAME, UserSchemaConstants.USER_NAME_URI, "User Name", true, ".*", Uniqueness.SERVER),
    USER_DISPLAY_NAME(UserSchemaConstants.DISPLAY_NAME, UserSchemaConstants.DISPLAY_NAME_URI, "Display Name", false,
            ".*", Uniqueness.NONE),
    USER_PASSWORD(UserSchemaConstants.PASSWORD, UserSchemaConstants.PASSWORD_URI, "Password", true, ".*", Uniqueness.NONE),
    USER_FAMILY_NAME(UserSchemaConstants.FAMILY_NAME, UserSchemaConstants.FAMILY_NAME_URI, "Family Name", false, ".*", Uniqueness.NONE),
    USER_GIVEN_NAME(UserSchemaConstants.GIVEN_NAME, UserSchemaConstants.GIVEN_NAME_URI, "Given Name", false, ".*", Uniqueness.NONE),
    USER_EMAIL_PRIMARY("primaryEmail", UserSchemaConstants.EMAILS_PRIMARY_URI, "Primary Email", false, ".*", Uniqueness.SERVER),
    USER_EMAIL_WORK("workEmail", "urn:ietf:params:scim:schemas:core:2.0:User:emails.work", "Work Email", false, ".*", Uniqueness.NONE),
    USER_EMAIL_HOME("homeEmail", "urn:ietf:params:scim:schemas:core:2.0:User:emails.home", "Home Email", false,
            ".*", Uniqueness.NONE),

    //Group attributes
    GROUP_DISPLAY_NAME(GroupSchemaConstants.DISPLAY_NAME, GroupSchemaConstants.DISPLAY_NAME_URI, "Group Display Name",
            false, ".*", Uniqueness.SERVER);

    private final String attributeName;
    private final String attributeUri;
    private final String displayName;
    private final boolean required;
    private final String regex;
    private final Uniqueness uniqueness;

    /**
     * @param attributeName Name of the attribute
     * @param attributeUri  URI of the attribute
     * @param displayName   Display name of the attribute
     * @param required      Indicates whether attribute is required or not
     * @param regex         Regex to validate attribute value is valid or not
     * @param uniqueness    Whether the attribute is unique across server/globally or none
     */
    DefaultAttributes(String attributeName, String attributeUri, String displayName, boolean required, String regex,
            Uniqueness uniqueness) {
        this.attributeName = attributeName;
        this.attributeUri = attributeUri;
        this.displayName = displayName;
        this.required = required;
        this.regex = regex;
        this.uniqueness = uniqueness;
    }

    /**
     * Get attribute name
     * 
     * @return attribute name
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Get attribute URI
     * 
     * @return attribute URI
     */
    public String getAttributeUri() {
        return attributeUri;
    }

    /**
     * Get attribute display name
     * 
     * @return attribute display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get attribute regex
     * 
     * @return attribute regex
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Returns whether attribute is required
     * 
     * @return whether attribute is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Returns whether attribute is unique across server/globally or none
     * 
     * @return whether attribute is unique across server/globally or none
     */
    public Uniqueness getUniqueness() {
        return uniqueness;
    }
}
