/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.auth.user.store.connector;


import org.wso2.carbon.auth.user.store.config.UserStoreConnectorConfig;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.IdentityStoreConnectorException;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * User store.
 */
public interface UserStoreConnector {

    /**
     * Initialize identity store by passing identity store configurations read from files.
     *
     * @param identityStoreConnectorConfig IdentityStoreConnectorConfig for this connector.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    void init(UserStoreConnectorConfig identityStoreConnectorConfig) throws IdentityStoreConnectorException;


    /**
     * Get connector user id from unique attribute..
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @return Connector user id.
     * @throws UserNotFoundException           User not found exception.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    String getConnectorUserId(String attributeName, String attributeValue) throws UserNotFoundException,
            IdentityStoreConnectorException;

    /**
     * List connector user ids from a attribute for a given range.
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @param offset         Start position.
     * @param length         Number of users to retrieve.
     * @return List of connector user ids.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    List<String> listConnectorUserIds(String attributeName, String attributeValue, int offset, int length) throws
            IdentityStoreConnectorException;

    /**
     * Retrieve attributes of the user with the given ID.
     *
     * @param userID ID of the user whose claims are requested
     * @return Attribute map of the user with given ID
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    List<Attribute> getUserAttributeValues(String userID) throws IdentityStoreConnectorException;


    /**
     * Get connector group id from unique attribute..
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @return Connector group id.
     * @throws GroupNotFoundException          Group not found exception.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    String getConnectorGroupId(String attributeName, String attributeValue) throws GroupNotFoundException,
            IdentityStoreConnectorException;

    /**
     * List connector group ids from a attribute for a given range.
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @param offset         Start position.
     * @param length         Number of groups to retrieve.
     * @return List of connector group ids.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    List<String> listConnectorGroupIds(String attributeName, String attributeValue, int offset, int length) throws
            IdentityStoreConnectorException;

    /**
     * Get all of the attributes that belongs to this group.
     *
     * @param groupId Id of the group.
     * @return Map of attributes.
     * @throws IdentityStoreConnectorException IdentityStore Exception
     */
    List<Attribute> getGroupAttributeValues(String groupId) throws IdentityStoreConnectorException;


    /**
     * Checks whether the user is in the group.
     *
     * @param userId  Id of the user.
     * @param groupId Id of the group.
     * @return true if user is in the group.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    boolean isUserInGroup(String userId, String groupId) throws IdentityStoreConnectorException;


    /**
     * Returns IdentityStoreConnectorConfig which consists of user store configurations.
     *
     * @return IdentityStoreConnectorConfig which consists of user store configurations
     */
    UserStoreConnectorConfig getIdentityStoreConfig();

    /**
     * Get a list of users which matches a given list of attributes.
     *
     * @param attributes Attributes of the user.
     * @return List of connector unique ids of the users.
     * @throws IdentityStoreConnectorException Identity store connector exception.
     */
    List<String> getUsers(List<Attribute> attributes, int offset, int length) throws IdentityStoreConnectorException;

    /**
     * Adds a new user.
     *
     * @param attributes Attributes of the user.
     * @return connector unique id of the user.
     * @throws IdentityStoreConnectorException Identity store connector exception.
     */
    String addUser(List<Attribute> attributes) throws IdentityStoreConnectorException;


    /**
     * Update all attributes of a user.
     *
     * @param userIdentifier User identifier.
     * @param attributes     Attribute values to update.
     * @return connector unique id of user.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    String updateUserAttributes(String userIdentifier, List<Attribute> attributes) throws
            IdentityStoreConnectorException;

    /**
     * Delete a user.
     *
     * @param userIdentifier User identifier.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    void deleteUser(String userIdentifier) throws IdentityStoreConnectorException;

    /**
     * Update group list of user.
     *
     * @param userIdentifier   User identifier.
     * @param groupIdentifiers Group identifiers.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    void updateGroupsOfUser(String userIdentifier, List<String> groupIdentifiers) throws
            IdentityStoreConnectorException;

    /**
     * Adds a new group.
     *
     * @param attributes Attributes of the group.
     * @return connector unique id of group.
     * @throws IdentityStoreConnectorException Identity store connector exception.
     */
    String addGroup(List<Attribute> attributes) throws IdentityStoreConnectorException;

    /**
     * Adds new groups.
     *
     * @param attributes Attributes of the groups.
     * @return Map with global unique id of the group with connector unique id.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    Map<String, String> addGroups(Map<String, List<Attribute>> attributes) throws IdentityStoreConnectorException;

    /**
     * Update all attributes of a group.
     *
     * @param groupIdentifier Group identifier.
     * @param attributes      Attribute values to update.
     * @return connector unique id of group.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    String updateGroupAttributes(String groupIdentifier, List<Attribute> attributes) throws
            IdentityStoreConnectorException;


    /**
     * Delete a group.
     *
     * @param groupIdentifier Group identifier.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    void deleteGroup(String groupIdentifier) throws IdentityStoreConnectorException;

    /**
     * Update user list of a group.
     *
     * @param groupIdentifier Group identifier.
     * @param userIdentifiers User identifier list.
     * @throws IdentityStoreConnectorException Identity Store Connector Exception.
     */
    void updateUsersOfGroup(String groupIdentifier, List<String> userIdentifiers) throws
            IdentityStoreConnectorException;

}
