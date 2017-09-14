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
