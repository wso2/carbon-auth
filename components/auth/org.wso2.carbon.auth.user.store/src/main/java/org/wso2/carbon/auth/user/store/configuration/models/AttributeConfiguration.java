/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.user.store.configuration.models;

import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;

/**
 * Class to hold user store attribute mappings
 *
 */
@Configuration(description = "Attribute Configurations")
public class AttributeConfiguration {
    
    @Element(description = "Attribute name")
    private String attributeName;

    @Element(description = "Attribute URI")
    private String attributeUri;
    
    @Element(description = "Attribute display name")
    private String displayName;
    
    @Element(description = "Attribute is required or not")
    private boolean required;
    
    @Element(description = "Attribute regex pattern")
    private String regex;

    @Element(description = "Attribute uniqueness")
    private Uniqueness uniqueness;

    public AttributeConfiguration() {
    }

    public AttributeConfiguration(String attributeName, String attributeUri, String displayName, boolean required,
            String regex, Uniqueness uniqueness) {
        this.attributeName = attributeName;
        this.attributeUri = attributeUri;
        this.displayName = displayName;
        this.required = required;
        this.regex = regex;
        this.uniqueness = uniqueness;
    }


    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setAttributeUri(String attributeUri) {
        this.attributeUri = attributeUri;
    }

    public String getAttributeUri() {
        return attributeUri;
    }

    public Uniqueness getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(Uniqueness uniqueness) {
        this.uniqueness = uniqueness;
    }
}
