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
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.connector.testutil.Utils;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.callback.PasswordCallback;

public class LDAPUserStoreConnectorIT {
    private UserStoreConfiguration storeConfiguration;
    private String group = "ldapGroup";
    private String user = "ldapUser";
    private String pass = "ldapPass";
    private UserStoreConnector connector;
    private String userID;
    private String groupId;

    @BeforeMethod
    public void init() throws Exception {
        DataSourceService dataSourceService = Mockito.mock(DataSourceService.class);
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
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_GROUP_SEARCH_BASE, "ou=Groups,dc=example,dc=org");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_GROUP_ENTRY_OBJECT_CLASS, "groupOfNames");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_GROUP_ATTRIBUTE, "cn");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_GROUP_SEARCH_FILTER,
                "(&amp;(objectClass=groupOfNames)(cn=?))");
        properties.put(org.wso2.carbon.auth.core.Constants.LDAP_GROUP_LIST_FILTER, "(objectClass=groupOfNames)");

        storeConfiguration.setLdapProperties(properties);

        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(configProvider.getConfigurationObject(UserStoreConfiguration.class))
                .thenReturn(storeConfiguration);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);

        ServiceReferenceHolder.getInstance().setDataSourceService(dataSourceService);

        connector = new LDAPUserStoreConnector();
        connector.init(storeConfiguration);
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
    public void testListConnectorUserIds() throws Exception {
        String user1 = "user1";
        String user2 = "user2";
        String nickNameValue = "commonName";

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user1));
        attributes.add(new Attribute(Constants.USER_DISPLAY_NAME_URI, nickNameValue));
        String userID1 = Utils.addUser(connector, attributes);
        Assert.assertEquals(user1, userID1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user2));
        attributes.add(new Attribute(Constants.USER_DISPLAY_NAME_URI, nickNameValue));
        String userID2 = Utils.addUser(connector, attributes);
        Assert.assertEquals(user2, userID2);

        List<String> uids = connector.listConnectorUserIds(Constants.USER_DISPLAY_NAME_URI, nickNameValue, 0, -1);
        Assert.assertEquals(uids.size(), 2);

        connector.deleteUser(userID1);
        connector.deleteUser(userID2);
    }

    @Test
    public void testGetUserAttributeValues() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);

        try {
            attributes = connector.getUserAttributeValues(userID);
            Assert.assertNotNull(attributes);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        Assert.assertEquals(5, attributes.size());
    }

    @Test
    public void testGetConnectorGroupId() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, group));
        groupId = connector.addGroup(attributes);
        Assert.assertEquals(group, groupId);

        String uid = connector.getConnectorGroupId(UserStoreConstants.GROUP_DISPLAY_NAME, group);
        Assert.assertEquals(group, uid);

        connector.deleteGroup(groupId);
    }

    @Test
    public void testListConnectorGroupIds() throws Exception {
        String group1 = "group1";
        String group2 = "group2";
        String nickNameValue = "commonName";

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, group1));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId1 = Utils.addGroup(connector, attributes);
        Assert.assertEquals(group1, groupId1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, group2));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId2 = Utils.addGroup(connector, attributes);
        Assert.assertEquals(group2, groupId2);

        List<String> uids = connector.listConnectorGroupIds(UserStoreConstants.NICKNAME, nickNameValue, 0, -1);
        Assert.assertEquals(2, uids.size());

        connector.deleteGroup(groupId1);
        connector.deleteGroup(groupId2);
    }

    @Test
    public void testGetGroupAttributeValues() throws Exception {
        String group1 = "group1";
        String nickNameValue = "commonName";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, group1));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId1 = Utils.addGroup(connector, attributes);
        Assert.assertEquals(group1, groupId1);

        try {
            attributes = connector.getGroupAttributeValues(groupId1);
            Assert.assertNotNull(attributes);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        Assert.assertEquals(6, attributes.size());
        connector.deleteGroup(groupId1);
    }

    @Test
    public void testIsUserInGroup() throws Exception {
        String user = "user1";
        String group = "group1";
        String userId = Utils.addUser(connector, user, null);
        String groupId = Utils.addGroup(connector, group);
        Assert.assertNotNull(userId);
        Assert.assertNotNull(groupId);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(groupId);

        boolean isIn = connector.isUserInGroup(userId, groupId);
        Assert.assertFalse(isIn);

        Utils.updateGroupsOfUser(connector, userId, ids);

        isIn = connector.isUserInGroup(userId, groupId);
        Assert.assertTrue(isIn);
        connector.deleteUser(userId);
        connector.deleteGroup(groupId);
    }

    @Test
    public void testAddUser() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);
    }

    @Test
    public void testUpdateUserAttributes() throws Exception {
        String user1 = "user1";
        String attCommonValue = "commonName";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user1));
        attributes.add(new Attribute(Constants.USER_DISPLAY_NAME_URI, attCommonValue));
        String userID1 = Utils.addUser(connector, attributes);
        Assert.assertEquals(user1, userID1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USER_DISPLAY_NAME_URI, attCommonValue));
        connector.updateUserAttributes(userID1, attributes);

        attributes = connector.getUserAttributeValues(userID1);
        for (Attribute attribute : attributes) {
            if (Constants.USER_DISPLAY_NAME_URI.equals(attribute.getAttributeUri())) {
                Assert.assertEquals(attCommonValue, attribute.getAttributeValue());
                return;
            }
        }
        connector.deleteUser(userID1);
    }

    @Test
    public void testAddDeleteGroup() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, group));
        groupId = connector.addGroup(attributes);
        Assert.assertEquals(group, groupId);

        connector.deleteGroup(groupId);
    }

    @Test
    public void testAddGroups() throws Exception {
        Map<String, List<Attribute>> attributes = new HashMap<>();
        List<Attribute> groupAttr;
        for (int i = 0; i < 5; i++) {
            groupAttr = new ArrayList<>();
            String groupName = "group_" + i;
            groupAttr.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, groupName));
            attributes.put(groupName, groupAttr);
        }

        Map<String, String> groupIds = null;
        try {
            groupIds = connector.addGroups(attributes);
            Assert.assertNotNull(groupIds);
            Assert.assertEquals(5, groupIds.size());
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        groupIds.forEach((String key, String value) -> {
            try {
                List<Attribute> attr = connector.getGroupAttributeValues(value);
                Assert.assertNotNull(attr);
                Assert.assertTrue(attr.size() > 0);
            } catch (UserStoreConnectorException e) {
                Assert.fail("Exception not expected");
            }
        });

        for (int i = 0; i < 5; i++) {
            String groupName = "group_" + i;
            connector.deleteGroup(groupName);
        }
    }

    @Test
    public void testUpdateGroupAttributes() throws Exception {
        String group1 = "group1";
        String nickNameValue = "commonName";
        String newNickNameValue = "commonName";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.GROUP_DISPLAY_NAME, group1));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId1 = Utils.addGroup(connector, attributes);
        Assert.assertEquals(group1, groupId1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, newNickNameValue));
        connector.updateGroupAttributes(groupId1, attributes);

        attributes = connector.getGroupAttributeValues(groupId1);
        for (Attribute attribute : attributes) {
            if (UserStoreConstants.NICKNAME.equals(attribute.getAttributeUri())) {
                Assert.assertEquals(newNickNameValue, attribute.getAttributeValue());
                return;
            }
        }
        connector.deleteGroup(groupId1);
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
    public void testUpdateCredentials() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertEquals(user, userID);

        passwordCallback.setPassword("newAdminPass".toCharArray());
        connector.updateCredentials(userID, passwordCallback);

        Map passInfo = connector.getUserPasswordInfo(user);
        Assert.assertNotNull(passInfo);

        String hashAlgo = "SHA256";
        int iterationCount = 4096;
        int keyLength = 256;

        String salt = (String) passInfo.get(UserStoreConstants.PASSWORD_SALT);
        String persistedPass = (String) passInfo.get(UserStoreConstants.PASSWORD);

        PasswordHandler passwordHandler = new DefaultPasswordHandler();
        passwordHandler.setIterationCount(iterationCount);
        passwordHandler.setKeyLength(keyLength);
        String hashedPassword;
        char[] password = passwordCallback.getPassword();
        try {
            hashedPassword = passwordHandler.hashPassword(password, salt, hashAlgo);
        } catch (NoSuchAlgorithmException e) {
            throw new UserStoreConnectorException("Error while hashing the password.", e);
        }

        Assert.assertEquals(hashedPassword, persistedPass);
    }

    @Test
    public void testDeleteCredential() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = connector.addUser(attributes);
        Assert.assertEquals(user, userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertEquals(user, userID);

        connector.deleteCredential(userID);

        Map passInfo = connector.getUserPasswordInfo(user);
        Assert.assertNull(passInfo);
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
