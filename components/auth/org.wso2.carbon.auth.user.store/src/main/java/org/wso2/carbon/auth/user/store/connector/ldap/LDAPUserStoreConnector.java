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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.Constants;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.Attribute;
import org.wso2.carbon.auth.user.store.connector.PasswordHandler;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.jdbc.DefaultPasswordHandler;
import org.wso2.carbon.auth.user.store.constant.LDAPConnectorConstants;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.GroupNotFoundException;
import org.wso2.carbon.auth.user.store.exception.LDAPConnectorException;
import org.wso2.carbon.auth.user.store.exception.UserNotFoundException;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.util.UserStoreUtil;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.callback.PasswordCallback;

/**
 * LDAP user store connection implementation
 */
public class LDAPUserStoreConnector implements UserStoreConnector {
    private static Logger log = LoggerFactory.getLogger(LDAPUserStoreConnector.class);
    private LDAPConnectionContext ldapConnectionContext;
    protected UserStoreConfiguration userStoreConfig;
    private String userSearchBase;
    private String groupSearchBase;
    private String usernameAttribute;
    private String groupAttribute;
    private String groupListFilter;
    private String userNameListFilter;
    private Map<String, Object> properties;

    public LDAPUserStoreConnector() {
    }

    @Override
    public void init(UserStoreConfiguration userStoreConfiguration) {
        this.userStoreConfig = userStoreConfiguration;
        this.ldapConnectionContext = new LDAPConnectionContext(userStoreConfiguration);
        this.properties = this.userStoreConfig.getLdapProperties();

        userSearchBase = (String) this.properties.get(Constants.LDAP_USER_SEARCH_BASE);
        groupSearchBase = (String) this.properties.get(Constants.LDAP_GROUP_SEARCH_BASE);
        usernameAttribute = (String) this.properties.get(Constants.LDAP_USERNAME_ATTRIBUTE);
        groupAttribute = (String) this.properties.get(Constants.LDAP_GROUP_ATTRIBUTE);
        groupListFilter = (String) this.properties.get(Constants.LDAP_GROUP_LIST_FILTER);
        userNameListFilter = (String) this.properties.get(Constants.LDAP_USERNAME_LIST_FILTER);
    }

    @Override
    public String getConnectorUserId(String attributeUri, String attributeValue)
            throws UserNotFoundException, UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute(attributeUri, attributeValue));

        try {
            NamingEnumeration<SearchResult> enumeration = context.search(userSearchBase, matchAttrs);
            if (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                return (String) next.getAttributes().get(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME).get();
            } else {
                throw new UserNotFoundException("User not found with the given attribute");
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public List<String> listConnectorUserIds(String attributeUri, String attributeValue, int offset, int length)
            throws UserStoreConnectorException {
        DirContext context;
        List<String> userList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute(attributeUri, attributeValue));

        try {
            NamingEnumeration<SearchResult> enumeration = context.search(userSearchBase, matchAttrs);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                userList.add((String) next.getAttributes().get(usernameAttribute).get());
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return userList;
    }

    @Override
    public List<String> listConnectorUserIds(int offset, int length) throws UserStoreConnectorException {
        if (length == 0) {
            return Collections.emptyList();
        }
        int searchTime = UserStoreConstants.MAX_SEARCH_TIME;

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setCountLimit(length);
        searchCtls.setTimeLimit(searchTime);

        StringBuilder searchFilter = new StringBuilder(userNameListFilter);
        String userNameProperty = usernameAttribute;

        StringBuilder finalFilter = new StringBuilder();

        String filter = "*";
        String[] returnedAtts = new String[] { userNameProperty, LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME };
        finalFilter.append("(&").append(searchFilter).append("(").append(userNameProperty).append("=").append(filter)
                .append("))");
        searchCtls.setReturningAttributes(returnedAtts);

        DirContext context;
        List<String> userList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        try {
            NamingEnumeration<SearchResult> enumeration =
                    context.search(userSearchBase, finalFilter.toString(), searchCtls);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                userList.add((String) next.getAttributes().get(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME).get());
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return userList;
    }

    @Override
    public List<Attribute> getUserAttributeValues(String userID) throws UserStoreConnectorException {
        DirContext context;
        List<Attribute> attributeList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        try {
            NameParser ldapParser = context.getNameParser("");
            String userName = getUserName(userID);
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + userName + "," + userSearchBase);
            Attributes attributes = context.getAttributes(compoundName);
            NamingEnumeration<String> ids = attributes.getIDs();
            while (ids.hasMoreElements()) {
                String id = ids.next();
                javax.naming.directory.Attribute attribute = attributes.get(id);
                attributeList.add(new Attribute(id, (String) attribute.get()));
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return attributeList;
    }

    @Override
    public List<Attribute> getUserAttributeValues(String userID, List<String> requiredAttribute)
            throws UserStoreConnectorException {
        DirContext context;
        List<Attribute> attributeList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        StringBuilder searchFilterBuilder = new StringBuilder();
        searchFilterBuilder.append("(&").append(userNameListFilter).append("(")
                .append(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME).append("=?))");
        String searchFilter = searchFilterBuilder.toString().replace("?", userID);

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] propertyNames = {};
        if (requiredAttribute != null && requiredAttribute.size() > 0) {
            propertyNames = requiredAttribute.toArray(new String[requiredAttribute.size()]);
            searchCtls.setReturningAttributes(propertyNames);
        }

        String userAttributeSeparator = ",";
        String attrSeparator = ",";
        try {
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(userSearchBase);
            NamingEnumeration<?> answer = context.search(compoundName, searchFilter, searchCtls);
            NamingEnumeration<?> attrs;
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attributes = sr.getAttributes();
                if (attributes != null) {
                    for (String name : propertyNames) {
                        if (name != null) {
                            javax.naming.directory.Attribute attribute = attributes.get(name);
                            if (attribute != null) {
                                StringBuilder attrBuffer = new StringBuilder();
                                for (attrs = attribute.getAll(); attrs.hasMore(); ) {
                                    Object attObject = attrs.next();
                                    String attr = null;
                                    if (attObject instanceof String) {
                                        attr = (String) attObject;
                                    }

                                    // else if (attObject instanceof byte[]) {
                                    // return canonical representation of UUIDs or base64 encoded
                                    // string of other binary data
                                    // Active Directory attribute: objectGUID
                                    // RFC 4530 attribute: entryUUID
                                    //todo: handle
                                    // }

                                    if (attr != null && attr.trim().length() > 0) {
                                        attrBuffer.append(attr).append(attrSeparator);
                                    }
                                    String value = attrBuffer.toString();

                                    /*
                                     * Length needs to be more than userAttributeSeparator.length() for a valid
                                     * attribute, since we
                                     * attach userAttributeSeparator
                                     */
                                    if (!StringUtils.isBlank(value) && value.trim().length() > userAttributeSeparator
                                            .length()) {
                                        value = value.substring(0, value.length() - userAttributeSeparator.length());
                                        attributeList.add(new Attribute(name, value));
                                    }

                                }
                            }
                        }
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return attributeList;
    }

    @Override
    public String getConnectorGroupId(String attributeUri, String attributeValue)
            throws GroupNotFoundException, UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute(attributeUri, attributeValue));

        try {
            NamingEnumeration<SearchResult> enumeration = context.search(groupSearchBase, matchAttrs);
            if (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                return (String) next.getAttributes().get(LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME).get();
            } else {
                throw new GroupNotFoundException("User not found with the given attribute");
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public List<String> listConnectorGroupIds(String attributeUri, String attributeValue, int offset, int length)
            throws UserStoreConnectorException {
        DirContext context;
        List<String> groupList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute(attributeUri, attributeValue));
        try {
            NamingEnumeration<SearchResult> enumeration = context.search(groupSearchBase, matchAttrs);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                groupList
                        .add((String) next.getAttributes().get(LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME).get());
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return groupList;
    }

    @Override
    public List<String> listConnectorGroupIds(int offset, int length) throws UserStoreConnectorException {
        if (length == 0) {
            return Collections.emptyList();
        }
        int searchTime = UserStoreConstants.MAX_SEARCH_TIME;

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setCountLimit(length);
        searchCtls.setTimeLimit(searchTime);

        StringBuilder searchFilter = new StringBuilder(groupListFilter);
        StringBuilder finalFilter = new StringBuilder();

        String filter = "*";
        String[] returnedAtts = new String[] { LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME };
        finalFilter.append("(&").append(searchFilter).append("(").append(groupAttribute).append("=").append(filter)
                .append("))");
        searchCtls.setReturningAttributes(returnedAtts);

        DirContext context;
        List<String> userList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        try {
            NamingEnumeration<SearchResult> enumeration =
                    context.search(groupSearchBase, finalFilter.toString(), searchCtls);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                userList.add((String) next.getAttributes().get(LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME).get());
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return userList;
    }

    @Override
    public List<Attribute> getGroupAttributeValues(String groupId) throws UserStoreConnectorException {
        DirContext context;
        List<Attribute> attributeList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        try {
            NameParser ldapParser = context.getNameParser("");
            String groupName = getGroupName(groupId);
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupName + "," + groupSearchBase);
            Attributes attributes = context.getAttributes(compoundName);
            NamingEnumeration<String> ids = attributes.getIDs();
            while (ids.hasMoreElements()) {
                String id = ids.next();
                javax.naming.directory.Attribute attribute = attributes.get(id);
                attributeList.add(new Attribute(id, (String) attribute.get()));
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return attributeList;
    }

    @Override
    public List<Attribute> getGroupAttributeValues(String groupId, List<String> requiredAttribute)
            throws UserStoreConnectorException {
        DirContext context;
        List<Attribute> attributeList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        StringBuilder searchFilterBuilder = new StringBuilder();
        searchFilterBuilder.append("(&").append(groupListFilter).append("(")
                .append(LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME).append("=?))");
        String searchFilter = searchFilterBuilder.toString().replace("?", groupId);
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] propertyNames = {};
        if (requiredAttribute != null && requiredAttribute.size() > 0) {
            propertyNames = requiredAttribute.toArray(new String[requiredAttribute.size()]);
            searchCtls.setReturningAttributes(propertyNames);
        }
        String userAttributeSeparator = ",";
        String attrSeparator = ",";
        try {
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(groupSearchBase);
            NamingEnumeration<?> answer = context.search(compoundName, searchFilter, searchCtls);
            NamingEnumeration<?> attrs;
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attributes = sr.getAttributes();
                if (attributes != null) {
                    for (String name : propertyNames) {
                        if (name != null) {
                            javax.naming.directory.Attribute attribute = attributes.get(name);
                            if (attribute != null) {
                                StringBuilder attrBuffer = new StringBuilder();
                                for (attrs = attribute.getAll(); attrs.hasMore(); ) {
                                    Object attObject = attrs.next();
                                    String attr = null;
                                    if (attObject instanceof String) {
                                        attr = (String) attObject;
                                    }
                                    // else if (attObject instanceof byte[]) {
                                    // return canonical representation of UUIDs or base64 encoded string of
                                    // other binary data
                                    // Active Directory attribute: objectGUID
                                    // RFC 4530 attribute: entryUUID
                                    //todo: handle
                                    // }

                                    if (!StringUtils.isBlank(attr) && attr.trim().length() > 0) {
                                        attrBuffer.append(attr).append(attrSeparator);
                                    }
                                    String value = attrBuffer.toString();

                                    /*
                                     * Length needs to be more than userAttributeSeparator.length() for a valid
                                     * attribute, since we
                                     * attach userAttributeSeparator
                                     */
                                    if (value.trim().length() > userAttributeSeparator.length()) {
                                        value = value.substring(0, value.length() - userAttributeSeparator.length());
                                        attributeList.add(new Attribute(name, value));
                                    }

                                }
                            }
                        }
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return attributeList;
    }

    @Override
    public boolean isUserInGroup(String userId, String groupId) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        try {
            NameParser ldapParser = context.getNameParser("");
            String userName = getUserName(userId);
            String groupName = getGroupName(groupId);
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupName + "," + groupSearchBase);
            Name userCompoundName = ldapParser.parse(usernameAttribute + "=" + userName + "," + userSearchBase);
            Attributes attributes = context.getAttributes(compoundName);
            NamingEnumeration<String> ids = attributes.getIDs();
            while (ids.hasMoreElements()) {
                String id = ids.next();
                if (UserStoreConstants.LDAP_MEMBER_ATTRIBUTE.equals(id)) {
                    javax.naming.directory.Attribute atts = attributes.get(id);
                    for (int i = 0; i < atts.size(); i++) {
                        if (atts.get(i).equals(userCompoundName.toString())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return false;
    }

    @Override
    public UserStoreConfiguration getUserStoreConfig() {
        return userStoreConfig;
    }

    @Override
    public List<String> getUsers(List<Attribute> attributes, int offset, int length)
            throws UserStoreConnectorException {
        //todo: not yet implemented
        throw new UserStoreConnectorException(UserStoreConstants.OPERATION_NOT_SUPPORTED_IN_LDAP);
    }

    @Override
    public String addUser(List<Attribute> attributes) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        String username = null;
        String scimid = null;
        for (Attribute attribute : attributes) {
            if (usernameAttribute.equalsIgnoreCase(attribute.getAttributeUri())) {
                username = attribute.getAttributeValue();
            } else if (LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME.equalsIgnoreCase(attribute.getAttributeUri())) {
                scimid = attribute.getAttributeValue();
            }
        }
        BasicAttributes basicAttributes = getUserBasicAttributes(username);
        setClaims(attributes, basicAttributes, username);
        try {
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + username + "," + userSearchBase);
            context.createSubcontext(compoundName, basicAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error adding user to LDAP", e);
        }
        return scimid;
    }

    @Override
    public String updateUserAttributes(String userIdentifier, List<Attribute> attributes)
            throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        ModificationItem[] basicAttributes = new ModificationItem[attributes.size()];
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            basicAttributes[i] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute(attribute.getAttributeUri(), attribute.getAttributeValue()));
        }
        try {
            NameParser ldapParser = context.getNameParser("");
            String username = getUserName(userIdentifier);
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + username + "," + userSearchBase);
            context.modifyAttributes(compoundName, basicAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error users of group", e);
        }
        return userIdentifier;
    }

    @Override
    public void deleteUser(String userIdentifier) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        try {
            String userName = getUserName(userIdentifier);
            context.destroySubcontext(usernameAttribute + "=" + userName + "," + userSearchBase);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while deleting user from LDAP", e);
        }
    }

    @Override
    public void updateGroupsOfUser(String userIdentifier, List<String> groupIdentifiers)
            throws UserStoreConnectorException {
        for (String groupId : groupIdentifiers) {
            this.updateUsersOfGroup(groupId, Arrays.asList(userIdentifier));
        }
    }

    @Override
    public void removeGroupsOfUser(String userIdentifier) throws UserStoreConnectorException {
        //TODO: implement removeGroupsOfUser in LDAPUserStoreConnector
        throw new UserStoreConnectorException(UserStoreConstants.OPERATION_NOT_SUPPORTED_IN_LDAP);
    }

    @Override
    public List<String> getUserIdsOfGroup(String groupIdentifier) throws UserStoreConnectorException {
        int givenMax = UserStoreConstants.MAX_USER_ROLE_LIST;
        int searchTime = UserStoreConstants.MAX_SEARCH_TIME;

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setCountLimit(givenMax);
        searchCtls.setTimeLimit(searchTime);

        StringBuilder searchFilter = new StringBuilder(groupListFilter);
        StringBuilder finalFilter = new StringBuilder();
        String[] returnedAtts = new String[] { LDAPConnectorConstants.MEMBERSHIP_ATTRIBUTE_NAME };
        finalFilter.append("(&").append(searchFilter).append("(")
                .append(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME).append("=").append(groupIdentifier)
                .append("))");
        searchCtls.setReturningAttributes(returnedAtts);

        DirContext context;
        List<String> userIdList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        List<String> userDNList = new ArrayList<>();
        try {
            NamingEnumeration<SearchResult> enumeration =
                    context.search(groupSearchBase, finalFilter.toString(), searchCtls);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                Attributes attributes = next.getAttributes();
                if (attributes != null) {
                    NamingEnumeration attributeEntry;
                    for (attributeEntry = attributes.getAll(); attributeEntry.hasMore(); ) {
                        javax.naming.directory.Attribute valAttribute =
                                (javax.naming.directory.Attribute) attributeEntry.next();
                        if (LDAPConnectorConstants.MEMBERSHIP_ATTRIBUTE_NAME.equals(valAttribute.getID())) {
                            NamingEnumeration values;
                            for (values = valAttribute.getAll(); values.hasMore(); ) {
                                String value = values.next().toString();
                                if (!StringUtils.isBlank(value)) {
                                    userDNList.add(value);
                                }
                            }
                        }
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }

        String[] returnedAttributes = { usernameAttribute, LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME };
        for (String user : userDNList) {
            Attributes userAttributes;
            try {
                // '\' and '"' characters need another level of escaping before searching
                userAttributes = context.getAttributes(new CompositeName().add(user), returnedAttributes);

                String userId;
                if (userAttributes != null) {
                    javax.naming.directory.Attribute userIdAttribute =
                            userAttributes.get(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME);
                    if (userIdAttribute != null) {
                        userId = (String) userIdAttribute.get();
                        userIdList.add(userId);
                    }
                }

            } catch (NamingException e) {
                String msg = "Error in reading user information in the user store for the user " + user;
                if (log.isDebugEnabled()) {
                    log.debug(msg, e);
                }
                throw new UserStoreConnectorException(msg, e);
            }

        }
        return userIdList;
    }

    @Override
    public List<String> getGroupIdsOfUser(String userIdentifier) throws UserStoreConnectorException {
        int givenMax = UserStoreConstants.MAX_USER_ROLE_LIST;
        int searchTime = UserStoreConstants.MAX_SEARCH_TIME;

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setCountLimit(givenMax);
        searchCtls.setTimeLimit(searchTime);
        StringBuilder searchFilter = new StringBuilder(groupListFilter);
        StringBuilder finalFilter = new StringBuilder();
        String[] returnedAtts = new String[] { LDAPConnectorConstants.MEMBERSHIP_ATTRIBUTE_NAME,
                LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME };
        String userDN = getUserDN(userIdentifier);
        finalFilter.append("(&").append(searchFilter).append("(")
                .append(LDAPConnectorConstants.MEMBERSHIP_ATTRIBUTE_NAME).append("=").append(userDN).append("))");
        searchCtls.setReturningAttributes(returnedAtts);

        DirContext context;
        List<String> groupIdList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        try {
            NamingEnumeration<SearchResult> enumeration =
                    context.search(groupSearchBase, finalFilter.toString(), searchCtls);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                Attributes attributes = next.getAttributes();
                if (attributes != null) {
                    javax.naming.directory.Attribute scimidAttr =
                            attributes.get(LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME);
                    if (scimidAttr != null) {
                        groupIdList.add((String) scimidAttr.get());
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return groupIdList;
    }

    @Override
    public List<String> getGroupsOfUser(String userIdentifier) throws UserStoreConnectorException {
        String membershipProperty = LDAPConnectorConstants.MEMBERSHIP_ATTRIBUTE_NAME;
        DirContext context;
        List<String> groupList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        try {
            NameParser ldapParser = context.getNameParser("");
            Name membershipValue = ldapParser.parse(usernameAttribute + "=" + userIdentifier + "," + userSearchBase);
            String searchFilter = "(&" + groupListFilter + "(" + membershipProperty + "=" + membershipValue + "))";
            String returnedAtts[] = { groupAttribute };
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchCtls.setReturningAttributes(returnedAtts);

            NamingEnumeration<SearchResult> enumeration = context.search(groupSearchBase, searchFilter, searchCtls);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                javax.naming.directory.Attribute attr = next.getAttributes().get(groupAttribute);
                if (attr != null) {
                    for (Enumeration vals = attr.getAll(); vals.hasMoreElements(); ) {
                        String name = (String) vals.nextElement();
                        groupList.add(name);
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        return groupList;
    }

    @Override
    public String addGroup(List<Attribute> attributes) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        String groupName = null;
        String scimid = null;
        for (Attribute attribute : attributes) {
            if (LDAPConnectorConstants.DISPLAY_NAME_ATTRIBUTE_NAME.equalsIgnoreCase(attribute.getAttributeUri())) {
                groupName = attribute.getAttributeValue();
            } else if (LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME.equalsIgnoreCase(attribute.getAttributeUri())) {
                scimid = attribute.getAttributeValue();
            }
        }
        BasicAttributes basicAttributes = getGroupBasicAttributes(groupName);
        setClaims(attributes, basicAttributes, groupName);
        try {
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupName + "," + groupSearchBase);
            context.createSubcontext(compoundName, basicAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error adding user to LDAP", e);
        }
        return scimid;
    }

    @Override
    public Map<String, String> addGroups(Map<String, List<Attribute>> attributes) throws UserStoreConnectorException {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry aGroup : attributes.entrySet()) {
            String gId = this.addGroup((List<Attribute>) aGroup.getValue());
            map.put((String) aGroup.getKey(), gId);
        }
        return map;
    }

    @Override
    public String updateGroupAttributes(String groupIdentifier, List<Attribute> attributes)
            throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        ModificationItem[] basicAttributes = new ModificationItem[attributes.size()];
        for (Attribute attribute : attributes) {
            basicAttributes[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute(attribute.getAttributeUri(), attribute.getAttributeValue()));
        }
        try {
            NameParser ldapParser = context.getNameParser("");
            String groupName = getGroupName(groupIdentifier);
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupName + "," + groupSearchBase);
            context.modifyAttributes(compoundName, basicAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error users of group", e);
        }
        return groupIdentifier;
    }

    @Override
    public void deleteGroup(String groupIdentifier) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        try {
            String groupName = getGroupName(groupIdentifier);
            context.destroySubcontext(groupAttribute + "=" + groupName + "," + groupSearchBase);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public void updateUsersOfGroup(String groupIdentifier, List<String> userIdentifiers)
            throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        try {
            NameParser ldapParser = context.getNameParser("");
            String groupName = getGroupName(groupIdentifier);
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupName + "," + groupSearchBase);

            Attributes newAttributes = new BasicAttributes(true);
            javax.naming.directory.Attribute attribute;
            for (String uid : userIdentifiers) {
                String userName = getUserName(uid);
                Name userCompoundName = ldapParser.parse(usernameAttribute + "=" + userName + "," + userSearchBase);
                attribute = new BasicAttribute(UserStoreConstants.LDAP_MEMBER_ATTRIBUTE);
                attribute.add(userCompoundName.toString());
                newAttributes.put(attribute);
            }
            context.modifyAttributes(compoundName, DirContext.REPLACE_ATTRIBUTE, newAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error users of group", e);
        }
    }

    @Override
    public void removeUsersOfGroup(String groupIdentifier) throws UserStoreConnectorException {
        //TODO: implement removeUsersOfGroup in LDAPUserStoreConnector
        throw new UserStoreConnectorException(UserStoreConstants.OPERATION_NOT_SUPPORTED_IN_LDAP);
    }

    @Override
    public String addCredential(String userIdentifier, PasswordCallback passwordCallback)
            throws UserStoreConnectorException {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        BasicAttribute userPassword = new BasicAttribute(LDAPConnectorConstants.USER_PASSWORD_ATTRIBUTE_NAME);
        BasicAttribute userPasswordSalt = new BasicAttribute(LDAPConnectorConstants.USER_PASSWORD_SALT_ATTRIBUTE_NAME);
        String hashAlgo = getHashAlgo();
        int iterationCount = getIterationCount();
        int keyLength = getKeyLength();

        String salt = UserStoreUtil.generateUUID();
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
        userPassword.add(hashedPassword);
        userPasswordSalt.add(salt);
        basicAttributes.put(userPassword);
        basicAttributes.put(userPasswordSalt);

        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        try {
            String userName = getUserName(userIdentifier);
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + userName + "," + userSearchBase);
            context.modifyAttributes(compoundName, DirContext.ADD_ATTRIBUTE, basicAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error adding user credentials to LDAP", e);
        }
        return userIdentifier;
    }

    @Override
    public String updateCredentials(String userIdentifier, PasswordCallback passwordCallback)
            throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        char[] password = passwordCallback.getPassword();
        String salt = UserStoreUtil.generateUUID();
        String hashAlgo = getHashAlgo();
        int iterationCount = getIterationCount();
        int keyLength = getKeyLength();
        PasswordHandler passwordHandler = new DefaultPasswordHandler();
        passwordHandler.setIterationCount(iterationCount);
        passwordHandler.setKeyLength(keyLength);
        String hashedPassword;
        try {
            hashedPassword = passwordHandler.hashPassword(password, salt, hashAlgo);
        } catch (NoSuchAlgorithmException e) {
            throw new UserStoreConnectorException("Error while hashing the password.", e);
        }

        ModificationItem[] basicAttributes = new ModificationItem[2];
        basicAttributes[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                new BasicAttribute(LDAPConnectorConstants.USER_PASSWORD_ATTRIBUTE_NAME, hashedPassword));
        basicAttributes[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                new BasicAttribute(LDAPConnectorConstants.USER_PASSWORD_SALT_ATTRIBUTE_NAME, salt));
        try {
            NameParser ldapParser = context.getNameParser("");
            String userName = getUserName(userIdentifier);
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + userName + "," + userSearchBase);
            context.modifyAttributes(compoundName, basicAttributes);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error users of group", e);
        }
        return userIdentifier;
    }

    @Override
    public void deleteCredential(String userIdentifier) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        try {
            String userName = getUserName(userIdentifier);
            context.destroySubcontext(usernameAttribute + "=" + userName + "," + userSearchBase);
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public Map getUserPasswordInfo(String userId) throws UserStoreConnectorException {
        int givenMax = UserStoreConstants.MAX_USER_ROLE_LIST;
        int searchTime = UserStoreConstants.MAX_SEARCH_TIME;

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setCountLimit(givenMax);
        searchCtls.setTimeLimit(searchTime);
        StringBuilder searchFilter = new StringBuilder(userNameListFilter);
        StringBuilder finalFilter = new StringBuilder();
        String[] returnedAtts = new String[] { LDAPConnectorConstants.USER_PASSWORD_SALT_ATTRIBUTE_NAME,
                LDAPConnectorConstants.USER_PASSWORD_ATTRIBUTE_NAME };
        finalFilter.append("(&").append(searchFilter).append("(")
                .append(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME).append("=").append(userId).append("))");
        searchCtls.setReturningAttributes(returnedAtts);

        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }
        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute(usernameAttribute, userId));
        try {
            NamingEnumeration<SearchResult> enumeration =
                    context.search(userSearchBase, finalFilter.toString(), searchCtls);
            if (enumeration.hasMoreElements()) {
                Map<String, Object> info = new HashMap<>();
                SearchResult next = enumeration.next();
                String userPassword = new String(
                        (byte[]) next.getAttributes().get(LDAPConnectorConstants.USER_PASSWORD_ATTRIBUTE_NAME).get(),
                        Charset.defaultCharset());
                String userPasswordSalt =
                        (String) next.getAttributes().get(LDAPConnectorConstants.USER_PASSWORD_SALT_ATTRIBUTE_NAME)
                                .get();
                info.put(UserStoreConstants.PASSWORD, userPassword);
                info.put(UserStoreConstants.PASSWORD_SALT, userPasswordSalt);
                info.put(UserStoreConstants.HASH_ALGO, getHashAlgo());
                info.put(UserStoreConstants.ITERATION_COUNT, getIterationCount());
                info.put(UserStoreConstants.KEY_LENGTH, getKeyLength());
                return info;
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return null;
    }

    private BasicAttributes getUserBasicAttributes(String username) {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        String userEntryObjectClassProperty = (String) this.properties.get(Constants.LDAP_USER_ENTRY_OBJECT_CLASS);
        BasicAttribute objectClass = new BasicAttribute(Constants.OBJECT_CLASS_NAME);
        String[] objectClassHierarchy = userEntryObjectClassProperty.split("/");
        for (String userObjectClass : objectClassHierarchy) {
            if (userObjectClass != null && !userObjectClass.trim().equals("")) {
                objectClass.add(userObjectClass.trim());
            }
        }
        objectClass.add(UserStoreConstants.LDAP_EXTENSIBLEOBJECT_ATTRIBUTE);
        basicAttributes.put(objectClass);
        BasicAttribute userNameAttribute = new BasicAttribute(usernameAttribute);
        userNameAttribute.add(username);
        basicAttributes.put(userNameAttribute);
        return basicAttributes;
    }

    private BasicAttributes getGroupBasicAttributes(String groupName) {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        String groupEntryObjectClassProperty = (String) this.properties.get(Constants.LDAP_GROUP_ENTRY_OBJECT_CLASS);
        BasicAttribute objectClass = new BasicAttribute(Constants.OBJECT_CLASS_NAME);
        String[] objectClassHierarchy = groupEntryObjectClassProperty.split("/");
        for (String userObjectClass : objectClassHierarchy) {
            if (userObjectClass != null && !userObjectClass.trim().equals("")) {
                objectClass.add(userObjectClass.trim());
            }
        }
        objectClass.add(UserStoreConstants.LDAP_EXTENSIBLEOBJECT_ATTRIBUTE);
        basicAttributes.put(objectClass);
        BasicAttribute userNameAttribute = new BasicAttribute(groupAttribute);
        userNameAttribute.add(groupName);
        basicAttributes.put(userNameAttribute);
        BasicAttribute member = new BasicAttribute(UserStoreConstants.LDAP_MEMBER_ATTRIBUTE);
        member.add("");
        basicAttributes.put(member);
        return basicAttributes;
    }

    private void setClaims(List<Attribute> attributes, BasicAttributes basicAttributes, String uniqueName) {
        log.debug("Processing user claims");
        boolean isSNExists = false;
        boolean isCNExists = false;
        for (Attribute attribute : attributes) {
            if (Constants.ATTR_NAME_CN.equals(attribute.getAttributeUri())) {
                isCNExists = true;
            } else if (Constants.ATTR_NAME_SN.equals(attribute.getAttributeUri())) {
                isSNExists = true;
            } else if (LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME.equals(attribute.getAttributeUri())) {
                BasicAttribute sn = new BasicAttribute(Constants.ATTR_NAME_SCIMID);
                sn.add(attribute.getAttributeValue());
                basicAttributes.put(sn);
            }
            if ("ref".equals(attribute.getAttributeUri())) {
                continue;
            }
            BasicAttribute uniqueNameAttribute = new BasicAttribute(attribute.getAttributeUri());
            uniqueNameAttribute.add(attribute.getAttributeValue());
            basicAttributes.put(uniqueNameAttribute);
        }
        // If required attributes cn, sn are not set during claim mapping,
        // set them as user names
        if (!isCNExists) {
            BasicAttribute cn = new BasicAttribute(Constants.ATTR_NAME_CN);
            cn.add(uniqueName);
            basicAttributes.put(cn);
        }
        if (!isSNExists) {
            BasicAttribute sn = new BasicAttribute(Constants.ATTR_NAME_SN);
            sn.add(uniqueName);
            basicAttributes.put(sn);
        }
    }

    private String getHashAlgo() {
        return userStoreConfig.getHashAlgo();
    }

    private int getIterationCount() {
        return userStoreConfig.getIterationCount();
    }

    private int getKeyLength() {
        return userStoreConfig.getKeyLength();
    }

    private String getUserName(String userID) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        StringBuilder userSearchFilterBuilder = new StringBuilder();
        userSearchFilterBuilder.append("(&").append(userNameListFilter).append("(")
                .append(LDAPConnectorConstants.USER_UUID_ATTRIBUTE_NAME).append("=?))");
        String searchFilter = userSearchFilterBuilder.toString().replace("?", userID);
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] propertyNames = { usernameAttribute };
        searchCtls.setReturningAttributes(propertyNames);

        try {
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(userSearchBase);
            NamingEnumeration<?> answer = context.search(compoundName, searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attributes = sr.getAttributes();
                if (attributes != null) {
                    javax.naming.directory.Attribute uid = attributes.get(usernameAttribute);
                    if (uid != null) {
                        return (String) uid.get();
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return null;
    }

    private String getUserDN(String userID) throws UserStoreConnectorException {
        String uName = getUserName(userID);
        return usernameAttribute + "=" + uName + "," + userSearchBase;
    }

    private String getGroupName(String groupID) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        StringBuilder groupSearchFilterBuilder = new StringBuilder();
        groupSearchFilterBuilder.append("(&").append(groupListFilter).append("(")
                .append(LDAPConnectorConstants.GROUP_UUID_ATTRIBUTE_NAME).append("=?))");
        String searchFilter = groupSearchFilterBuilder.toString().replace("?", groupID);
        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] propertyNames = { groupAttribute };
        searchCtls.setReturningAttributes(propertyNames);

        try {
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(groupSearchBase);
            NamingEnumeration<?> answer = context.search(compoundName, searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attributes = sr.getAttributes();
                if (attributes != null) {
                    javax.naming.directory.Attribute gid = attributes.get(groupAttribute);
                    if (gid != null) {
                        return (String) gid.get();
                    }
                }
            }
        } catch (NamingException e) {
            throw new UserStoreConnectorException("Error while getting group from LDAP", e);
        }
        return null;
    }
}
