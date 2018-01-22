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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.Constants;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.security.auth.callback.PasswordCallback;

/**
 * LDAP user store connection implementation
 */
public class LDAPUserStoreConnector implements UserStoreConnector {
    private static Logger log = LoggerFactory.getLogger(LDAPUserStoreConnector.class);
    protected LDAPConnectionContext ldapConnectionContext;
    protected UserStoreConfiguration userStoreConfig;
    private String userSearchBase;
    private String groupSearchBase;
    private String usernameAttribute;
    private String groupAttribute;
    private Map<String, Object> properties;

    public LDAPUserStoreConnector() {
    }

    @Override
    public void init(UserStoreConfiguration userStoreConfiguration) throws UserStoreConnectorException {
        this.userStoreConfig = userStoreConfiguration;
        this.ldapConnectionContext = new LDAPConnectionContext(userStoreConfiguration);
        this.properties = this.userStoreConfig.getLdapProperties();

        userSearchBase = (String) this.properties.get(Constants.LDAP_USER_SEARCH_BASE);
        groupSearchBase = (String) this.properties.get(Constants.LDAP_GROUP_SEARCH_BASE);
        usernameAttribute = (String) this.properties.get(Constants.LDAP_USERNAME_ATTRIBUTE);
        groupAttribute = (String) this.properties.get(Constants.LDAP_GROUP_ATTRIBUTE);
    }

    @Override
    public String getConnectorUserId(String attributeName, String attributeValue)
            throws UserNotFoundException, UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        String mappedAttributeName = LdapUtils.mappingClaim(attributeName);
        matchAttrs.put(new BasicAttribute(mappedAttributeName, attributeValue));

        try {
            //            NameParser ldapParser = context.getNameParser("");
            //            Name compoundName = ldapParser.parse(userSearchBase);
            NamingEnumeration<SearchResult> enumeration = context.search(userSearchBase, matchAttrs);
            if (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                return (String) next.getAttributes().get(usernameAttribute).get();
            } else {
                throw new UserNotFoundException("User not found with the given attribute");
            }
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public List<String> listConnectorUserIds(String attributeName, String attributeValue, int offset, int length)
            throws UserStoreConnectorException {
        DirContext context;
        List<String> userList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        String mappedAttributeName = LdapUtils.mappingClaim(attributeName);
        matchAttrs.put(new BasicAttribute(mappedAttributeName, attributeValue));

        try {
            //            NameParser ldapParser = context.getNameParser("");
            //            Name compoundName = ldapParser.parse(userSearchBase);
            NamingEnumeration<SearchResult> enumeration = context.search(userSearchBase, matchAttrs);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                userList.add((String) next.getAttributes().get(usernameAttribute).get());
            }
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
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
            //            Name compoundName = ldapParser.parse(userSearchBase);
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + userID + "," + userSearchBase);
            Attributes attributes = context.getAttributes(compoundName);
            NamingEnumeration<String> ids = attributes.getIDs();
            while (ids.hasMoreElements()) {
                String id = ids.next();
                javax.naming.directory.Attribute attribute = attributes.get(id);
                attributeList.add(new Attribute(id, (String) attribute.get()));
            }

        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return attributeList;
    }

    @Override
    public String getConnectorGroupId(String attributeName, String attributeValue)
            throws GroupNotFoundException, UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        String mappedAttributeName = LdapUtils.mappingClaim(attributeName);
        matchAttrs.put(new BasicAttribute(mappedAttributeName, attributeValue));

        try {
            //            NameParser ldapParser = context.getNameParser("");
            //            Name compoundName = ldapParser.parse(userSearchBase);
            NamingEnumeration<SearchResult> enumeration = context.search(groupSearchBase, matchAttrs);
            if (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                return (String) next.getAttributes().get(groupAttribute).get();
            } else {
                throw new GroupNotFoundException("User not found with the given attribute");
            }
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public List<String> listConnectorGroupIds(String attributeName, String attributeValue, int offset, int length)
            throws UserStoreConnectorException {
        DirContext context;
        List<String> groupList = new ArrayList<>();
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        String mappedAttributeName = LdapUtils.mappingClaim(attributeName);
        matchAttrs.put(new BasicAttribute(mappedAttributeName, attributeValue));

        try {
            //            NameParser ldapParser = context.getNameParser("");
            //            Name compoundName = ldapParser.parse(userSearchBase);
            NamingEnumeration<SearchResult> enumeration = context.search(groupSearchBase, matchAttrs);
            while (enumeration.hasMoreElements()) {
                SearchResult next = enumeration.next();
                groupList.add((String) next.getAttributes().get(groupAttribute).get());
            }
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return groupList;
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
            //            Name compoundName = ldapParser.parse(userSearchBase);
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupId + "," + groupSearchBase);
            Attributes attributes = context.getAttributes(compoundName);
            NamingEnumeration<String> ids = attributes.getIDs();
            while (ids.hasMoreElements()) {
                String id = ids.next();
                javax.naming.directory.Attribute attribute = attributes.get(id);
                attributeList.add(new Attribute(id, (String) attribute.get()));
            }

        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return attributeList;
    }

    @Override
    public boolean isUserInGroup(String userId, String groupId) throws UserStoreConnectorException {
        //todo: not yet implemented
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
        return null;
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
        for (Attribute attribute : attributes) {
            if (UserStoreConstants.CLAIM_USERNAME.equals(attribute.getAttributeName())) {
                username = attribute.getAttributeValue();
                break;
            }
        }
        BasicAttributes basicAttributes = getUserBasicAttributes(username);
        setClaims(attributes, basicAttributes, username);
        try {

            //            context = (DirContext) context.lookup(userSearchBase);
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + username + "," + userSearchBase);
            context.createSubcontext(compoundName, basicAttributes);
        } catch (NamingException e) {
            log.error("Error adding user to LDAP", e);
            throw new UserStoreConnectorException("Error adding user to LDAP", e);
        }
        return username;
    }

    @Override
    public String updateUserAttributes(String userIdentifier, List<Attribute> attributes)
            throws UserStoreConnectorException {
        //todo: not yet implemented
        return null;
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
            //                        NameParser ldapParser = context.getNameParser("");
            //            Name compoundName = ldapParser.parse(userSearchBase);
            context.destroySubcontext(usernameAttribute + "=" + userIdentifier + "," + userSearchBase);
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public void updateGroupsOfUser(String userIdentifier, List<String> groupIdentifiers)
            throws UserStoreConnectorException {
        //todo: not yet implemented
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
        for (Attribute attribute : attributes) {
            if (UserStoreConstants.CLAIM_USERNAME.equals(attribute.getAttributeName())) {
                groupName = attribute.getAttributeValue();
                break;
            }
        }
        BasicAttributes basicAttributes = getGroupBasicAttributes(groupName);
        setClaims(attributes, basicAttributes, groupName);
        try {

            //            context = (DirContext) context.lookup(userSearchBase);
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(groupAttribute + "=" + groupName + "," + groupSearchBase);
            context.createSubcontext(compoundName, basicAttributes);
        } catch (NamingException e) {
            log.error("Error adding user to LDAP", e);
            throw new UserStoreConnectorException("Error adding user to LDAP", e);
        }
        return groupName;
    }

    @Override
    public Map<String, String> addGroups(Map<String, List<Attribute>> attributes) throws UserStoreConnectorException {
        //todo: not yet implemented
        return null;
    }

    @Override
    public String updateGroupAttributes(String groupIdentifier, List<Attribute> attributes)
            throws UserStoreConnectorException {
        //todo: not yet implemented
        return null;
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
            //                        NameParser ldapParser = context.getNameParser("");
            //            Name compoundName = ldapParser.parse(userSearchBase);
            context.destroySubcontext(groupAttribute + "=" + groupIdentifier + "," + groupSearchBase);
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
    }

    @Override
    public void updateUsersOfGroup(String groupIdentifier, List<String> userIdentifiers)
            throws UserStoreConnectorException {
        //todo: not yet implemented
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
            //            context = (DirContext) context.lookup(userSearchBase);
            NameParser ldapParser = context.getNameParser("");
            Name compoundName = ldapParser.parse(usernameAttribute + "=" + userIdentifier + "," + userSearchBase);
            context.modifyAttributes(compoundName, DirContext.ADD_ATTRIBUTE, basicAttributes);
        } catch (NamingException e) {
            log.error("Error adding user credentials to LDAP", e);
            throw new UserStoreConnectorException("Error adding user credentials to LDAP", e);
        }

        return userIdentifier;
    }

    @Override
    public String updateCredentials(String userIdentifier, PasswordCallback passwordCallback)
            throws UserStoreConnectorException {
        //todo: not yet implemented
        return null;
    }

    @Override
    public void deleteCredential(String userIdentifier) throws UserStoreConnectorException {
        //todo: not yet implemented
    }

    @Override
    public Map getUserPasswordInfo(String userId) throws UserStoreConnectorException {
        DirContext context;
        try {
            context = ldapConnectionContext.getContext();
        } catch (LDAPConnectorException e) {
            throw new UserStoreConnectorException("Error getting LDAP context ", e);
        }

        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute(usernameAttribute, userId));

        try {
            NamingEnumeration<SearchResult> enumeration = context.search(userSearchBase, matchAttrs);
            while (enumeration.hasMoreElements()) {
                Map info = new HashMap();
                SearchResult next = enumeration.next();
                String userPassword = new String(
                        (byte[]) next.getAttributes().get(LDAPConnectorConstants.USER_PASSWORD_ATTRIBUTE_NAME).get(),
                        Charset.defaultCharset());
                String userPasswordSalt = (String) next.getAttributes()
                        .get(LDAPConnectorConstants.USER_PASSWORD_SALT_ATTRIBUTE_NAME).get();
                info.put(UserStoreConstants.PASSWORD, userPassword);
                info.put(UserStoreConstants.PASSWORD_SALT, userPasswordSalt);
                info.put(UserStoreConstants.HASH_ALGO, getHashAlgo());
                info.put(UserStoreConstants.ITERATION_COUNT, getIterationCount());
                info.put(UserStoreConstants.KEY_LENGTH, getKeyLength());
                return info;
            }
        } catch (NamingException e) {
            log.error("Error while getting user from LDAP", e);
            throw new UserStoreConnectorException("Error while getting user from LDAP", e);
        }
        return null;
    }

    protected BasicAttributes getUserBasicAttributes(String username) {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        String userEntryObjectClassProperty = (String) this.properties.get(Constants.LDAP_USER_ENTRY_OBJECT_CLASS);
        BasicAttribute objectClass = new BasicAttribute(Constants.OBJECT_CLASS_NAME);
        String[] objectClassHierarchy = userEntryObjectClassProperty.split("/");
        for (String userObjectClass : objectClassHierarchy) {
            if (userObjectClass != null && !userObjectClass.trim().equals("")) {
                objectClass.add(userObjectClass.trim());
            }
        }
        objectClass.add("extensibleObject");
        basicAttributes.put(objectClass);

        BasicAttribute userNameAttribute = new BasicAttribute(usernameAttribute);
        userNameAttribute.add(username);
        basicAttributes.put(userNameAttribute);
        return basicAttributes;
    }

    protected BasicAttributes getGroupBasicAttributes(String groupName) {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        String userEntryObjectClassProperty = (String) this.properties.get(Constants.LDAP_GROUP_ENTRY_OBJECT_CLASS);
        BasicAttribute objectClass = new BasicAttribute(Constants.OBJECT_CLASS_NAME);
        String[] objectClassHierarchy = userEntryObjectClassProperty.split("/");
        for (String userObjectClass : objectClassHierarchy) {
            if (userObjectClass != null && !userObjectClass.trim().equals("")) {
                objectClass.add(userObjectClass.trim());
            }
        }
        objectClass.add("extensibleObject");
        basicAttributes.put(objectClass);

        BasicAttribute userNameAttribute = new BasicAttribute(groupAttribute);
        userNameAttribute.add(groupName);
        basicAttributes.put(userNameAttribute);
        return basicAttributes;
    }

    protected void setClaims(List<Attribute> attributes, BasicAttributes basicAttributes, String uniqueName) {
        log.debug("Processing user claims");

        boolean isSNExists = false;
        boolean isCNExists = false;
        for (Attribute attribute : attributes) {
            if (Constants.ATTR_NAME_CN.equals(attribute.getAttributeName())) {
                isCNExists = true;
            } else if (Constants.ATTR_NAME_SN.equals(attribute.getAttributeName())) {
                isSNExists = true;
            }
            log.debug("Mapped attribute: " + attribute.getAttributeName());
            log.debug("Attribute value: " + attribute.getAttributeValue());
            String ldapClaim = LdapUtils.mappingClaim(attribute.getAttributeName());
            BasicAttribute uniqueNameAttribute = new BasicAttribute(ldapClaim);
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
}
