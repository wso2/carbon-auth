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

package org.wso2.carbon.auth.user.store.configuration.models;

import org.wso2.carbon.auth.core.Constants;
import org.wso2.carbon.auth.user.store.configuration.DefaultAttributes;
import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold user store configuration
 */

@Configuration(namespace = "wso2.carbon.auth.user.store", description = "Auth Configuration Parameters")
public class UserStoreConfiguration {

    @Element(description = "Connector Type")
    private String connectorType = "JDBC";

    @Element(description = "Super user ID")
    private String superUser = "admin";

    @Element(description = "Super user pass")
    private String superUserPass = "admin";

    @Element(description = "Super user group")
    private String superUserGroup = "admin";

    @Element(description = "JDBC Property Map")
    private Map<String, Object> jdbcProperties = new HashMap<>();

    @Element(description = "LDAP Property Map")
    private Map<String, Object> ldapProperties = new HashMap<>();

    @Element(description = "Read Only or not")
    private boolean readOnly = false;

    @Element(description = "Attribute Mapping")
    private List<AttributeConfiguration> attributes = new ArrayList<AttributeConfiguration>();

    @Element(description = "Password hash algorithm")
    private String hashAlgo = "SHA256";

    @Element(description = "Password iteration count")
    private int iterationCount = 4096;

    @Element(description = "Password key length")
    private int keyLength = 256;

    public UserStoreConfiguration() {
        populateJDBCDefaultProperties();
        populateLDAPDefaultProperties();
        populateDefaultAttributes();

    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
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
        for (DefaultAttributes attribute : DefaultAttributes.values()) {
            AttributeConfiguration attributeConfiguration = new AttributeConfiguration(attribute.getAttributeName(),
                    attribute.getAttributeUri(), attribute.getDisplayName(), attribute.isRequired(),
                    attribute.getRegex(), attribute.getUniqueness());
            attributes.add(attributeConfiguration);
        }
    }

    private void populateJDBCDefaultProperties() {
        jdbcProperties.put(Constants.DATASOURCE, "WSO2_UM_DB");
    }

    private void populateLDAPDefaultProperties() {
        ldapProperties.put(Constants.LDAP_CONNECTOR_CLASS,
                "org.wso2.carbon.auth.user.store.connector.ldap.LDAPUserStoreConnector");
        ldapProperties.put(Constants.LDAP_CONNECTION_URL, "ldap://localhost:10389");
        ldapProperties.put(Constants.LDAP_CONNECTION_NAME, "uid=admin,ou=system");
        ldapProperties.put(Constants.LDAP_CONNECTION_PASSWORD, "admin");
        ldapProperties.put(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapProperties.put(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
        ldapProperties.put(Constants.LDAP_USER_SEARCH_BASE, "ou=Users,dc=wso2,dc=org");
        ldapProperties.put(Constants.LDAP_USER_ENTRY_OBJECT_CLASS, "identityPerson");
        ldapProperties.put(Constants.LDAP_USERNAME_ATTRIBUTE, "uid");
        ldapProperties.put(Constants.LDAP_USERNAME_SEARCH_FILTER, "(&amp;(objectClass=person)(uid=?))");
        ldapProperties.put(Constants.LDAP_USERNAME_LIST_FILTER, "(objectClass=person)");
        ldapProperties.put(Constants.LDAP_GROUP_SEARCH_BASE, "ou=Groups,dc=wso2,dc=org");
        ldapProperties.put(Constants.LDAP_GROUP_ENTRY_OBJECT_CLASS, "groupOfNames");
        ldapProperties.put(Constants.LDAP_GROUP_ATTRIBUTE, "cn");
        ldapProperties.put(Constants.LDAP_GROUP_SEARCH_FILTER, "(&amp;(objectClass=groupOfNames)(cn=?))");
        ldapProperties.put(Constants.LDAP_GROUP_LIST_FILTER, "(objectClass=groupOfNames)");
    }

    public String getSuperUser() {
        return superUser;
    }

    public void setSuperUser(String superUser) {
        this.superUser = superUser;
    }

    public String getSuperUserPass() {
        return superUserPass;
    }

    public void setSuperUserPass(String superUserPass) {
        this.superUserPass = superUserPass;
    }

    public String getHashAlgo() {
        return hashAlgo;
    }

    public void setHashAlgo(String hashAlgo) {
        this.hashAlgo = hashAlgo;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public Map<String, Object> getJdbcProperties() {
        return jdbcProperties;
    }

    public void setJdbcProperties(Map<String, Object> jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    public Map<String, Object> getLdapProperties() {
        return ldapProperties;
    }

    public void setLdapProperties(Map<String, Object> ldapProperties) {
        this.ldapProperties = ldapProperties;
    }

    public String getSuperUserGroup() {

        return superUserGroup;
    }

    public void setSuperUserGroup(String superUserGroup) {

        this.superUserGroup = superUserGroup;
    }
}
