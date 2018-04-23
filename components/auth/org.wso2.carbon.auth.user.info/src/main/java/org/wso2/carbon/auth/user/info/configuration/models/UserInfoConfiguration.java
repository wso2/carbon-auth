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

package org.wso2.carbon.auth.user.info.configuration.models;

import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.store.configuration.DefaultAttributes;
import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold user info configuration
 */

@Configuration(namespace = "wso2.carbon.auth.user.info", description = "User Info Configuration Parameters")
public class UserInfoConfiguration {

    @Element(description = "Response Builder Class Name")
    private String responseBuilderClassName = UserInfoConstants.RESPONSE_BUILDER_CLASS_NAME;

    @Element(description = "Scope to claim dialects mapping")
    private Map<String, List<String>> scopeToClaimDialectsMapping = new HashMap<>();

    public UserInfoConfiguration() {
        List<String> requiredUserAttributes = new ArrayList<>();
        requiredUserAttributes.add(OAuthConstants.SUB);
        requiredUserAttributes.add(DefaultAttributes.USER_FAMILY_NAME.getAttributeName());
        requiredUserAttributes.add(DefaultAttributes.USER_GIVEN_NAME.getAttributeName());
        requiredUserAttributes.add(DefaultAttributes.USER_EMAIL_WORK.getAttributeName());
        requiredUserAttributes.add(DefaultAttributes.USER_EMAIL_HOME.getAttributeName());
        requiredUserAttributes.add(SCIMConstants.UserSchemaConstants.EMAILS);
        scopeToClaimDialectsMapping.put(UserInfoConstants.OPENID, requiredUserAttributes);
    }

    public String getResponseBuilderClassName() {
        return responseBuilderClassName;
    }

    public void setResponseBuilderClassName(String responseBuilderClassName) {
        this.responseBuilderClassName = responseBuilderClassName;
    }

    public Map<String, List<String>> getScopeToClaimDialectsMapping() {
        return scopeToClaimDialectsMapping;
    }

    public void setScopeToClaimDialectsMapping(Map<String, List<String>> scopeToClaimDialectsMapping) {
        this.scopeToClaimDialectsMapping = scopeToClaimDialectsMapping;
    }

}
