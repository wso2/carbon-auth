/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.user.info;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.util.HashMap;
import java.util.Map;

public class AbstractUserInfoResponseBuilderTest extends UserInfoTestBase {

    @Test(priority = 1)
    public void testGetResponseString() throws Exception {

        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setUsername("user1");
        introspectionResponse.setScope(UserInfoConstants.OPENID + " email");

        User user = new User();
        String attributeName = "id";
        SimpleAttribute idAttribute = new SimpleAttribute(attributeName, "id1");
        user.setAttribute(idAttribute);

        SimpleAttribute homeEmailType = new SimpleAttribute("type", "home");
        homeEmailType.setType(SCIMDefinitions.DataType.STRING);
        SimpleAttribute homeEmailValue = new SimpleAttribute("value", "home@wso2.com");
        homeEmailValue.setType(SCIMDefinitions.DataType.STRING);
        Map<String, Attribute> homeEmail = new HashMap<>();
        homeEmail.put("type", homeEmailType);
        homeEmail.put("value", homeEmailValue);

        SimpleAttribute workEmailType = new SimpleAttribute("type", "work");
        workEmailType.setType(SCIMDefinitions.DataType.STRING);
        SimpleAttribute workEmailValue = new SimpleAttribute("value", "work@wso2.com");
        workEmailValue.setType(SCIMDefinitions.DataType.STRING);
        Map<String, Attribute> workEmail = new HashMap<>();
        workEmail.put("type", workEmailType);
        workEmail.put("value", workEmailValue);

        MultiValuedAttribute emails = new MultiValuedAttribute();
        emails.setComplexValueWithSetOfSubAttributes(homeEmail);
        emails.setComplexValueWithSetOfSubAttributes(workEmail);
        emails.setName("emails");
        user.setAttribute(emails);

        ComplexAttribute names = new ComplexAttribute();
        names.setSubAttribute(new SimpleAttribute("givenName", "user1"));
        names.setSubAttribute(new SimpleAttribute("familyName", "user1 family"));
        names.setName("names");
        user.setAttribute(names);

        Mockito.when(userManager.getMe(introspectionResponse.getUsername(), null)).thenReturn(user);

        UserInfoJSONResponseBuilder userInfoJSONResponseBuilder = new UserInfoJSONResponseBuilder();
        String userInfo = userInfoJSONResponseBuilder.getResponseString(introspectionResponse);
        Assert.assertNotNull(userInfo);
    }

    @Test(priority = 2)
    public void testGetResponseStringForInvalidUser() throws Exception {

        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setUsername("user2");
        introspectionResponse.setScope(UserInfoConstants.OPENID);
        Mockito.when(userManager.getMe(introspectionResponse.getUsername(), null)).thenReturn(null);

        UserInfoJSONResponseBuilder userInfoJSONResponseBuilder = new UserInfoJSONResponseBuilder();
        String userInfo = userInfoJSONResponseBuilder.getResponseString(introspectionResponse);
        Assert.assertNotNull(userInfo);
    }

    @Test(priority = 3)
    public void testGetResponseStringForException() throws Exception {

        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setUsername("user3");
        introspectionResponse.setScope(UserInfoConstants.OPENID);
        Mockito.when(userManager.getMe(introspectionResponse.getUsername(), null)).thenThrow(CharonException.class);

        UserInfoJSONResponseBuilder userInfoJSONResponseBuilder = new UserInfoJSONResponseBuilder();
        try {
            userInfoJSONResponseBuilder.getResponseString(introspectionResponse);
            Assert.fail("When retrieving user attributes, UserInfoException is not thrown.");
        } catch (UserInfoException e) {
            Assert.assertEquals(e.getErrorHandler(), ExceptionCodes.INTERNAL_ERROR);
        }

    }

    @Test(priority = 4)
    public void testGetResponseStringWhenRequiredAttributesAreEmpty() throws Exception {

        userInfoConfiguration.setScopeToClaimDialectsMapping(new HashMap<>());
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        introspectionResponse.setUsername("user4");
        introspectionResponse.setScope(UserInfoConstants.OPENID);

        User user = new User();
        String attributeName = "id";
        SimpleAttribute idAttribute = new SimpleAttribute(attributeName, "id1");
        user.setAttribute(idAttribute);

        Mockito.when(userManager.getMe(introspectionResponse.getUsername(), null)).thenReturn(user);

        UserInfoJSONResponseBuilder userInfoJSONResponseBuilder = new UserInfoJSONResponseBuilder();
        String userInfo = userInfoJSONResponseBuilder.getResponseString(introspectionResponse);
        Assert.assertNotNull(userInfo);
    }

}
