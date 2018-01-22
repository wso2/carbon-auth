/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.auth.user.store.connector.jdbc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.AuthConfiguration;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.connector.testutil.Utils;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.internal.ConnectorDataHolder;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.security.auth.callback.PasswordCallback;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceReferenceHolder.class, SecretKeyFactory.class, LoggerFactory.class })
public class JDBCUserStoreConnectorTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(JDBCUserStoreConnectorTest.class);
    UserStoreConfiguration userStoreConfiguration;
    @Mock
    ServiceReferenceHolder serviceReferenceHolder;
    @Mock
    UserStoreConnector connector;
    @Mock
    DataSourceService dataSourceService;
    @Mock
    SecretKeyFactory secretKeyFactory;
    @Mock
    AuthConfiguration authConfiguration;

    String connectorUniqueId = null;

    @Before
    public void setup() throws Exception {
        super.init();
        super.setup();

        userStoreConfiguration = new UserStoreConfiguration();
        userStoreConfiguration.setConnectorType(UserStoreConstants.JDBC_CONNECTOR_TYPE);

        log.info("setup JDBCUserStoreConnectorTest");
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        PowerMockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        PowerMockito.when(serviceReferenceHolder.getAuthConfiguration()).thenReturn(authConfiguration);
        PowerMockito.when(authConfiguration.getUserStoreConfiguration()).thenReturn(userStoreConfiguration);
        PowerMockito.when(dataSourceService.getDataSource(Constants.DATASOURCE_WSO2UM_DB))
                .thenReturn(this.umDataSource.getDatasource());

        ConnectorDataHolder.getInstance().setDataSourceService(dataSourceService);

        connector = UserStoreConnectorFactory.getUserStoreConnector();
        Assert.assertNotNull(connector);
        try {
            connector.init(userStoreConfiguration);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute(Constants.PASSWORD_URI, "admin"));
        attributeList.add(new Attribute(Constants.USERNAME_URI, "admin"));
        String userId = connector.addUser(attributeList);
        Assert.assertNotNull(userId);

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.DISPLAY_NAME_URI, "WSO2_GROUP"));
        try {
            connectorUniqueId = connector.addGroup(attributes);
            Assert.assertNotNull(connectorUniqueId);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        Logger logs = PowerMockito.mock(Logger.class);
        PowerMockito.mockStatic(LoggerFactory.class);
        PowerMockito.when(LoggerFactory.getLogger(JDBCUserStoreConnector.class)).thenReturn(logs);
        PowerMockito.when(logs.isDebugEnabled()).thenReturn(true);
    }

    @Test
    public void testInit() throws Exception {
        try {
            connector.init(userStoreConfiguration);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        userStoreConfiguration.getJdbcProperties()
                .put(org.wso2.carbon.auth.core.Constants.DATASOURCE, "nuSuchDataSource");
        try {
            connector.init(userStoreConfiguration);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertEquals("Datasource is not configured properly", e.getMessage());
        }
    }

    @Test
    public void testGetConnectorUserId() throws Exception {
        String userId;
        try {
            userId = connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin");
            Assert.assertNotNull(userId);
        } catch (UserNotFoundException e) {
            Assert.fail("Exception not expected");
        }

        try {
            userId = connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "nouser");
            Assert.fail("Exception expected");
        } catch (UserNotFoundException e) {
            Assert.assertEquals("User not found with the given attribute", e.getMessage());
        }

        //checking SQL exception path
        super.cleanup();
        try {
            userId = connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, "admin");
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testListConnectorUserIds() throws Exception {
        for (int i = 0; i < 5; i++) {
            List<Attribute> attributeList = new ArrayList<>();
            attributeList.add(new Attribute(Constants.PASSWORD_URI, "admin" + i));
            attributeList.add(new Attribute(Constants.USERNAME_URI, "user"));
            String userId = connector.addUser(attributeList);
            Assert.assertNotNull(userId);
        }

        List<String> userIds = null;

        try {
            userIds = connector.listConnectorUserIds(UserStoreConstants.CLAIM_USERNAME, "user", 0, 3);
            Assert.assertNotNull(userIds);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        Assert.assertEquals(3, userIds.size());
        userIds.forEach((String id) -> {
            Assert.assertNotNull(id);
        });

        try {
            userIds = connector.listConnectorUserIds(UserStoreConstants.CLAIM_USERNAME, "user", 1, -1);
            Assert.assertNotNull(userIds);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        Assert.assertEquals(5, userIds.size());
        userIds.forEach((String id) -> {
            Assert.assertNotNull(id);
        });

        //checking SQL exception path
        super.cleanup();
        try {
            userIds = connector.listConnectorUserIds(UserStoreConstants.CLAIM_USERNAME, "user", 0, 3);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testGetUserAttributeValues() throws Exception {
        // add test user
        List<Attribute> attributeList = new ArrayList<>();
        String adminUser = "superAdmin";
        String adminPass = "superAdminPass";
        attributeList.add(new Attribute(Constants.USERNAME_URI, adminUser));
        attributeList.add(new Attribute(Constants.PASSWORD_URI, adminPass));
        String adminUserId = connector.addUser(attributeList);
        Assert.assertNotNull(adminUserId);

        String userId = null;
        try {
            userId = connector.getConnectorUserId(UserStoreConstants.CLAIM_USERNAME, adminUser);
            Assert.assertNotNull(userId);
            Assert.assertEquals(adminUserId, userId);
        } catch (UserNotFoundException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> attributes = null;
        try {
            attributes = connector.getUserAttributeValues(userId);
            Assert.assertNotNull(attributes);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        Assert.assertEquals(2, attributes.size());

        //checking SQL exception path
        super.cleanup();
        try {
            attributes = connector.getUserAttributeValues(userId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testGetConnectorGroupId() throws Exception {
        String groupID;
        try {
            groupID = connector.getConnectorGroupId(Constants.DISPLAY_NAME_URI, "WSO2_GROUP");
            Assert.assertEquals(connectorUniqueId, groupID);
        } catch (Exception e) {  //non of the exception is expected
            Assert.fail("exception not expected");
        }

        try {
            groupID = connector.getConnectorGroupId(Constants.DISPLAY_NAME_URI, "noSuchGroup");
            Assert.fail("exception expected");
            Assert.assertEquals(connectorUniqueId, groupID);
        } catch (GroupNotFoundException e) {
            Assert.assertEquals("User not found with the given attribute", e.getMessage());
        }

        //checking SQL exception path
        super.cleanup();
        try {
            groupID = connector.getConnectorGroupId(Constants.DISPLAY_NAME_URI, "WSO2_GROUP");
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testListConnectorGroupIds() throws Exception {
        for (int i = 0; i < 5; i++) {
            List<Attribute> attributeList = new ArrayList<>();
            attributeList.add(new Attribute(Constants.DISPLAY_NAME_URI, "SAME_GROUP"));
            String connectorUniqueId = null;
            try {
                connectorUniqueId = connector.addGroup(attributeList);
                Assert.assertNotNull(connectorUniqueId);
            } catch (UserStoreConnectorException e) {
                Assert.fail("Exception not expected");
            }
        }

        List<String> groupIds = null;

        try {
            groupIds = connector.listConnectorGroupIds(Constants.DISPLAY_NAME_URI, "SAME_GROUP", 0, 3);
            Assert.assertNotNull(groupIds);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        Assert.assertEquals(3, groupIds.size());
        groupIds.forEach((String id) -> {
            Assert.assertNotNull(id);
        });

        try {
            groupIds = connector.listConnectorGroupIds(Constants.DISPLAY_NAME_URI, "SAME_GROUP", 1, -1);
            Assert.assertNotNull(groupIds);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        Assert.assertEquals(5, groupIds.size());
        groupIds.forEach((String id) -> {
            Assert.assertNotNull(id);
        });

        try {
            groupIds = connector.listConnectorGroupIds(Constants.DISPLAY_NAME_URI, "NO_SUCH_GROUP", 0, -1);
            Assert.assertNotNull(groupIds);
            Assert.assertTrue(groupIds.isEmpty());
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            groupIds = connector.listConnectorGroupIds(Constants.DISPLAY_NAME_URI, "SAME_GROUP", 0, 3);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testGetGroupAttributeValues() throws Exception {
        List<Attribute> attributes;

        try {
            attributes = connector.getGroupAttributeValues(connectorUniqueId);
            Assert.assertNotNull(attributes);
            Assert.assertEquals(1, attributes.size());
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        try {
            attributes = connector.getGroupAttributeValues("noSuchID");
            Assert.assertNotNull(attributes);
            Assert.assertTrue(attributes.isEmpty());
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            attributes = connector.getGroupAttributeValues(connectorUniqueId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testIsUserInGroup() throws Exception {
        String user = "user1";
        String pass = "pass1";
        String group = "group1";
        String userId = Utils.addUser(connector, user, pass);
        String groupId = Utils.addGroup(connector, group);
        Assert.assertNotNull(userId);
        Assert.assertNotNull(groupId);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(groupId);
        Utils.updateGroupsOfUser(connector, userId, ids);

        boolean isIn;
        try {
            isIn = connector.isUserInGroup(userId, groupId);
            Assert.assertTrue(isIn);
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        try {
            isIn = connector.isUserInGroup("noSuchUser", groupId);
            Assert.assertFalse(isIn);
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        try {
            isIn = connector.isUserInGroup(userId, "noSuchGroup");
            Assert.assertFalse(isIn);
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        try {
            isIn = connector.isUserInGroup("noSuchUser", "noSuchGroup");
            Assert.assertFalse(isIn);
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            isIn = connector.isUserInGroup(userId, groupId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testGetUserStoreConfig() throws Exception {
        UserStoreConfiguration configs = connector.getUserStoreConfig();
        Assert.assertEquals(userStoreConfiguration, configs);
    }

    @Test
    public void testGetUsers() throws Exception {
        String user = "jdbcUser";
        String pass = "pass";
        String middleName = "middlename";

        //add dummy attribute name
        String query = "INSERT INTO `AUTH_UM_ATTRIBUTES` (`ATTR_NAME`) VALUES ('" + middleName + "');";
        super.executeOnUmDb(query);

        try {
            List<Attribute> attributeList = new ArrayList<>();
            attributeList.add(new Attribute(Constants.USERNAME_URI, user));
            attributeList.add(new Attribute(Constants.PASSWORD_URI, pass));
            attributeList.add(new Attribute(middleName, middleName));
            for (int i = 0; i < 5; i++) {
                connector.addUser(attributeList);
            }
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(middleName, middleName));
        List<String> list;
        try {
            list = connector.getUsers(attributes, 0, 10);
            Assert.assertNotNull(list);
            Assert.assertEquals(5, list.size());
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            list = connector.getUsers(attributes, 0, 10);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testAddUser() throws Exception {
        String user = "user1";
        String pass = "pass1";
        String userId = Utils.addUser(connector, user, pass);
        Assert.assertNotNull(userId);

        //checking SQL exception path
        super.cleanup();
        try {
            List<Attribute> attributeList = new ArrayList<>();
            attributeList.add(new Attribute(Constants.USERNAME_URI, user));
            attributeList.add(new Attribute(Constants.PASSWORD_URI, pass));
            connector.addUser(attributeList);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testUpdateUserAttributes() throws Exception {
        String user = "jdbcUser";
        String pass = "pass";
        String familyName = "familyName";
        String newFamilyName = "newFamilyName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute(Constants.USERNAME_URI, user));
        attributeList.add(new Attribute(Constants.PASSWORD_URI, pass));
        attributeList.add(new Attribute(Constants.FAMILY_NAME_URI, familyName));
        String userId = Utils.addUser(connector, attributeList);
        Assert.assertNotNull(userId);

        List<Attribute> attributeUpdate = new ArrayList<>();
        attributeUpdate.add(new Attribute(Constants.FAMILY_NAME_URI, newFamilyName));

        try {
            connector.updateUserAttributes(userId, attributeUpdate);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> attributes = connector.getUserAttributeValues(userId);
        attributes.forEach((Attribute attribute) -> {
            if (Constants.FAMILY_NAME_URI.equalsIgnoreCase(attribute.getAttributeName())) {
                Assert.assertEquals(newFamilyName, attribute.getAttributeValue());
            }
        });

        //checking SQL exception path
        super.cleanup();
        try {
            connector.updateUserAttributes(userId, attributeUpdate);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testDeleteUser() throws Exception {
        String user = "user1";
        String pass = "pass1";
        String userId = Utils.addUser(connector, user, pass);
        Assert.assertNotNull(userId);

        try {
            connector.deleteUser(userId);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> vals = connector.getUserAttributeValues(userId);
        Assert.assertTrue(vals.isEmpty());

        //non exist user
        try {
            connector.deleteUser("noSuchId");
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            connector.deleteUser(userId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testUpdateGroupsOfUser() throws Exception {
        String user = "user1";
        String pass = "pass1";
        String displayName = "deleteGroup";
        String userId = Utils.addUser(connector, user, pass);
        Assert.assertNotNull(userId);
        String groupId = Utils.addGroup(connector, displayName);
        Assert.assertNotNull(groupId);

        Utils.updateGroupsOfUser(connector, userId, Arrays.asList(groupId));

        boolean isIn;
        try {
            isIn = connector.isUserInGroup(userId, groupId);
            Assert.assertTrue(isIn);
        } catch (UserStoreConnectorException e) {
            Assert.fail("exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            connector.updateGroupsOfUser(userId, Arrays.asList(groupId));
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testAddGroup() throws Exception {
        String displayName = "deleteGroup";
        String groupId = Utils.addGroup(connector, displayName);
        Assert.assertNotNull(groupId);

        //checking SQL exception path
        super.cleanup();
        try {
            List<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute(Constants.DISPLAY_NAME_URI, displayName));
            connector.addGroup(attributes);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testAddGroups() throws Exception {
        Map<String, List<Attribute>> attributes = new HashMap<>();
        List<Attribute> groupAttr;
        for (int i = 0; i < 5; i++) {
            groupAttr = new ArrayList<>();
            String groupName = "group_" + i;
            groupAttr.add(new Attribute(Constants.DISPLAY_NAME_URI, groupName));
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

        //checking SQL exception path
        super.cleanup();
        try {
            groupIds = connector.addGroups(attributes);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testUpdateGroupAttributes() throws Exception {
        String displayName = "updateGroup";
        String familyName = "familyName";
        String newFamilyName = "newFamilyName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute(Constants.DISPLAY_NAME_URI, displayName));
        attributeList.add(new Attribute(Constants.FAMILY_NAME_URI, familyName));
        String groupId = Utils.addGroup(connector, attributeList);
        Assert.assertNotNull(groupId);

        List<Attribute> updateAttributeList = new ArrayList<>();
        updateAttributeList.add(new Attribute(Constants.FAMILY_NAME_URI, newFamilyName));

        try {
            connector.updateGroupAttributes(groupId, updateAttributeList);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> attributes = connector.getGroupAttributeValues(groupId);
        attributes.forEach((Attribute attribute) -> {
            if (Constants.FAMILY_NAME_URI.equalsIgnoreCase(attribute.getAttributeName())) {
                Assert.assertEquals(newFamilyName, attribute.getAttributeValue());
            }
        });

        //checking SQL exception path
        super.cleanup();
        try {
            connector.updateGroupAttributes(groupId, updateAttributeList);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testDeleteGroup() throws Exception {
        String displayName = "deleteGroup";
        String groupId = Utils.addGroup(connector, displayName);
        Assert.assertNotNull(groupId);

        try {
            connector.deleteGroup(groupId);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        List<Attribute> vals = connector.getGroupAttributeValues(groupId);
        Assert.assertTrue(vals.isEmpty());

        //non exist group
        try {
            connector.deleteGroup("noSuchGroupId");
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        //checking SQL exception path
        super.cleanup();
        try {
            connector.deleteGroup(groupId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testUpdateUsersOfGroup() throws Exception {
        String user = "jdbcUser";
        String pass = "pass";
        String group = "jdbcGroup";
        String userId;
        List<String> userList = new ArrayList<>();
        List<String> newUserList = new ArrayList<>();

        String groupId = Utils.addGroup(connector, group);
        Assert.assertNotNull(groupId);

        for (int i = 0; i < 5; i++) {
            userId = Utils.addUser(connector, user, pass);
            Assert.assertNotNull(userId);
            userList.add(userId);
            Utils.updateGroupsOfUser(connector, userId, Arrays.asList(groupId));
        }

        userList.forEach((String uid) -> {
            try {
                boolean isIn = connector.isUserInGroup(uid, groupId);
                Assert.assertTrue(isIn);
            } catch (UserStoreConnectorException e) {
                Assert.fail("Exception not expected");
            }
        });

        //check with empty
        try {
            connector.updateUsersOfGroup(groupId, Collections.EMPTY_LIST);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        userList.forEach((String uid) -> {
            try {
                boolean isIn = connector.isUserInGroup(uid, groupId);
                Assert.assertFalse(isIn);
            } catch (UserStoreConnectorException e) {
                Assert.fail("Exception not expected");
            }
        });

        //add new users set
        for (int i = 0; i < 5; i++) {
            userId = Utils.addUser(connector, user, pass);
            Assert.assertNotNull(userId);
            newUserList.add(userId);
            Utils.updateGroupsOfUser(connector, userId, Arrays.asList(groupId));
        }

        try {
            connector.updateUsersOfGroup(groupId, newUserList);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        newUserList.forEach((String uid) -> {
            try {
                boolean isIn = connector.isUserInGroup(uid, groupId);
                Assert.assertTrue(isIn);
            } catch (UserStoreConnectorException e) {
                Assert.fail("Exception not expected");
            }
        });

        //checking SQL exception path
        super.cleanup();
        try {
            connector.updateUsersOfGroup(groupId, Collections.EMPTY_LIST);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testAddAndUpdateAndDeleteCredential() throws Exception {
        String user = "jdbcUser";
        String pass = "pass";
        String userId = Utils.addUser(connector, user, pass);
        Assert.assertNotNull(userId);

        String hashedPass = "YTFBMXMyUzI=";
        String updatedHashedPass = "dXBkYXRlZEExQTFzMlMy";

        PowerMockito.mockStatic(SecretKeyFactory.class);
        PowerMockito.when(SecretKeyFactory.getInstance(Mockito.anyString())).thenReturn(secretKeyFactory);
        byte[] decoded = Base64.getDecoder().decode(hashedPass);
        SecretKey secretKey = PowerMockito.mock(SecretKey.class);
        PowerMockito.when(secretKey.getEncoded()).thenReturn(decoded);
        PowerMockito.when(secretKeyFactory.generateSecret(Mockito.any(java.security.spec.KeySpec.class)))
                .thenReturn(secretKey);

        //add credential
        PasswordCallback passwordCallback = new PasswordCallback("password", false);
        try {
            connector.addCredential(userId, passwordCallback);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        Map info;
        try {
            info = connector.getUserPasswordInfo(userId);
            Assert.assertNotNull(info);
            Assert.assertEquals(hashedPass, info.get(UserStoreConstants.PASSWORD));
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        //update credential
        byte[] decodUpdatedPass = Base64.getDecoder().decode(updatedHashedPass);
        PowerMockito.when(secretKey.getEncoded()).thenReturn(decodUpdatedPass);

        try {
            connector.updateCredentials(userId, passwordCallback);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        try {
            info = connector.getUserPasswordInfo(userId);
            Assert.assertNotNull(info);
            Assert.assertEquals(updatedHashedPass, info.get(UserStoreConstants.PASSWORD));
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }

        //delete credential
        try {
            connector.deleteCredential(userId);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
        try {
            info = connector.getUserPasswordInfo(userId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertEquals("Password not found for the user.", e.getMessage());
        }

        PowerMockito.when(SecretKeyFactory.getInstance(Mockito.anyString())).thenThrow(NoSuchAlgorithmException.class);
        try {
            connector.addCredential(userId, passwordCallback);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertEquals("Error while hashing the password.", e.getMessage());
        }

        try {
            connector.updateCredentials(userId, passwordCallback);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertEquals("Error while hashing the password.", e.getMessage());
        }

        //checking SQL exception path
        super.cleanup();
        try {
            connector.addCredential(userId, passwordCallback);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }

        try {
            connector.updateCredentials(userId, passwordCallback);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }

        try {
            connector.deleteCredential(userId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }

        try {
            info = connector.getUserPasswordInfo(userId);
            Assert.fail("Exception expected");
        } catch (UserStoreConnectorException e) {
            Assert.assertTrue(e.getCause() instanceof SQLException);
        }
    }

}
