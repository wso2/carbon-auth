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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold user store configuration
 *
 */
public class UserStoreConfiguration {
    
    @Element(description = "ConnectorType")
    private String connectorType;
    
    @Element(description = "Property Map")
    private Map<String, String> properties = new HashMap<>();
    
    @Element(description = "Attribute Mapping")
    private List<AttributeMappingConfiguration> attributeMappings = new ArrayList<AttributeMappingConfiguration>();
    
    public UserStoreConfiguration() {
        connectorType = "JDBC";
        properties.put("dataSource", "WSO2_USER_DB");
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<AttributeMappingConfiguration> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<AttributeMappingConfiguration> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }
    
    

}
