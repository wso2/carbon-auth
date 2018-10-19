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
import org.wso2.carbon.auth.user.store.claim.ClaimConstants;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.connector.testutil.Utils;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.security.auth.callback.PasswordCallback;

public class LDAPUserStoreConnectorIT {
    private UserStoreConfiguration storeConfiguration;
    private String group = "ldapGroup";
    private String user = "ldapUser";
    private String pass = "ldapPass";
    private String displayNameAttrName = "displayName";
    private String scimid = "scimid";

    private UserStoreConnector connector;
    private String userID;
    private String groupId;

    @BeforeMethod
    public void init() throws Exception {
        System.setProperty(ClaimConstants.CARBON_RUNTIME_DIR_PROP_NAME,
                System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                        + "resources" + File.separator + "runtime.home" + File.separator);
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
        userID = null;
        groupId = null;
    }

    @Test
    public void testGetConnectorUserId() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertNotNull(userID);

        String uid = connector.getConnectorUserId(Constants.USERNAME_URI, user);
        Assert.assertEquals(userID, uid);
    }

    @Test
    public void testListConnectorUserIds() throws Exception {
        String user1 = "user1";
        String user2 = "user2";
        String nickNameValue = "commonName";

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user1));
        attributes.add(new Attribute(displayNameAttrName, nickNameValue));
        String userID1 = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user2));
        attributes.add(new Attribute(displayNameAttrName, nickNameValue));
        String userID2 = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID2);

        List<String> uids = connector.listConnectorUserIds(displayNameAttrName, nickNameValue, 0, -1);
        Assert.assertEquals(uids.size(), 2);
    }

    @Test
    public void testGetUserAttributeValues() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);

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
        attributes.add(new Attribute(displayNameAttrName, group));
        groupId = Utils.addGroup(connector, attributes);
        Assert.assertNotNull(groupId);

        String gid = connector.getConnectorGroupId(displayNameAttrName, group);
        Assert.assertEquals(groupId, gid);
    }

    @Test
    public  void testListConnectorGroupIds() throws Exception {
        String group1 = "group1";
        String group2 = "group2";
        String nickNameValue = "commonName";

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(displayNameAttrName, group1));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId1 = Utils.addGroup(connector, attributes);
        Assert.assertNotNull(groupId1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(displayNameAttrName, group2));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId2 = Utils.addGroup(connector, attributes);
        Assert.assertNotNull(groupId2);

        List<String> uids = connector.listConnectorGroupIds(UserStoreConstants.NICKNAME, nickNameValue, 0, -1);
        Assert.assertEquals(2, uids.size());
    }

    @Test
    public void testGetGroupAttributeValues() throws Exception {
        String group1 = "group1";
        String nickNameValue = "commonName";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(displayNameAttrName, group1));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        groupId = Utils.addGroup(connector, attributes);
        Assert.assertNotNull(groupId);

        try {
            attributes = connector.getGroupAttributeValues(groupId);
            Assert.assertNotNull(attributes);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        Assert.assertEquals(7, attributes.size());
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
    }

    @Test
    public void testAddUser() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);
    }

    @Test
    public void testUpdateUserAttributes() throws Exception {
        String user1 = "user1";
        String attCommonValue = "commonName";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user1));
        attributes.add(new Attribute(displayNameAttrName, attCommonValue));
        String userID1 = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID1);

        attributes = new ArrayList<>();
        attributes.add(new Attribute(displayNameAttrName, attCommonValue));
        connector.updateUserAttributes(userID1, attributes);

        attributes = connector.getUserAttributeValues(userID1);
        for (Attribute attribute : attributes) {
            if (displayNameAttrName.equals(attribute.getAttributeUri())) {
                Assert.assertEquals(attCommonValue, attribute.getAttributeValue());
                return;
            }
        }
    }

    @Test
    public void testAddDeleteGroup() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(displayNameAttrName, group));
        groupId = Utils.addGroup(connector, attributes);
        Assert.assertNotNull(groupId);

        connector.deleteGroup(groupId);
        List<String> groupIdList = connector.listConnectorGroupIds(0, 100);
        Assert.assertFalse(groupIdList.contains(groupId));
    }

    @Test
    public void testAddGroups() throws Exception {
        Map<String, List<Attribute>> attributes = new HashMap<>();
        List<Attribute> groupAttr;
        for (int i = 0; i < 5; i++) {
            groupAttr = new ArrayList<>();
            String groupName = "group_" + i;
            groupAttr.add(new Attribute(displayNameAttrName, groupName));
            groupAttr.add(new Attribute(scimid, UUID.randomUUID().toString()));
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
    }

    @Test
    public void testUpdateGroupAttributes() throws Exception {
        String group1 = "group1";
        String nickNameValue = "commonName";
        String newNickNameValue = "commonName";
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(displayNameAttrName, group1));
        attributes.add(new Attribute(UserStoreConstants.NICKNAME, nickNameValue));
        String groupId1 = Utils.addGroup(connector, attributes);
        Assert.assertNotNull(groupId1);

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
    }

    @Test
    public void testAddCredential() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertNotNull(userID);
    }

    @Test
    public void testUpdateCredentials() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertNotNull(userID);

        passwordCallback.setPassword("newAdminPass".toCharArray());
        connector.updateCredentials(userID, passwordCallback);

        Map passInfo = connector.getUserPasswordInfo(userID);
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
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertNotNull(userID);

        connector.deleteCredential(userID);

        Map passInfo = connector.getUserPasswordInfo(userID);
        Assert.assertNull(passInfo);
    }

    @Test
    public void testGetUserPasswordInfo() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.USERNAME_URI, user));
        userID = Utils.addUser(connector, attributes);
        Assert.assertNotNull(userID);

        PasswordCallback passwordCallback = new PasswordCallback(pass, false);
        userID = connector.addCredential(userID, passwordCallback);
        Assert.assertNotNull(userID);

        Map passInfo = connector.getUserPasswordInfo(userID);
        Assert.assertNotNull(passInfo.get(UserStoreConstants.PASSWORD));
        Assert.assertNotNull(passInfo.get(UserStoreConstants.PASSWORD_SALT));
    }

    @AfterMethod
    public void clean() throws Exception {
        List<String> userIdsList = connector.listConnectorUserIds(0, 100);
        for (String uid : userIdsList) {
            connector.deleteUser(uid);
        }

        List<String> groupIdList = connector.listConnectorGroupIds(0, 100);
        for (String gid : groupIdList) {
            connector.deleteGroup(gid);
        }
    }

}
