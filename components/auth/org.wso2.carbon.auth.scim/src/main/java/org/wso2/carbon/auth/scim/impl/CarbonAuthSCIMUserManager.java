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

package org.wso2.carbon.auth.scim.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.exception.TemplateExceptionCodes;
import org.wso2.carbon.auth.scim.impl.constants.SCIMCommonConstants;
import org.wso2.carbon.auth.scim.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.scim.utils.SCIMClaimResolver;
import org.wso2.carbon.auth.user.store.claim.ClaimConstants;
import org.wso2.carbon.auth.user.store.claim.ClaimMetadataStore;
import org.wso2.carbon.auth.user.store.claim.api.ClaimMapping;
import org.wso2.carbon.auth.user.store.configuration.models.AttributeConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;
import org.wso2.charon3.core.utils.codeutils.ExpressionNode;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.security.auth.callback.PasswordCallback;
import javax.ws.rs.core.Response;

/**
 * This is the wrapper class of Charon User Manager. This deals with the user management API.
 */
public class CarbonAuthSCIMUserManager implements UserManager {

    private static Logger log = LoggerFactory.getLogger(CarbonAuthSCIMUserManager.class);
    private UserStoreConnector userStoreConnector;
    private ClaimMetadataStore claimMetadataStore;

    //Holds user attribute-name to attribute-info mapping
    private Map<String, AttributeConfiguration> attributeMappings = new HashMap<>();

    public CarbonAuthSCIMUserManager(UserStoreConnector userStoreConnector, ClaimMetadataStore claimMetadataStore) {
        this.userStoreConnector = userStoreConnector;
        this.claimMetadataStore = claimMetadataStore;
        // todo load from database
        List<AttributeConfiguration> attributes =
                ServiceReferenceHolder.getInstance().getUserStoreConfigurationService().getUserStoreConfiguration()
                        .getAttributes();

        for (AttributeConfiguration attribute : attributes) {
            attributeMappings.put(attribute.getAttributeName(), attribute);
        }
    }

    @Override
    public User createUser(User user, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, BadRequestException {
        try {

            log.debug("Creating user: {}", user);

            Map<String, String> claimsMap = SCIMClaimResolver.getClaimsMap(user);
            // need to populate the supported claims/attributes. Then filter out 
            // user attributes against the supported list ?
            List<Attribute> userAttributeValueList = getAttributeListFromMap(claimsMap);
            if (isUserExist(userAttributeValueList)) {
                throw new ConflictException("User: " + user + " already exists in the system.");
            }

            String userId = userStoreConnector.addUser(userAttributeValueList);
            //handle password
            if (user.getAttribute(SCIMConstants.UserSchemaConstants.PASSWORD) != null) {
                char[] password = ((SimpleAttribute) (user.getAttribute(SCIMConstants.UserSchemaConstants.PASSWORD)))
                        .getStringValue().toCharArray();
                PasswordCallback passwordCallback =
                        new PasswordCallback(SCIMConstants.UserSchemaConstants.PASSWORD, false);
                passwordCallback.setPassword(password);
                userStoreConnector.addCredential(userId, passwordCallback);
            }

            log.debug("User: {} is created through SCIM.", user);
            // get the user again from the user store and send it to client.
            return this.getUser(userId, requiredAttributes);

        } catch (UserStoreConnectorException e) {
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error("Error occurred while adding user: " + user + " to user store", e);
            handleUserStoreExceptionWhenAdding(e);
        } catch (NotFoundException e) {
            String errMsg = "Error in retrieving newly added user: " + user + " from user store";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
        return null;
    }

    @Override
    public User getUser(String userId, Map<String, Boolean> requiredAttributes)
            throws CharonException, BadRequestException, NotFoundException {
        log.debug("Retrieving user: {} ", userId);
        try {
            List<String> requiredClaims = getRequiredClaims(requiredAttributes);

            return getSCIMUser(userId, requiredClaims, true);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error in getting user from the userId :" + userId;
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    @Override
    public List<Object> listUsersWithGET(Node rootNode, int startIndex, int count, String sortBy, String sortOrder,
            Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        log.debug("Listing Users");

        List<String> requiredClaims = getRequiredClaims(requiredAttributes);

        try {
            // check if it is a pagination and filter combination.
            if (sortOrder == null && sortBy == null && rootNode != null) {
                return listUsersWithPaginationAndFilter(requiredClaims, rootNode, startIndex, count);
            }

            //check if it is a pagination only request.
            //rootNode is null
            if (sortOrder == null && sortBy == null) {
                return listUsersWithPagination(requiredClaims, startIndex, count);
            } else {
                throw new NotImplementedException("Sorting is not supported.");
            }
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error in listing users";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    @Override
    public List<Object> listUsersWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        // this is identical to getUsersWithGet.
        return listUsersWithGET(searchRequest.getFilter(), searchRequest.getStartIndex(), searchRequest.getCount(),
                searchRequest.getSortBy(), searchRequest.getSortOder(), requiredAttributes);
    }

    @Override
    public User updateUser(User user, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, CharonException, BadRequestException, NotFoundException {
        log.debug("Updating user: {}", user);
        Map<String, String> attributesMap = SCIMClaimResolver.getClaimsMap(user);
        // need to populate the supported claims/attributes. Then filter out 
        // user attributes against the supported list ?
        List<Attribute> attributeList = getAttributeListFromMap(attributesMap);

        try {
            userStoreConnector.updateUserAttributes(user.getId(), attributeList);

            //handle password
            if (user.getAttribute(SCIMConstants.UserSchemaConstants.PASSWORD) != null) {
                char[] password = ((SimpleAttribute) (user.getAttribute(SCIMConstants.UserSchemaConstants.PASSWORD)))
                        .getStringValue().toCharArray();
                PasswordCallback passwordCallback =
                        new PasswordCallback(SCIMConstants.UserSchemaConstants.PASSWORD, false);
                passwordCallback.setPassword(password);
                userStoreConnector.updateCredentials(user.getId(), passwordCallback);
            }

            // get the updated user from the user core and sent it to client.
            return this.getUser(user.getId(), requiredAttributes);
        } catch (UserStoreConnectorException e) {
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error("Error occurred while updating user: " + user + " to user store", e);
            handleUserStoreExceptionWhenUpdating(e);
        }
        return null;
    }

    @Override
    public void deleteUser(String userId)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException {
        log.debug("Deleting user: {}", userId);
        try {
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), null, null);
            List<String> requiredUserClaims = getRequiredClaims(requiredAttributes);
            if (getSCIMUser(userId, requiredUserClaims, false) == null) {
                throw new NotFoundException("No user exists with the given id: " + userId);
            }
            userStoreConnector.deleteUser(userId);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error occurred while deleting user: " + userId;
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg);
        }

        log.debug("User with the id : " + userId + " is deleted through SCIM.");

    }

    @Override
    public Group createGroup(Group group, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, NotImplementedException, BadRequestException {
        log.debug("Creating group: {}", group);
        try {
            Map<String, String> claimMap = SCIMClaimResolver.getClaimsMap(group);

            List<Attribute> attributeValueList = getAttributeListFromMap(claimMap);

            if (isGroupExist(attributeValueList)) {
                throw new ConflictException("Group: " + group + " already exists in the system.");
            }

            List<String> requiredClaims = getRequiredClaims(requiredAttributes);
            // validate users (members) specified in the new the group
            Optional<MultiValuedAttribute> membersAttribute = Optional.ofNullable(
                    (MultiValuedAttribute) (group.getAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS)));
            List<String> userIds = new ArrayList<>();
            if (membersAttribute.isPresent()) {
                List<org.wso2.charon3.core.attributes.Attribute> subValues =
                        membersAttribute.get().getAttributeValues();
                userIds = validateAndGetUserIds(subValues, requiredClaims);
            }

            //adding the group
            String groupId = userStoreConnector.addGroup(attributeValueList);
            //need to add users groups if it is available in the request
            if (userIds.size() != 0) {
                //now add the user's groups explicitly.
                userStoreConnector.updateUsersOfGroup(groupId, userIds);
            }
            log.debug("Group: {} is created through SCIM.", group);
            // get the group again from the user store and send it to client.
            return this.getGroup(groupId, requiredAttributes);
        } catch (UserStoreConnectorException e) {
            // Charon wrap exception to SCIMResponse and does not log exceptions
            log.error("Error occurred while adding group: " + group + " to user store", e);
            handleUserStoreExceptionWhenAdding(e);
        } catch (NotFoundException e) {
            String errMsg = "Error in retrieving newly added group: " + group + " from user store";
            // Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
        return null;
    }

    @Override
    public Group getGroup(String groupId, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException {
        log.debug("Retrieving group: {} ", groupId);
        try {
            List<String> requiredClaims = getRequiredClaims(requiredAttributes);
            return getSCIMGroup(groupId, requiredClaims, true);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error in getting group from the groupId :" + groupId;
            // Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    @Override
    public List<Object> listGroupsWithGET(Node rootNode, int startIndex, int count, String sortBy, String sortOrder,
            Map<String, Boolean> requiredAttributes)
            throws CharonException, NotImplementedException, BadRequestException {
        log.debug("Listing Users");
        List<String> requiredClaims = getRequiredClaims(requiredAttributes);
        try {
            // check if it is a pagination and filter combination.
            if (sortOrder == null && sortBy == null && rootNode != null) {
                return listGroupsWithPaginationAndFilter(requiredClaims, rootNode, startIndex, count);
            }

            //check if it is a pagination only request.
            //rootNode is null
            if (sortOrder == null && sortBy == null) {
                return listGroupsWithPagination(requiredClaims, startIndex, count);
            } else {
                throw new NotImplementedException("Sorting is not supported.");
            }
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error in listing users";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    @Override
    public List<Object> listGroupsWithPost(SearchRequest searchRequest, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException {
        // this is identical to getUsersWithGet.
        return listGroupsWithGET(searchRequest.getFilter(), searchRequest.getStartIndex(), searchRequest.getCount(),
                searchRequest.getSortBy(), searchRequest.getSortOder(), requiredAttributes);
    }

    @Override
    public Group updateGroup(Group oldGroup, Group newGroup, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, BadRequestException, CharonException, NotFoundException {
        log.debug("Updating group: {}", oldGroup.getId());

        Map<String, String> attributesMap = SCIMClaimResolver.getClaimsMap(newGroup);
        // need to populate the supported claims/attributes. Then filter out 
        // user attributes against the supported list ?
        List<Attribute> attributeList = getAttributeListFromMap(attributesMap);
        List<String> requiredGroupClaims = getRequiredClaims(requiredAttributes);
        try {
            //check if group already exist
            if (getSCIMGroup(oldGroup.getId(), requiredGroupClaims, false) == null) {
                String errMsg = "Group " + oldGroup.getId() + " does not exist.";
                log.error(errMsg);
                throw new NotFoundException(errMsg);
            }

            userStoreConnector.updateGroupAttributes(oldGroup.getId(), attributeList);

            // handle users of the group
            Optional<MultiValuedAttribute> membersAttribute = Optional.ofNullable(
                    (MultiValuedAttribute) (newGroup.getAttribute(SCIMConstants.GroupSchemaConstants.MEMBERS)));

            if (membersAttribute.isPresent()) {
                List<org.wso2.charon3.core.attributes.Attribute> subValues =
                        membersAttribute.get().getAttributeValues();

                // list to store the user ids
                List<String> userIds = validateAndGetUserIds(subValues, null);

                //need to add users of the group if it is available in the request
                if (userIds.size() != 0) {
                    //now add the users of the group explicitly.
                    userStoreConnector.updateUsersOfGroup(oldGroup.getId(), userIds);
                } else {
                    //ex : { .. "members" : [] }
                    // then -> remove existing users of the group
                    userStoreConnector.removeUsersOfGroup(oldGroup.getId());
                }
            } else {
                //ex : { .. "members" : null } or "members" attribute not present at all.
                // then -> remove existing users of the group
                userStoreConnector.removeUsersOfGroup(oldGroup.getId());
            }

            // get the updated group from the user store and send it to client.
            return this.getGroup(oldGroup.getId(), requiredAttributes);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error occurred while updating group: " + oldGroup.getId();
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            handleUserStoreExceptionWhenUpdating(e);
        }
        return null;
    }

    /**
     * Validate the provided member attribute sub values and returns a list of user IDs
     *
     * @param subValues member attribute sub values defined in a group
     * @return a list of extracted user ids
     * @throws CharonException             error occurred while getting sub attributes
     * @throws BadRequestException         if any user does not exist in the system
     * @throws UserStoreConnectorException error occurred while retrieving data from user store
     */
    private List<String> validateAndGetUserIds(List<org.wso2.charon3.core.attributes.Attribute> subValues,
            List<String> requiredClaims) throws CharonException, BadRequestException, UserStoreConnectorException {
        List<String> userIds = new ArrayList<>();
        if (subValues != null && subValues.size() != 0) {
            for (org.wso2.charon3.core.attributes.Attribute subValue : subValues) {
                SimpleAttribute valueAttribute =
                        (SimpleAttribute) ((subValue)).getSubAttribute(SCIMConstants.CommonSchemaConstants.VALUE);
                String userId = (String) valueAttribute.getValue();
                if (getSCIMUser(userId, requiredClaims, false) != null) {
                    userIds.add(userId);
                } else {
                    String errorMsg = "User with Id " + userId + " does not exist in the system.";
                    log.error(errorMsg);
                    throw new BadRequestException(errorMsg, ResponseCodeConstants.INVALID_VALUE);
                }
            }
        }
        return userIds;
    }

    @Override
    public void deleteGroup(String groupId)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException {
        log.debug("Deleting group: {}", groupId);
        try {
            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), null, null);
            List<String> requiredGroupClaims = getRequiredClaims(requiredAttributes);
            if (getSCIMGroup(groupId, requiredGroupClaims, false) == null) {
                throw new NotFoundException("No group exists with the given id: " + groupId);
            }
            userStoreConnector.deleteGroup(groupId);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error occurred while deleting group: " + groupId;
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }

        log.debug("Group with the id : {} is deleted through SCIM.", groupId);
    }

    @Override
    public User createMe(User user, Map<String, Boolean> requiredAttributes)
            throws CharonException, ConflictException, BadRequestException {
        throw new CharonException("Creating a user anonymously is not supported");
    }

    @Override
    public User getMe(String userName, Map<String, Boolean> requiredAttributes)
            throws CharonException, BadRequestException, NotFoundException {
        try {
            String userId =
                    userStoreConnector.getConnectorUserId(SCIMConstants.UserSchemaConstants.USER_NAME_URI, userName);
            return getUser(userId, requiredAttributes);
        } catch (UserNotFoundException | UserStoreConnectorException e) {
            String errMsg = "Error in getting user id from username";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    @Override
    public User updateMe(User userId, Map<String, Boolean> requiredAttributes)
            throws NotImplementedException, CharonException, BadRequestException, NotFoundException {
        throw new NotImplementedException("Updating current user profile is not supported");
    }

    @Override
    public void deleteMe(String userId)
            throws NotFoundException, CharonException, NotImplementedException, BadRequestException {
        throw new NotImplementedException("Deleting current user is not supported");

    }

    /**
     * Checks whether the user already exist in the user store based on provided attributes
     *
     * @param userAttributeList attributes list of the user
     * @return true/false
     */
    private boolean isUserExist(List<Attribute> userAttributeList) {
        String uidAttributeName = claimMetadataStore.getAttributeName(UserStoreConstants.CLAIM_USERNAME);
        for (Attribute attribute : userAttributeList) {
            try {
                if (uidAttributeName.equals(attribute.getAttributeUri())) {
                    String userId = userStoreConnector
                            .getConnectorUserId(attribute.getAttributeUri(), attribute.getAttributeValue());
                    if (!StringUtils.isEmpty(userId)) {
                        return true;
                    }
                    break;
                }
            } catch (UserStoreConnectorException e) {
                log.debug("Error while checking whether user exists. {}", e.getMessage());
            } catch (UserNotFoundException e) {
                log.debug("User exists: false");
            }
        }
        return false;
    }

    /**
     * Checks whether the group already exist in the user store based on provided attributes
     *
     * @param attributeList attributes list of the group
     * @return true/false
     */
    private boolean isGroupExist(List<Attribute> attributeList) {
        String gidAttributeName = claimMetadataStore.getAttributeName(UserStoreConstants.CLAIM_DISPLAYNAME);
        for (Attribute attribute : attributeList) {
            String attributeName = attribute.getAttributeUri();
            String attributeValue = attribute.getAttributeValue();
            if (attributeMappings.containsKey(attributeName)) {
                try {
                    if (gidAttributeName.equals(attribute.getAttributeUri())) {
                        String groupId = userStoreConnector.getConnectorGroupId(attributeName, attributeValue);
                        if (!StringUtils.isEmpty(groupId)) {
                            return true;
                        }
                        break;
                    }
                } catch (UserStoreConnectorException e) {
                    log.debug("Error while checking whether user exists. {}", e.getMessage());
                } catch (GroupNotFoundException e) {
                    log.debug("Group exists: false");
                }
            }
        }
        return false;
    }

    /**
     * Returns attributes list from the map
     *
     * @param attributesMap attribute map
     * @return attribute list
     */
    private List<Attribute> getAttributeListFromMap(Map<String, String> attributesMap) {
        List<Attribute> attributeList = new ArrayList<>();
        for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            Attribute attribute = new Attribute(claimMetadataStore.getAttributeName(entry.getKey()), entry.getValue());
            attributeList.add(attribute);
        }
        return attributeList;
    }

    /**
     * Returns attributes map from the list
     *
     * @param alttributeList attribute list
     * @return attribute map
     */
    private Map<String, String> getAttributeMapFromList(List<Attribute> alttributeList) {
        Map<String, String> attributesMap = new HashMap<>();
        for (Attribute attribute : alttributeList) {
            attributesMap.put(attribute.getAttributeUri(), attribute.getAttributeValue());
        }
        return attributesMap;
    }

    /**
     * Get a SCIM user from the uuid
     *
     * @param userId        user id
     * @param includeGroups whether to include groups the user belongs to
     * @return user SCIM user object
     * @throws BadRequestException         if error occurred while constructing SCIM user object
     * @throws CharonException             if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     */
    private User getSCIMUser(String userId, List<String> requiredClaims, boolean includeGroups)
            throws CharonException, BadRequestException, UserStoreConnectorException {

        List<String> properties = new ArrayList<>();
        for (String claim : requiredClaims) {
            String att = claimMetadataStore.getAttributeName(claim);
            properties.add(att);
        }

        try {
            List<Attribute> attributeList = userStoreConnector.getUserAttributeValues(userId, properties);

            List<Attribute> claimValueAttributeList = new ArrayList<>();
            for (String aClaim : requiredClaims) {
                ClaimMapping mapping = claimMetadataStore.getClaimMapping(aClaim);
                String property = mapping.getMappedAttribute();

                for (Attribute attr : attributeList) {
                    if (attr.getAttributeUri().equalsIgnoreCase(property)) {
                        String value = attr.getAttributeValue();
                        claimValueAttributeList.add(new Attribute(aClaim, value));
                        break;
                    }
                }
            }
            if (attributeList.size() == 0) {
                //user does not exist
                return null;
            }

            // construct the SCIM Object from the attributes
            User scimUser = (User) SCIMClaimResolver
                    .constructSCIMObjectFromAttributes(getAttributeMapFromList(claimValueAttributeList),
                            SCIMCommonConstants.USER);

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), null, null);
            List<String> requiredGroupClaims = getRequiredClaims(requiredAttributes);

            if (includeGroups) {
                //set members of group
                List<String> groupIds = userStoreConnector.getGroupIdsOfUser(userId);
                if (groupIds != null) {
                    for (String groupId : groupIds) {
                        Optional<Group> group = Optional.ofNullable(getSCIMGroup(groupId, requiredGroupClaims, false));
                        if (group.isPresent()) {
                            scimUser.setGroup(null, group.get().getId(), group.get().getDisplayName());
                        } else {
                            log.warn("Group " + groupId + " recorded as a group of user " + userId + " but group "
                                    + "does not exist in the system.");
                        }
                    }
                }
            }

            // set the schemas of the scim user
            scimUser.setSchemas();
            // set location
            scimUser.setLocation(SCIMCommonConstants.USERS_LOCATION + "/" + userId);

            return scimUser;

        } catch (NotFoundException e) {
            String errMsg = "Error in getting user from the userId :" + userId;
            //Charon wrap exception to SCIMResponse and does not log exceptions so we need to log here
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    /**
     * Get a SCIM group from the uuid
     *
     * @param groupId      unique group Id
     * @param includeUsers whether to include users of the group
     * @return group SCIM group object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws CharonException             if error occurred while constructing SCIM group object
     * @throws BadRequestException         if error occurred while constructing SCIM group object
     */
    private Group getSCIMGroup(String groupId, List<String> requiredClaims, boolean includeUsers)
            throws UserStoreConnectorException, CharonException, BadRequestException {
        List<String> properties = new ArrayList<>();
        for (String claim : requiredClaims) {
            String att = claimMetadataStore.getAttributeName(claim);
            properties.add(att);
        }
        try {
            List<Attribute> attributeList = userStoreConnector.getGroupAttributeValues(groupId, properties);
            List<Attribute> claimValueAttributeList = new ArrayList<>();
            for (String aClaim : requiredClaims) {
                ClaimMapping mapping = claimMetadataStore.getClaimMapping(aClaim);
                String property = mapping.getMappedAttribute();

                for (Attribute attr : attributeList) {
                    if (attr.getAttributeUri().equals(property)) {
                        String value = attr.getAttributeValue();
                        claimValueAttributeList.add(new Attribute(aClaim, value));
                        break;
                    }
                }
            }

            if (attributeList.size() == 0) {
                //group not exists
                return null;
            }

            Group scimGroup = (Group) SCIMClaimResolver
                    .constructSCIMObjectFromAttributes(getAttributeMapFromList(claimValueAttributeList),
                            SCIMCommonConstants.GROUP);

            SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(schema), null, null);
            List<String> requiredUserClaims = getRequiredClaims(requiredAttributes);

            if (includeUsers) {
                //set members of group
                List<String> userIds = userStoreConnector.getUserIdsOfGroup(groupId);
                if (userIds != null) {
                    for (String userId : userIds) {
                        Optional<User> user = Optional.ofNullable(getSCIMUser(userId, requiredUserClaims, false));
                        if (user.isPresent()) {
                            scimGroup.setMember(user.get().getId(), user.get().getUserName());
                        } else {
                            log.warn("User " + userId + " recorded as member of group " + groupId + " but user "
                                    + "does not exist in the system.");
                        }
                    }
                }
            }

            //set the schemas of the group
            scimGroup.setSchemas();
            //set location
            scimGroup.setLocation(SCIMCommonConstants.GROUPS_LOCATION + "/" + groupId);

            return scimGroup;

        } catch (NotFoundException e) {
            String errMsg = "Error in getting group from the groupId :" + groupId;
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }

    /**
     * List the users with pagination and filter (Eq filter only)
     *
     * @param rootNode   filter model
     * @param startIndex pagination start index
     * @param count      pagination count
     * @return list of Users
     * @throws NotImplementedException     if unsupported filter model is provided.
     * @throws CharonException             if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws BadRequestException         if error occurred while constructing SCIM user object
     */
    private List<Object> listUsersWithPaginationAndFilter(List<String> requiredClaims, Node rootNode, int startIndex,
            int count)
            throws NotImplementedException, CharonException, UserStoreConnectorException, BadRequestException {
        // Filter model simply consists of a binary tree where the terminal nodes are the filter expressions and
        // non -terminal nodes are the logical operators.
        // we currently do not support complex type filter
        // eg : userName Eq vindula AND nickName sw J
        if (rootNode.getRightNode() != null) {
            throw new NotImplementedException("Complex filters are not implemented.");
        }
        if (rootNode.getLeftNode() != null) {
            throw new NotImplementedException("Complex filters are not implemented.");
        }
        // we only support 'eq' filter
        if (((ExpressionNode) (rootNode)).getOperation().equalsIgnoreCase("eq")) {
            String attributeName = ((ExpressionNode) (rootNode)).getAttributeValue();
            String attributeValue = ((ExpressionNode) (rootNode)).getValue();

            List<String> userIdsList =
                    userStoreConnector.listConnectorUserIds(attributeName, attributeValue, startIndex, count);
            List<Object> userObjectList = new ArrayList<>();
            // we need to set the first item of the array to be the number of users in the given domain.
            userObjectList.add(userIdsList.size());

            for (String userId : userIdsList) {
                User scimUser = getSCIMUser(userId, requiredClaims, true);
                userObjectList.add(scimUser);
            }
            return userObjectList;

        } else {
            throw new NotImplementedException(
                    "Filter type :" + ((ExpressionNode) (rootNode)).getOperation() + " is not supported.");
        }
    }

    /**
     * List users with pagination
     *
     * @param startIndex pagination start index
     * @param count      pagination count
     * @return list of users
     * @throws NotImplementedException     if unsupported filter model is provided.
     * @throws CharonException             if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws BadRequestException         if error occurred while constructing SCIM user object
     */
    private List<Object> listUsersWithPagination(List<String> requiredClaims, int startIndex, int count)
            throws NotImplementedException, CharonException, UserStoreConnectorException, BadRequestException {
        List<String> userIdsList = userStoreConnector.listConnectorUserIds(startIndex, count);
        List<Object> userObjectList = new ArrayList<>();
        // we need to set the first item of the array to be the number of users in the given domain.
        userObjectList.add(userIdsList.size());

        for (String userId : userIdsList) {
            User scimUser = getSCIMUser(userId, requiredClaims, true);
            userObjectList.add(scimUser);
        }
        return userObjectList;
    }

    /**
     * List the groups with pagination and filter (Eq filter only)
     *
     * @param rootNode   filter model
     * @param startIndex pagination start index
     * @param count      pagination count
     * @return list of groups
     * @throws NotImplementedException     if unsupported filter model is provided.
     * @throws CharonException             if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws BadRequestException         if error occurred while constructing SCIM user object
     */
    private List<Object> listGroupsWithPaginationAndFilter(List<String> requiredClaims, Node rootNode, int startIndex,
            int count)
            throws NotImplementedException, CharonException, UserStoreConnectorException, BadRequestException {
        // Filter model simply consists of a binary tree where the terminal nodes are the filter expressions and
        // non -terminal nodes are the logical operators.
        // we currently do not support complex type filter
        // eg : userName Eq vindula AND nickName sw J
        if (rootNode.getRightNode() != null) {
            throw new NotImplementedException("Complex filters are not implemented.");
        }
        if (rootNode.getLeftNode() != null) {
            throw new NotImplementedException("Complex filters are not implemented.");
        }
        // we only support 'eq' filter
        if (((ExpressionNode) (rootNode)).getOperation().equalsIgnoreCase("eq")) {
            String attributeName = ((ExpressionNode) (rootNode)).getAttributeValue();
            String attributeValue = ((ExpressionNode) (rootNode)).getValue();

            List<String> groupIds =
                    userStoreConnector.listConnectorGroupIds(attributeName, attributeValue, startIndex, count);
            List<Object> groupObjList = new ArrayList<>();
            // we need to set the first item of the array to be the number of users in the given domain.
            groupObjList.add(groupIds.size());

            for (String groupId : groupIds) {
                Group scimGroup = getSCIMGroup(groupId, requiredClaims, true);
                groupObjList.add(scimGroup);
            }
            return groupObjList;

        } else {
            throw new NotImplementedException(
                    "Filter type :" + ((ExpressionNode) (rootNode)).getOperation() + " is not supported.");
        }
    }

    /**
     * List groups with pagination
     *
     * @param startIndex pagination start index
     * @param count      pagination count
     * @return list of groups
     * @throws NotImplementedException     if unsupported filter model is provided.
     * @throws CharonException             if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws BadRequestException         if error occurred while constructing SCIM user object
     */
    private List<Object> listGroupsWithPagination(List<String> requiredClaims, int startIndex, int count)
            throws NotImplementedException, CharonException, UserStoreConnectorException, BadRequestException {
            List<String> groupIdsList = userStoreConnector.listConnectorGroupIds(startIndex, count);
        List<Object> groupObjList = new ArrayList<>();
        // we need to set the first item of the array to be the number of users in the given domain.
        groupObjList.add(groupIdsList.size());

        for (String groupId : groupIdsList) {
            Group scimGroup = getSCIMGroup(groupId, requiredClaims, true);
            groupObjList.add(scimGroup);
        }
        return groupObjList;
    }

    /**
     * Handle UserStoreConnectorException when adding a resource
     *
     * @param e UserStoreConnectorException
     * @throws ConflictException   UserStoreConnectorException due to resource already exist
     * @throws CharonException     UserStoreConnectorException due to a internal error
     * @throws BadRequestException UserStoreConnectorException due to issue with request params
     */
    private void handleUserStoreExceptionWhenAdding(UserStoreConnectorException e)
            throws ConflictException, CharonException, BadRequestException {
        handleUserStoreException(e);

        if (e.getErrorHandler().getHttpStatusCode() == Response.Status.CONFLICT.getStatusCode()) {
            throw new ConflictException(e.getErrorHandler().getErrorDescription());
        } else {
            throw new CharonException(SCIMCommonConstants.INTERNAL_ERROR_MESSAGE);
        }
    }

    /**
     * Handle UserStoreConnectorException when updating a resource
     *
     * @param e UserStoreConnectorException
     * @throws CharonException     UserStoreConnectorException due to a internal error
     * @throws NotFoundException   UserStoreConnectorException due to resource does not exist
     * @throws BadRequestException UserStoreConnectorException due to issue with request params
     */
    private void handleUserStoreExceptionWhenUpdating(UserStoreConnectorException e)
            throws CharonException, NotFoundException, BadRequestException {
        handleUserStoreException(e);

        if (e.getErrorHandler().getHttpStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new NotFoundException(e.getErrorHandler().getErrorDescription());
        } else {
            throw new CharonException(SCIMCommonConstants.INTERNAL_ERROR_MESSAGE);
        }
    }

    /**
     * Handle UserStoreConnectorException for common cases
     *
     * @param e UserStoreConnectorException
     * @throws CharonException     UserStoreConnectorException due to a internal error
     * @throws BadRequestException UserStoreConnectorException due to issue with request params
     */
    private void handleUserStoreException(UserStoreConnectorException e) throws CharonException, BadRequestException {
        if (e.getErrorHandler() == null) {
            throw new CharonException(SCIMCommonConstants.INTERNAL_ERROR_MESSAGE);
        }
        if (e.getErrorHandler().getHttpStatusCode() == Response.Status.BAD_REQUEST.getStatusCode()) {
            if (e.getErrorHandler() instanceof TemplateExceptionCodes.UniqueAttributeViolationUpdatingResource) {
                throw new BadRequestException(e.getErrorHandler().getErrorDescription(),
                        ResponseCodeConstants.UNIQUENESS);
            }
        }
    }

    @SuppressFBWarnings("WMI_WRONG_MAP_ITERATOR")
    private static List<String> getOnlyRequiredClaims(List<String> claimURIList,
            Map<String, Boolean> requiredAttributes) {
        List<String> requiredClaimList = new ArrayList<>();
        for (String requiredClaim : requiredAttributes.keySet()) {
            if (requiredAttributes.get(requiredClaim)) {
                if (claimURIList.contains(requiredClaim)) {
                    requiredClaimList.add(requiredClaim);
                } else {
                    String[] parts = requiredClaim.split("[.]");
                    for (String claim : claimURIList) {
                        if (parts.length == 3) {
                            if (claim.contains(parts[0] + "." + parts[1])) {
                                if (!requiredClaimList.contains(claim)) {
                                    requiredClaimList.add(claim);
                                }
                            }
                        } else if (parts.length == 2) {
                            if (claim.contains(parts[0])) {
                                if (!requiredClaimList.contains(claim)) {
                                    requiredClaimList.add(claim);
                                }
                            }
                        }

                    }
                }
            } else {
                if (!requiredClaimList.contains(requiredClaim)) {
                    requiredClaimList.add(requiredClaim);
                }
            }
        }
        return requiredClaimList;
    }

    private List<String> getRequiredClaims(Map<String, Boolean> requiredAttributes) {

        ClaimMapping[] coreClaims;
        ClaimMapping[] userClaims;
        //        ClaimMapping[] extensionClaims = null;

        coreClaims = claimMetadataStore.getAllClaimMappings(ClaimConstants.SCIM_CORE_CLAIM_DIALECT);
        userClaims = claimMetadataStore.getAllClaimMappings(ClaimConstants.SCIM_USER_CLAIM_DIALECT);
        //        if (SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema() != null) {
        //            extensionClaims = store.getAllClaimMappings(
        //                    SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema().getURI());
        //        }

        List<String> claimURIList = new ArrayList<>();
        for (ClaimMapping claim : coreClaims) {
            claimURIList.add(claim.getClaim().getClaimUri());
        }
        for (ClaimMapping claim : userClaims) {
            claimURIList.add(claim.getClaim().getClaimUri());
        }

        List<String> requiredClaims = getOnlyRequiredClaims(claimURIList, requiredAttributes);
        return requiredClaims;
    }
}
