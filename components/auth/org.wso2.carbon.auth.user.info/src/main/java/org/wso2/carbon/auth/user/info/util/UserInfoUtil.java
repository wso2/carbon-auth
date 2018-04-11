/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.user.info.util;

import org.json.JSONException;
import org.json.JSONStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.scim.SCIMManager;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.user.info.UserInfoResponseBuilder;
import org.wso2.carbon.auth.user.info.configuration.UserInfoConfigurationService;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.carbon.auth.user.info.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.store.configuration.DefaultAttributes;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Util class implementation for user info
 */
public class UserInfoUtil {

    private static UserManager userManager;
    private static UserInfoConfigurationService userInfoConfigurationService;
    private static final Logger log = LoggerFactory.getLogger(UserInfoUtil.class);

    private UserInfoUtil() {
    }

    /**
     * Initialize user manager
     *
     * @param userManager UserManager instance
     * @throws UserInfoException If failed to initialize
     */
    public static synchronized void initializeUserManager(UserManager userManager) throws UserInfoException {

        if (UserInfoUtil.userManager != null) {
            log.debug("User Manager is already initialized");
            return;
        }
        UserInfoUtil.userManager = userManager;
    }

    /**
     * Retrieve user manager instance
     *
     * @return UserManager instance of UserManager
     * @throws UserInfoException If failed to retrieve userManager instance
     */
    public static synchronized UserManager getUserManager() throws UserInfoException {

        if (userManager == null) {
            try {
                UserInfoUtil.userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            } catch (AuthUserManagementException e) {
                String errorMsg = "Error while retrieving SCIM Manager instance.";
                throw new UserInfoException(errorMsg, e, ExceptionCodes.INTERNAL_ERROR);
            }
        }
        return userManager;
    }

    /**
     * Retrieve user attribute for given introspect response
     *
     * @param introspectionResponse Introspect response
     * @return Map of user attributes
     * @throws UserInfoException If failed to retrieve user attributes
     */
    public static Map<String, Attribute> getUserAttributes(IntrospectionResponse introspectionResponse)
            throws UserInfoException {

        Map<String, Attribute> userAttributes = new HashMap<>();
        try {
            User user = getUserManager().getMe(introspectionResponse.getUsername(), null);
            if (user != null) {
                userAttributes = user.getAttributeList();
            }
        } catch (CharonException | BadRequestException | NotFoundException e) {
            String errorMsg = "Error while retrieving user attributes.";
            throw new UserInfoException(errorMsg, e, ExceptionCodes.INTERNAL_ERROR);
        }

        return userAttributes;
    }

    /**
     * Initialize user info configuration service
     *
     * @param userInfoConfigurationService UserInfoConfigurationService instance
     */
    public static synchronized void initializeUserInfoConfigurationService(UserInfoConfigurationService
                                                                                   userInfoConfigurationService) {
        if (UserInfoUtil.userInfoConfigurationService != null) {
            log.debug("User Info Configuration Service is already initialized");
            return;
        }
        UserInfoUtil.userInfoConfigurationService = userInfoConfigurationService;
    }

    /**
     * Retrieve user info configuration service
     *
     * @return UserInfoConfigurationService instance of UserInfoConfigurationService
     */
    public static synchronized UserInfoConfigurationService getUserInfoConfigurationService() {

        if (userInfoConfigurationService == null) {
            UserInfoUtil.userInfoConfigurationService = ServiceReferenceHolder.getInstance()
                    .getUserInfoConfigurationService();
        }
        return userInfoConfigurationService;
    }

    /**
     * Retrieve UserInfoResponseBuilder
     *
     * @return UserInfoResponseBuilder instance
     * @throws UserInfoException if failed to retrieve UserInfoResponseBuilder instance
     */
    public static UserInfoResponseBuilder getUserInfoResponseBuilder() throws UserInfoException {

        UserInfoConfigurationService userInfoConfigurationService = UserInfoUtil.getUserInfoConfigurationService();
        String responseBuilderClassName = userInfoConfigurationService.getUserInfoConfiguration()
                .getResponseBuilderClassName();

        try {
            UserInfoResponseBuilder userInfoResponseBuilder = (UserInfoResponseBuilder) Class
                    .forName(responseBuilderClassName).newInstance();
            return userInfoResponseBuilder;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new UserInfoException("Error while initializing UserInfoResponseBuilder", e);
        }
    }

    /**
     * Filter user attributes based on the requested scopes.
     *
     * @param userAttributes  Map of user attributes
     * @param requestedScopes Requested scopes
     * @return Map of filtered user attribute values
     */
    public static Map<String, Object> getUserAttributesFilteredByScope(Map<String, Attribute> userAttributes,
                                                                       String[] requestedScopes)
            throws UserInfoException {

        if (userAttributes.isEmpty()) {
            // No user attributes to filter.
            log.debug("No user attributes to filter. Returning an empty map of filtered user attributes.");
            return new HashMap<>();
        }

        List<String> requiredUserAttributes = getUserInfoConfigurationService().getUserInfoConfiguration()
                .getRequiredUserAttributes();

        Map<String, Object> filteredUserAttributes = new HashMap<>();

        if (!requiredUserAttributes.isEmpty()) {

            Map<String, String> extractedUserAttributes = extractUserAttributes(userAttributes);

            for (String requiredUserAttribute : requiredUserAttributes) {

                String userAttribute = extractedUserAttributes.get(requiredUserAttribute);
                if (userAttribute != null) {
                    filteredUserAttributes.put(requiredUserAttribute, userAttribute);
                    log.debug("Required user attribute: {} is found.", requiredUserAttribute);
                }

            }

            handleEmailAttributes(requiredUserAttributes, userAttributes, filteredUserAttributes);

        } else {
            log.debug("Required user attributes are empty. Returning an empty map of filtered user attributes.");
        }

        return filteredUserAttributes;
    }

    /**
     * Extract user attributes values and return a map with values.
     *
     * @param userAttributes Map of user attributes
     * @return Map of user attribute values
     */
    private static Map<String, String> extractUserAttributes(Map<String, Attribute> userAttributes) {

        Map<String, String> extractedUserAttributes = new HashMap<>();

        for (Map.Entry<String, Attribute> attribute : userAttributes.entrySet()) {
            Attribute userAttribute = attribute.getValue();
            String userAttributeKey = attribute.getKey();

            if (userAttribute != null) {
                if (userAttribute instanceof SimpleAttribute) {

                    String userAttributeValue = ((SimpleAttribute) userAttribute).getValue().toString();
                    extractedUserAttributes.put(userAttributeKey, userAttributeValue);
                    log.debug("Extracted user attribute: {} and attribute value: {} from simple attribute.",
                            userAttributeKey, userAttributeValue);

                } else if (userAttribute instanceof ComplexAttribute) {

                    Map<String, Attribute> subAttributeList = ((ComplexAttribute) userAttribute).
                            getSubAttributesList();
                    for (Map.Entry<String, Attribute> subAttributeEntry : subAttributeList.entrySet()) {

                        Attribute subAttribute = subAttributeEntry.getValue();
                        String subAttributeKey = subAttributeEntry.getKey();

                        if (subAttribute instanceof SimpleAttribute) {
                            String subAttributeValue = ((SimpleAttribute) subAttribute).getValue().toString();
                            extractedUserAttributes.put(subAttributeKey, subAttributeValue);
                            log.debug("Extracted user attribute: {} and attribute value: {} from complex attribute.",
                                    subAttributeKey, subAttributeValue);
                        }

                    }

                }
            }
        }

        return extractedUserAttributes;
    }

    /**
     * Handle email attribute by going through multi valued attributes.
     *
     * @param requiredUserAttributes List of required user attributes
     * @param userAttributes         Map of user attributes
     * @param filteredUserAttributes Filtered user attributes
     * @throws UserInfoException if failed to handle email attributes
     */
    private static void handleEmailAttributes(List<String> requiredUserAttributes, Map<String, Attribute>
            userAttributes, Map<String, Object> filteredUserAttributes) throws UserInfoException {

        Attribute emailsAttribute = userAttributes.get(SCIMConstants.UserSchemaConstants.EMAILS);

        if (emailsAttribute != null && emailsAttribute instanceof MultiValuedAttribute) {

            List<Attribute> multiValuedAttributes = ((MultiValuedAttribute) emailsAttribute).getAttributeValues();
            Map<String, String> emailValues = getEmailValues(multiValuedAttributes);

            if (requiredUserAttributes.contains(DefaultAttributes.USER_EMAIL_HOME.getAttributeName())) {
                String homeEmail = emailValues.get(SCIMConstants.UserSchemaConstants.HOME);
                if (homeEmail != null) {
                    filteredUserAttributes.put(DefaultAttributes.USER_EMAIL_HOME.getAttributeName(), homeEmail);
                }
            }

            if (requiredUserAttributes.contains(DefaultAttributes.USER_EMAIL_WORK.getAttributeName())) {
                String workEmail = emailValues.get(SCIMConstants.UserSchemaConstants.WORK);
                if (workEmail != null) {
                    filteredUserAttributes.put(DefaultAttributes.USER_EMAIL_WORK.getAttributeName(), workEmail);
                }
            }
        }
    }

    /**
     * Get email values from multi valued attributes.
     *
     * @param multiValuedAttributes List of multi valued attributes
     * @throws UserInfoException if failed to get email values
     */
    private static Map<String, String> getEmailValues(List<Attribute> multiValuedAttributes) throws UserInfoException {

        Map<String, String> emailValues = new HashMap<>();
        try {
            for (Attribute attribute : multiValuedAttributes) {
                SimpleAttribute type = (SimpleAttribute) (attribute).getSubAttribute(SCIMConstants
                        .CommonSchemaConstants.TYPE);
                SimpleAttribute value = (SimpleAttribute) (attribute).getSubAttribute(SCIMConstants
                        .CommonSchemaConstants.VALUE);
                emailValues.put(type.getStringValue(), value.getStringValue());
            }
        } catch (CharonException e) {
            String errorMsg = "Error while retrieving email attributes";
            throw new UserInfoException(errorMsg, e);
        }

        return emailValues;
    }

    /**
     * Build Json and return as a String value.
     *
     * @param params Map of values
     * @throws UserInfoException if failed to build the json
     */
    public static String buildJSON(Map<String, Object> params) throws UserInfoException {

        JSONStringer stringer = new JSONStringer();
        try {
            stringer.object();
            Iterator iterator = params.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry param = (Map.Entry) iterator.next();
                if (param.getKey() != null && !"".equals(param.getKey()) && param.getValue() != null && !"".equals
                        (param.getValue())) {
                    stringer.key((String) param.getKey()).value(param.getValue());
                }
            }
            stringer.endObject();
        } catch (JSONException e) {
            String errorMsg = "Error while building the json response";
            throw new UserInfoException(errorMsg, e);
        }
        return stringer.toString();
    }
}
