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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.AttributeMappingConfiguration;
import org.wso2.carbon.auth.core.exception.UserNotFoundException;
import org.wso2.carbon.auth.core.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.scim.impl.constants.SCIMCommonConstants;
import org.wso2.carbon.auth.scim.utils.SCIMClaimResolver;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
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
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.utils.codeutils.ExpressionNode;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;

import static org.wso2.carbon.kernel.utils.StringUtils.isNullOrEmpty;

/**
 * This is the wrapper class of Charon User Manager. This deals with the user management API. 
 * 
 */
public class CarbonAuthUserManager implements UserManager {
    
    private static Logger log = LoggerFactory.getLogger(CarbonAuthUserManager.class);
    
    UserStoreConnector userStoreConnector;
    
    //Holds user attribute to claim mapping
    Map<String, ClaimInfo> claimMappings = new HashMap<String, ClaimInfo>();
    
    public CarbonAuthUserManager(UserStoreConnector userStoreConnector) {
        this.userStoreConnector = userStoreConnector;
        List<AttributeMappingConfiguration> attributeMappings = ServiceReferenceHolder.getInstance()
                .getAuthConfiguration().getUserStoreConfiguration().getAttributeMappings();
        
        for (AttributeMappingConfiguration attributeMapping: attributeMappings) {
            ClaimInfo claimInfo = new ClaimInfo(attributeMapping.getClaimUri(), 
                    Boolean.parseBoolean(attributeMapping.getUnique()));
            claimMappings.put(attributeMapping.getAttribute(), claimInfo);
        }
    }    

    @Override
    public User createUser(User user, Map<String, Boolean> requiredAttributes) throws CharonException, 
            ConflictException, BadRequestException {
        try {
            
            log.debug("Creating user: {}", user);

            Map<String, String> attributesMap = SCIMClaimResolver.getClaimsMap(user);
            // need to populate the supported claims/attributes. Then filter out 
            // user attributes against the supported list ?
            List<Attribute> attributeList = getAttributeListFromMap(attributesMap);
            
            if (isUserExist(attributeList)) {
                throw new ConflictException("User: " + user + " already exists in the system.");
            } 
            
            String userId = userStoreConnector.addUser(attributeList);
            
            //handle password
            if (user.getAttribute(SCIMConstants.UserSchemaConstants.PASSWORD) != null) {
                char[] password = ((SimpleAttribute) (user.getAttribute(
                        SCIMConstants.UserSchemaConstants.PASSWORD))).getStringValue().toCharArray();
                PasswordCallback passwordCallback = new PasswordCallback(SCIMConstants.UserSchemaConstants.PASSWORD,
                        false);
                passwordCallback.setPassword(password);
                List<Callback> callbackList = new ArrayList<>();
                callbackList.add(passwordCallback);
                userStoreConnector.addCredential(userId, callbackList);
            }
            
            // handle groups of the user
            MultiValuedAttribute groupsAttribute = (MultiValuedAttribute) (
                    user.getAttribute(SCIMConstants.UserSchemaConstants.GROUPS));
            // list to store the group ids which will be used to create the group attribute in scim user.
            List<String> groupIds = new ArrayList<>();
            if (groupsAttribute != null) {
                List<org.wso2.charon3.core.attributes.Attribute> subValues = groupsAttribute.getAttributeValues();

                if (subValues != null && subValues.size() != 0) {
                    for (org.wso2.charon3.core.attributes.Attribute subValue : subValues) {
                        SimpleAttribute valueAttribute =
                            (SimpleAttribute) ((subValue)).getSubAttribute(
                                     SCIMConstants.CommonSchemaConstants.VALUE);
                        groupIds.add((String) valueAttribute.getValue());
                    }
                }                    
                //need to add users groups if it is available in the request
                if (groupIds.size() != 0) {
                    //now add the user's groups explicitly.
                    userStoreConnector.updateGroupsOfUser(userId, groupIds);
                }
            }
            
            log.debug("User: {} is created through SCIM.", user);
            // get the user again from the user store and send it to client.
            return this.getUser(userId, requiredAttributes);
            
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error occurred while adding user: " + user + " to user store";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new ConflictException(errMsg);
        } catch (NotFoundException e) {
            String errMsg = "Error in retrieving newly added user: " + user + " from user store";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }
    
    @Override
    public User getUser(String userId, Map<String, Boolean> requiredAttributes) throws CharonException, 
                                          BadRequestException, NotFoundException {
        log.debug("Retrieving user: {} ", userId);
        try {
            // TODO : Check user exists
            
            return getSCIMUser(userId);
            
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error in getting user from the userId :" + userId;
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }
    
    @Override
    public List<Object> listUsersWithGET(Node rootNode, int startIndex, int count, String sortBy, String sortOrder, 
            Map<String, Boolean> requiredAttributes) throws CharonException, NotImplementedException, 
            BadRequestException {
        log.debug("Listing Users");
        try {
            // check if it is a pagination and filter combination.
            if (sortOrder == null && sortBy == null && rootNode != null) {
                return listUsersWithPaginationAndFilter(rootNode, startIndex, count);
            } 
            
            //check if it is a pagination only request.
            if (sortOrder == null && sortBy == null && rootNode == null) {
                return listUsersWithPagination(startIndex, count);
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
    public User updateUser(User user, Map<String, Boolean> requiredAttributes) throws NotImplementedException, 
                                CharonException, BadRequestException, NotFoundException {
        log.debug("Updating user: {}", user);
        // TODO : Check user exists
        Map<String, String> attributesMap = SCIMClaimResolver.getClaimsMap(user);
        // need to populate the supported claims/attributes. Then filter out 
        // user attributes against the supported list ?
        List<Attribute> attributeList = getAttributeListFromMap(attributesMap);
        
        try {
            userStoreConnector.updateUserAttributes(user.getId(), attributeList);
            
            // get the updated user from the user core and sent it to client.
            return this.getUser(user.getId(), requiredAttributes);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error occurred while updating user: " + user + " to user store";
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg);
        }
    }
    
    @Override
    public void deleteUser(String userId) throws NotFoundException, CharonException, NotImplementedException,
            BadRequestException {
        log.debug("Deleting user: {}", userId);
        //TODO Check user exists
        try {
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
    public Group createGroup(Group group, Map<String, Boolean> requiredAttributes) throws CharonException, 
                    ConflictException, NotImplementedException, BadRequestException {
        log.debug("Creating user: {}", group);
        try {
            Map<String, String> attributesMap = SCIMClaimResolver.getClaimsMap(group);

            List<Attribute> attributeList = getAttributeListFromMap(attributesMap);
            
            if (isGroupExist(attributeList)) {
                throw new ConflictException("Group: " + group + " already exists in the system.");
            }
            
            String groupId = userStoreConnector.addGroup(attributeList);
            
            log.debug("User: {} is created through SCIM.", group);
            // get the group again from the user store and send it to client.
            return this.getGroup(groupId, requiredAttributes);
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error occurred while adding group: " + group + " to user store";
            // Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new ConflictException(errMsg);
        } catch (NotFoundException e) {
            String errMsg = "Error in retrieving newly added group: " + group + " from user store";
            // Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }

    }
    
    @Override
    public Group getGroup(String groupId, Map<String, Boolean> requiredAttributes) throws NotImplementedException, 
                                        BadRequestException, CharonException, NotFoundException {
        log.debug("Retrieving group: {} ", groupId);
        try {
            // TODO : Check user exists
            
            return getSCIMGroup(groupId);
            
        } catch (UserStoreConnectorException e) {
            String errMsg = "Error in getting group from the groupId :" + groupId;
            // Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }
    }
    
    @Override
    public List<Object> listGroupsWithGET(Node arg0, int arg1, int arg2, String arg3, String arg4,
            Map<String, Boolean> arg5) throws CharonException, NotImplementedException, BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> listGroupsWithPost(SearchRequest arg0, Map<String, Boolean> arg1)
            throws NotImplementedException, BadRequestException, CharonException {
        // TODO Auto-generated method stub
        return null;
    }    

    @Override
    public Group updateGroup(Group arg0, Group arg1, Map<String, Boolean> arg2) throws NotImplementedException,
            BadRequestException, CharonException, NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void deleteGroup(String arg0) throws NotFoundException, CharonException, NotImplementedException,
            BadRequestException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public User createMe(User user, Map<String, Boolean> requiredAttributes) throws CharonException, ConflictException,
            BadRequestException {
        // Redirect to create user
        return createUser(user, requiredAttributes);
    }
    
    @Override
    public User getMe(String userId, Map<String, Boolean> requiredAttributes) throws CharonException, 
            BadRequestException, NotFoundException {
        // Redirect to get user
        return getUser(userId, requiredAttributes);
    }
    
    @Override
    public User updateMe(User userId, Map<String, Boolean> requiredAttributes) throws NotImplementedException, 
            CharonException, BadRequestException, NotFoundException {
        // Redirect to update user
        return updateUser(userId, requiredAttributes);
    }

    @Override
    public void deleteMe(String userId) throws NotFoundException, CharonException, NotImplementedException,
            BadRequestException {
        // Redirect to delete user
        deleteUser(userId);
        
    }
    
    /**
     * Checks whether the user already exist in the user store based on provided attributes
     * @param attributeList attributes list of the user
     * @return true/false 
     */
    private boolean isUserExist(List<Attribute> attributeList) {
        for (Attribute attribute : attributeList) {
            String attributeName = attribute.getAttributeName();
            String attributeValue = attribute.getAttributeValue();
            if (claimMappings.containsKey(attributeName) && claimMappings.get(attributeName).isUnique()) {
                try {
                    String userId = userStoreConnector.getConnectorUserId(attributeName, attributeValue);
                    if (!isNullOrEmpty(userId)) {
                        return true;
                    }
                } catch (UserStoreConnectorException e) {
                    log.debug("Error while checking whether user exists. {}", e.getMessage());
                } catch (UserNotFoundException e) {
                    log.debug("User exists: false");
                }
            }
        }
        return false;
    }
    
    /**
     * Checks whether the group already exist in the user store based on provided attributes
     * @param attributeList attributes list of the group
     * @return true/false 
     */
    private boolean isGroupExist(List<Attribute> attributeList) {
        for (Attribute attribute : attributeList) {
            String attributeName = attribute.getAttributeName();
            String attributeValue = attribute.getAttributeValue();
            if (claimMappings.containsKey(attributeName) && claimMappings.get(attributeName).isUnique()) {
                try {
                    String groupId = userStoreConnector.getConnectorGroupId(attributeName, attributeValue);
                    if (!isNullOrEmpty(groupId)) {
                        return true;
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
     * @param attributesMap attribute map
     * @return attribute list
     */
    private List<Attribute> getAttributeListFromMap(Map<String, String> attributesMap) {
        List<Attribute> attributeList = new ArrayList<Attribute>();
        for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            Attribute attribute = 
                    new Attribute(entry.getKey(), entry.getValue());
            attributeList.add(attribute);
        }
        return attributeList;
    }
    
    /**
     * Returns attributes map from the list
     * @param alttributeList attribute list
     * @return attribute map
     */
    private Map<String, String> getAttributeMapFromList(List<Attribute> alttributeList) {
        Map<String, String> attributesMap = new HashMap<String, String>();
        for (Attribute attribute : alttributeList) {
            attributesMap.put(attribute.getAttributeName(), attribute.getAttributeValue());
        }
        return attributesMap;
    }
    
    /**
     * @param userId user id
     * @return user SCIM user object
     * @throws BadRequestException if error occurred while constructing SCIM user object
     * @throws CharonException if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     */
    private User getSCIMUser(String userId) throws CharonException, BadRequestException, UserStoreConnectorException {
        try {
            List<Attribute> alttributeList = userStoreConnector.getUserAttributeValues(userId);
            // construct the SCIM Object from the attributes
            User scimUser = (User) SCIMClaimResolver.constructSCIMObjectFromAttributes(
                    getAttributeMapFromList(alttributeList), 1);
            
            // TODO : Get user groups

            // set the id of the user from the unique user id.
            scimUser.setId(userId);
            // set the schemas of the scim user
            scimUser.setSchemas();
            // set location
            scimUser.setLocation(SCIMCommonConstants.USERS_LOCATION + "/" + userId);

            return scimUser;
            
        } catch (NotFoundException e) {
            String errMsg = "Error in getting user from the userId :" + userId;
            //Charon wrap exception to SCIMResponse and does not log exceptions
            log.error(errMsg, e);
            throw new CharonException(errMsg, e);
        }         
    }
    
    /**
     * @param groupId unique group Id
     * @return group SCIM group object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws CharonException if error occurred while constructing SCIM group object
     * @throws BadRequestException if error occurred while constructing SCIM group object
     */
    private Group getSCIMGroup(String groupId) throws UserStoreConnectorException, CharonException, 
                                                                                        BadRequestException {
        try {
            List<Attribute> alttributeList = userStoreConnector.getGroupAttributeValues(groupId);
            Group scimGroup = (Group) SCIMClaimResolver.constructSCIMObjectFromAttributes(
                    getAttributeMapFromList(alttributeList), 2);
            
            // TODO : Get group members
            
            //set the id of the group from the unique group id.
            scimGroup.setId(groupId);
            //set the schemas of the scim user
            scimGroup.setSchemas();
            //set location
            scimGroup.setLocation(SCIMCommonConstants.USERS_LOCATION + "/" + groupId);
            
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
     * @param rootNode filter model
     * @param startIndex pagination start index
     * @param count pagination count
     * @return list of Users
     * @throws NotImplementedException if unsupported filter model is provided.
     * @throws CharonException if error occurred while constructing SCIM user object
     * @throws UserStoreConnectorException if error occurred while connecting to user store
     * @throws BadRequestException if error occurred while constructing SCIM user object
     */
    private List<Object> listUsersWithPaginationAndFilter(Node rootNode, int startIndex, int count)
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

            List<String> userIdsList = userStoreConnector.listConnectorUserIds(attributeName, attributeValue,
                    startIndex, count);
            List<Object> userObjectList = new ArrayList<>();
            // we need to set the first item of the array to be the number of users in the given domain.
            userObjectList.add(userIdsList.size());

            for (String userId : userIdsList) {
                User scimUser = getSCIMUser(userId);
                userObjectList.add(scimUser);
            }
            return userObjectList;

        } else {
            throw new NotImplementedException("Filter type :" + ((ExpressionNode) (rootNode)).getOperation()
                    + " is not supported.");
        }
    }
    
    private List<Object> listUsersWithPagination(int startIndex, int count) throws CharonException {
        //TODO: Add User store method to list userIds without passing attributes
        return null;
    }
    
    /*private List<Object> listGroupsWithPaginationAndFilter(Node rootNode, int startIndex, int count) {
        return null;
    }*/

}
