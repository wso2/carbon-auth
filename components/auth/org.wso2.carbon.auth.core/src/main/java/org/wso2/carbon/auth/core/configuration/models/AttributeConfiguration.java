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

package org.wso2.carbon.auth.core.configuration.models;

import org.wso2.carbon.kernel.annotations.Element;

/**
 * Class to hold user store attribute mappings
 *
 */
public class AttributeConfiguration {
    
    @Element(description = "Attribute name")
    private String attribute;
    
    @Element(description = "Attribute display name")
    private String displayName;
    
    @Element(description = "Attribute is required or not")
    private boolean required;
    
    @Element(description = "Attribute regex pattern")
    private String regex;
    
    @Element(description = "Attribute is unique or not")
    private boolean unique;
    
    public AttributeConfiguration(String attribute, String displayName, boolean required, 
            String regex, boolean unique) {
        this.attribute = attribute;
        this.displayName = displayName;
        this.required = required;
        this.regex = regex;
        this.unique = unique;
    }
    
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
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
    
}
