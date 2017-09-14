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

import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold user store configuration
 *
 */
public class UserStoreConfiguration {
    
    @Element(description = "ConnectorType")
    private String connectorType;
    
    @Element(description = "Property Map")
    private Map<String, String> propertyList = new HashMap<>();
    
    public UserStoreConfiguration() {
        connectorType = "JDBC";
        propertyList.put("dataSource", "WSO2_USER_DB");
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public Map<String, String> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(Map<String, String> propertyList) {
        this.propertyList = propertyList;
    }
    
    

}
