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

package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.oauth.rest.api.UserinfoApiService;
import org.wso2.carbon.auth.user.info.configuration.UserInfoConfigurationService;
import org.wso2.carbon.auth.user.info.configuration.models.UserInfoConfiguration;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.carbon.auth.user.info.util.UserInfoUtil;

public class UserinfoApiServiceFactoryTest {

    @Test
    public void testGetUserinfoApi() {

        UserInfoConfigurationService userInfoConfigurationService = Mockito.mock(UserInfoConfigurationService.class);
        UserInfoUtil.initializeUserInfoConfigurationService(userInfoConfigurationService);

        UserInfoConfiguration userInfoConfiguration = new UserInfoConfiguration();
        userInfoConfiguration.setResponseBuilderClassName(UserInfoConstants.RESPONSE_BUILDER_CLASS_NAME);

        Mockito.when(userInfoConfigurationService.getUserInfoConfiguration()).thenReturn(userInfoConfiguration)
                .thenThrow(UserInfoException.class);
        UserinfoApiServiceFactory userinfoApiServiceFactory = new UserinfoApiServiceFactory();
        UserinfoApiService userinfoApiService = userinfoApiServiceFactory.getUserinfoApi();
        Assert.assertNotNull(userinfoApiService);

        try {
            userinfoApiServiceFactory.getUserinfoApi();
            Assert.fail("Exception is not thrown");
        } catch (IllegalStateException e) {
            Assert.assertEquals(e.getLocalizedMessage(), "Could not create UserInfoService");
        }
    }


}
