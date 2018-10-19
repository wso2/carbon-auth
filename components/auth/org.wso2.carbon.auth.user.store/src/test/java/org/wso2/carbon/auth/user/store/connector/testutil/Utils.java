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
package org.wso2.carbon.auth.user.store.connector.testutil;

import org.junit.Assert;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {
    private static final String scimid = "scimId";
    public static String addUser(UserStoreConnector connector, String username, String password) {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute(Constants.USERNAME_URI, username));
        if (password != null) {
//            attributeList.add(new Attribute(Constants.PASSWORD_URI, password));
        }
        return addUser(connector, attributeList);
    }

    public static String addUser(UserStoreConnector connector, List<Attribute> attributeList) {
        String userId;
        try {
            attributeList = populateScimId(attributeList);
            userId = connector.addUser(attributeList);
            Assert.assertNotNull(userId);
            return userId;
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected. " + e.getCause());
        }
        return null;
    }

    public static String addGroup(UserStoreConnector connector, String displayName) {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Constants.GROUP_DISPLAY_NAME_URI, displayName));
        return addGroup(connector, attributes);
    }

    public static String addGroup(UserStoreConnector connector, List<Attribute> attributeList) {
        String connectorUniqueId;
        try {
            attributeList = populateScimId(attributeList);
            connectorUniqueId = connector.addGroup(attributeList);
            Assert.assertNotNull(connectorUniqueId);
            return connectorUniqueId;
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected." + e.getCause());
        }
        return null;
    }

    private static List<Attribute> populateScimId(List<Attribute> attributeList) {
        if (attributeList != null) {
            for (Attribute attribute : attributeList) {
                if (scimid.equalsIgnoreCase(attribute.getAttributeUri())) {
                    return attributeList;
                }
            }
            attributeList.add(new Attribute(scimid, UUID.randomUUID().toString()));
            return attributeList;
        }
        return attributeList;
    }

    public static void updateGroupsOfUser(UserStoreConnector connector, String userId, List<String> groupIds) {
        try {
            connector.updateGroupsOfUser(userId, groupIds);
        } catch (UserStoreConnectorException e) {
            Assert.fail("Exception not expected");
        }
    }
}
