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

package org.wso2.carbon.auth.user.info;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.carbon.auth.user.info.util.UserInfoUtil;
import org.wso2.charon3.core.attributes.Attribute;

import java.util.Map;

/**
 * Abstract representation of the UserInfoResponse. The response can be a JSON
 * or a JWT
 */
public abstract class AbstractUserInfoResponseBuilder implements UserInfoResponseBuilder {

    private static final Log log = LogFactory.getLog(AbstractUserInfoResponseBuilder.class);

    /**
     * Get response string based on the introspection response
     *
     * @see UserInfoResponseBuilder#getResponseString(IntrospectionResponse)
     */
    @Override
    public String getResponseString(IntrospectionResponse introspectionResponse) throws UserInfoException {

        // Retrieve user attributes
        Map<String, Attribute> userAttributes = retrieveUserAttributes(introspectionResponse);

        // Filter user attributes
        String[] requestedScopes = introspectionResponse.getScope().split(" ");
        Map<String, Object> filteredUserAttributes = getUserAttributesFilteredByScope(userAttributes,
                requestedScopes);

        // Handle subject attribute.
        String subAttributeValue = getSubjectAttribute(introspectionResponse);
        filteredUserAttributes.put(OAuthConstants.SUB, subAttributeValue);

        return buildResponse(introspectionResponse, filteredUserAttributes);
    }

    /**
     * Retrieve User attributes.
     *
     * @param introspectionResponse Introspection response
     * @return List of Attributes
     * @throws UserInfoException if failed to retrieve user attributes
     */
    protected abstract Map<String, Attribute> retrieveUserAttributes(IntrospectionResponse introspectionResponse)
            throws UserInfoException;


    /**
     * Filter user attributes based on the requested scopes.
     *
     * @param userAttributes  Map of user attributes
     * @param requestedScopes Requested scopes
     * @return Map of filtered user attribute values
     */
    protected Map<String, Object> getUserAttributesFilteredByScope(Map<String, Attribute> userAttributes,
                                                                   String[] requestedScopes) throws UserInfoException {

        return UserInfoUtil.getUserAttributesFilteredByScope(userAttributes, requestedScopes);
    }


    /**
     * Get the 'sub' attribute.
     *
     * @param introspectionResponse Introspection response
     * @return Value of the sub attribute
     */
    protected String getSubjectAttribute(IntrospectionResponse introspectionResponse) {

        return introspectionResponse.getUsername();
    }

    /**
     * Build UserInfo response to be sent back to the client.
     *
     * @param introspectionResponse  Introspection response
     * @param filteredUserAttributes Filtered user attributes based on the requested scopes
     * @return UserInfo Response String to be sent in the response.
     * @throws UserInfoException if failed to build the response
     */
    protected abstract String buildResponse(IntrospectionResponse introspectionResponse, Map<String, Object>
            filteredUserAttributes) throws UserInfoException;

}
