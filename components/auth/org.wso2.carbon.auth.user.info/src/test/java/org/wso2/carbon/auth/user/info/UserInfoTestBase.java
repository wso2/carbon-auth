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
import org.testng.annotations.BeforeClass;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.user.info.configuration.UserInfoConfigurationService;
import org.wso2.carbon.auth.user.info.configuration.models.UserInfoConfiguration;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.info.util.UserInfoUtil;
import org.wso2.carbon.auth.user.store.configuration.DefaultAttributes;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoTestBase {

    protected UserManager userManager;
    protected UserInfoConfigurationService userInfoConfigurationService;
    protected UserInfoConfiguration userInfoConfiguration;

    @BeforeClass
    public void init() throws Exception {

        userManager = Mockito.mock(UserManager.class);
        UserInfoUtil.setUserManager(userManager);

        userInfoConfigurationService = Mockito.mock(UserInfoConfigurationService.class);
        UserInfoUtil.initializeUserInfoConfigurationService(userInfoConfigurationService);

        userInfoConfiguration = new UserInfoConfiguration();
        Mockito.when(userInfoConfigurationService.getUserInfoConfiguration()).thenReturn(userInfoConfiguration);

        List<String> openidUserAttributes = new ArrayList<>();
        openidUserAttributes.add(OAuthConstants.SUB);
        openidUserAttributes.add(DefaultAttributes.USER_FAMILY_NAME.getAttributeName());
        openidUserAttributes.add(DefaultAttributes.USER_GIVEN_NAME.getAttributeName());
        openidUserAttributes.add(DefaultAttributes.USER_EMAIL_HOME.getAttributeName());

        List<String> emailUserAttributes = new ArrayList<>();
        emailUserAttributes.add(DefaultAttributes.USER_EMAIL_WORK.getAttributeName());
        emailUserAttributes.add(DefaultAttributes.USER_EMAIL_HOME.getAttributeName());
        emailUserAttributes.add(SCIMConstants.UserSchemaConstants.EMAILS);
        emailUserAttributes.add(DefaultAttributes.USER_EMAIL_HOME.getAttributeName());

        Map<String, List<String>> scopeToClaimDialectsMapping = new HashMap<>();
        scopeToClaimDialectsMapping.put(UserInfoConstants.OPENID, openidUserAttributes);
        scopeToClaimDialectsMapping.put("email", emailUserAttributes);
        userInfoConfiguration.setScopeToClaimDialectsMapping(scopeToClaimDialectsMapping);
    }
}
