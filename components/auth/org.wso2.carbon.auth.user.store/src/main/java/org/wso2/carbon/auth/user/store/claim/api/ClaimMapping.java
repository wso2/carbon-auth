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

package org.wso2.carbon.auth.user.store.claim.api;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



/**
 * Claim Mapping information
 */
public class ClaimMapping {
    protected Claim claim;
    private String mappedAttribute;
    private Map<String, String> mappedAttributes = new HashMap<>();

    public ClaimMapping() {
    }

    public ClaimMapping(Claim claim, String mappedAttribute) {
        this.claim = claim;
        this.mappedAttribute = mappedAttribute;
    }

    public Claim getClaim() {
        return this.claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public String getMappedAttribute() {
        return this.mappedAttribute;
    }

    public void setMappedAttribute(String mappedAttribute) {
        if (mappedAttribute != null) {
            int index = mappedAttribute.indexOf("/");
            if (index > 0) {
                String domainName = mappedAttribute.substring(0, index);
                if (!StringUtils.isBlank(domainName)) {
                    this.mappedAttributes.put(domainName.toUpperCase(Locale.getDefault()), mappedAttribute);
                }
            } else {
                this.mappedAttribute = mappedAttribute;
            }
        }

    }

    public void setMappedAttribute(String domainName, String mappedAttribute) {
        if (domainName != null && mappedAttribute != null) {
            this.mappedAttributes.put(domainName.toUpperCase(Locale.getDefault()), mappedAttribute);
        }

        if (domainName == null) {
            this.mappedAttribute = mappedAttribute;
        }

    }

    public String getMappedAttribute(String domainName) {
        return domainName != null ? this.mappedAttributes.get(domainName.toUpperCase(Locale.getDefault())) : null;
    }

    public Map<String, String> getMappedAttributes() {
        return this.mappedAttributes;
    }

    public void setMappedAttributes(Map<String, String> attrMap) {
        this.mappedAttributes = attrMap;
    }

    public void setMappedAttributeWithNoDomain(String mappedAttribute) {
        this.mappedAttribute = mappedAttribute;
    }
}
