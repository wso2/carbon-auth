/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.user.store.connector.ldap;

import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.AuthConfiguration;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.internal.ConnectorDataHolder;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.callback.PasswordCallback;

public class LDAPUserStoreConnectorIT {
    private UserStoreConfiguration storeConfiguration;
    private String user = "ldapUser";
    private String pass = "ldapPass";
    private UserStoreConnector connector;
    private String userID;

    @BeforeMethod
    public void init() throws Exception {
        DataSourceService dataSourceService = Mockito.mock(DataSourceService.class);
        AuthConfiguration authConfig = new AuthConfiguration();
        storeConfiguration = new UserStoreConfiguration();
        storeConfiguration.setConnectorType(UserStoreConstants.LDAP_CONNECTOR_TYPE);
        Map<String, Object> properties = new HashMap<>();
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_CONNECTION_URL, "ldap://localhost:389");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_CONNECTION_NAME, "cn=admin,dc=example,dc=org");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_CONNECTION_PASSWORD, "admin");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_USER_SEARCH_BASE, "ou=users,dc=example,dc=org");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_USER_ENTRY_OBJECT_CLASS, "identityPerson");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_USERNAME_ATTRIBUTE, "uid");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_USERNAME_SEARCH_FILTER,
                "(&amp;(objectClass=person)(uid=?))");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_USERNAME_LIST_FILTER, "(objectClass=person)");
        storeConfiguration.setLdapProperties(properties);
        authConfig.setUserStoreConfiguration(storeConfiguration);

        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(configProvider.getConfigurationObject(AuthConfiguration.class)).thenReturn(authConfig);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);

        ConnectorDataHolder.getInstance().setDataSourceService(dataSourceService);

        connector = new LDAPUserStoreConnector();
        connector.init(storeConfiguration);
    }

    @Test
    public void testAddUser() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);
    }

    @Test
    public void testAddCredential() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertEquals(user, userID);
    }

    @Test
    public void testGetConnectorUserId() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertEquals(user, userID);

        String uid = connector.getConnectorUserId(Constants.USERNAME_URI, user);
        Assert.assertEquals(user, uid);
    }

    @Test
    public void testGetUserPasswordInfo() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertEquals(user, userID);

        Map passInfo = connector.getUserPasswordInfo(user);
        Assert.assertNotNull(passInfo.get(UserStoreConstants.PASSWORD));
        Assert.assertNotNull(passInfo.get(UserStoreConstants.PASSWORD_SALT));
    }

    @AfterMethod
    public void clean() throws Exception {
        connector.deleteUser(userID);
        try {
            String uid = connector.getConnectorUserId(Constants.USERNAME_URI, user);
            Assert.fail("Exception expected");
        } catch (UserNotFoundException e) {
            Assert.assertEquals("User not found with the given attribute", e.getMessage());
        }
    }

}
