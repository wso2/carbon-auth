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

import org.wso2.carbon.config.annotation.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold user store configuration
 *
 */
public class UserStoreConfiguration {
    
    @Element(description = "Connector Type")
    private String connectorType = "JDBC";
    
    @Element(description = "Property Map")
    private Map<String, Object> properties = new HashMap<>();
    
    @Element(description = "Read Only or not")
    private boolean readOnly = false;
    
    @Element(description = "Attribute Mapping")
    private List<AttributeConfiguration> attributes = new ArrayList<AttributeConfiguration>();
    
    public UserStoreConfiguration() {
        properties.put("dataSource", "WSO2_USER_DB");
        populateDefaultAttributes();
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<AttributeConfiguration> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeConfiguration> attributes) {
        this.attributes = attributes;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    private void populateDefaultAttributes() {
        AttributeConfiguration userNameAttribute = new AttributeConfiguration("userName", "Username", true, ".*", true);
        AttributeConfiguration nameAttribute = new AttributeConfiguration("givenName", "First Name", false, ".*", 
                false);
        AttributeConfiguration lastNameAttribute = new AttributeConfiguration("lastName", "Last Name", false, ".*", 
                false);
        AttributeConfiguration emailAttribute = new AttributeConfiguration("email", "Email", false, ".*", false);
        AttributeConfiguration addressAttribute = new AttributeConfiguration("address", "Address", false, ".*", false);
        AttributeConfiguration phoneAttribute = new AttributeConfiguration("phoneNumber", "Phone Number", false, ".*",
                false);
        AttributeConfiguration organization = new AttributeConfiguration("organization", "Organization", false, ".*",
                false);
        attributes.add(userNameAttribute);
        attributes.add(nameAttribute);
        attributes.add(lastNameAttribute);
        attributes.add(emailAttribute);
        attributes.add(phoneAttribute);
        attributes.add(addressAttribute);
        attributes.add(organization);
    }

}
