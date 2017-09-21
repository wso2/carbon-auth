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
public class AttributeMappingConfiguration {
    
    @Element(description = "Attribute name")
    private String attribute;
    
    @Element(description = "Mapped claim uri")
    private String claimUri;
    
    @Element(description = "Attribute is unique or not")
    private String unique;
    
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getClaimUri() {
        return claimUri;
    }

    public void setClaimUri(String claimUri) {
        this.claimUri = claimUri;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }
}
