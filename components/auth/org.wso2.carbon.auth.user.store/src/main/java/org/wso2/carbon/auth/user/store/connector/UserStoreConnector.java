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

import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;

import java.util.List;
import java.util.Map;
import javax.security.auth.callback.PasswordCallback;

/**
 * User store.
 */
public interface UserStoreConnector {

    /**
     * Initialize identity store by passing user store configurations read from files.
     *
     * @param userStoreConfiguration UserStoreConfiguration for this connector.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    void init(UserStoreConfiguration userStoreConfiguration) throws UserStoreConnectorException;


    /**
     * Get connector user id from unique attribute..
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @return Connector user id.
     * @throws UserNotFoundException           User not found exception.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    String getConnectorUserId(String attributeName, String attributeValue) throws UserNotFoundException,
            UserStoreConnectorException;

    /**
     * List connector user ids from a attribute for a given range.
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @param offset         Start position.
     * @param length         Number of users to retrieve.
     * @return List of connector user ids.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    List<String> listConnectorUserIds(String attributeName, String attributeValue, int offset, int length) throws
            UserStoreConnectorException;

    /**
     * Retrieve attributes of the user with the given ID.
     *
     * @param userID ID of the user whose claims are requested
     * @return Attribute map of the user with given ID
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    List<Attribute> getUserAttributeValues(String userID) throws UserStoreConnectorException;


    /**
     * Get connector group id from unique attribute..
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @return Connector group id.
     * @throws GroupNotFoundException          Group not found exception.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    String getConnectorGroupId(String attributeName, String attributeValue) throws GroupNotFoundException,
            UserStoreConnectorException;

    /**
     * List connector group ids from a attribute for a given range.
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @param offset         Start position.
     * @param length         Number of groups to retrieve.
     * @return List of connector group ids.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    List<String> listConnectorGroupIds(String attributeName, String attributeValue, int offset, int length) throws
            UserStoreConnectorException;

    /**
     * Get all of the attributes that belongs to this group.
     *
     * @param groupId Id of the group.
     * @return Map of attributes.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    List<Attribute> getGroupAttributeValues(String groupId) throws UserStoreConnectorException;


    /**
     * Checks whether the user is in the group.
     *
     * @param userId  Id of the user.
     * @param groupId Id of the group.
     * @return true if user is in the group.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    boolean isUserInGroup(String userId, String groupId) throws UserStoreConnectorException;


    /**
     * Returns UserStoreConfiguration which consists of user store configurations.
     *
     * @return UserStoreConfiguration which consists of user store configurations
     */
    UserStoreConfiguration getUserStoreConfig();

    /**
     * Get a list of users which matches a given list of attributes.
     *
     * @param attributes Attributes of the user.
     * @return List of connector unique ids of the users.
     * @throws UserStoreConnectorException User store connector exception.
     */
    List<String> getUsers(List<Attribute> attributes, int offset, int length) throws UserStoreConnectorException;

    /**
     * Adds a new user.
     *
     * @param attributes Attributes of the user.
     * @return connector unique id of the user.
     * @throws UserStoreConnectorException User store connector exception.
     */
    String addUser(List<Attribute> attributes) throws UserStoreConnectorException;


    /**
     * Update all attributes of a user.
     *
     * @param userIdentifier User identifier.
     * @param attributes     Attribute values to update.
     * @return connector unique id of user.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    String updateUserAttributes(String userIdentifier, List<Attribute> attributes) throws
            UserStoreConnectorException;

    /**
     * Delete a user.
     *
     * @param userIdentifier User identifier.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    void deleteUser(String userIdentifier) throws UserStoreConnectorException;

    /**
     * Update group list of user.
     *
     * @param userIdentifier   User identifier.
     * @param groupIdentifiers Group identifiers.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    void updateGroupsOfUser(String userIdentifier, List<String> groupIdentifiers) throws
            UserStoreConnectorException;

    /**
     * Adds a new group.
     *
     * @param attributes Attributes of the group.
     * @return connector unique id of group.
     * @throws UserStoreConnectorException User store connector exception.
     */
    String addGroup(List<Attribute> attributes) throws UserStoreConnectorException;

    /**
     * Adds new groups.
     *
     * @param attributes Attributes of the groups.
     * @return Map with global unique id of the group with connector unique id.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    Map<String, String> addGroups(Map<String, List<Attribute>> attributes) throws UserStoreConnectorException;

    /**
     * Update all attributes of a group.
     *
     * @param groupIdentifier Group identifier.
     * @param attributes      Attribute values to update.
     * @return connector unique id of group.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    String updateGroupAttributes(String groupIdentifier, List<Attribute> attributes) throws
            UserStoreConnectorException;


    /**
     * Delete a group.
     *
     * @param groupIdentifier Group identifier.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    void deleteGroup(String groupIdentifier) throws UserStoreConnectorException;

    /**
     * Update user list of a group.
     *
     * @param groupIdentifier Group identifier.
     * @param userIdentifiers User identifier list.
     * @throws UserStoreConnectorException User Store Connector Exception.
     */
    void updateUsersOfGroup(String groupIdentifier, List<String> userIdentifiers) throws
            UserStoreConnectorException;
    
    /**
     * Add user credentials.
     *
     * @param userIdentifier unique user id of the connector
     * @param passwordCallback Callback which contains credentials.
     * @return user id
     * @throws UserStoreConnectorException Credential Store Exception.
     */
    String addCredential(String userIdentifier, PasswordCallback passwordCallback) throws UserStoreConnectorException;

    /**
     * Update all user credentials.
     *
     * @param userIdentifier unique user id of the connector
     * @param passwordCallback Callback which contains credentials.
     * @return user id
     * @throws UserStoreConnectorException Credential Store Exception.
     */
    String updateCredentials(String userIdentifier, PasswordCallback passwordCallback) throws
            UserStoreConnectorException;

    /**
     * Delete credential
     *
     * @param userIdentifier unique user id of the connector
     * @throws UserStoreConnectorException CredentialStore Exception
     */
    void deleteCredential(String userIdentifier) throws UserStoreConnectorException;

    Map getUserPasswordInfo(String userId) throws UserStoreConnectorException;

}
