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

package org.wso2.carbon.auth.oauth.rest.api.impl;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;
import org.wso2.carbon.auth.user.info.UserinfoRequestHandler;
import org.wso2.carbon.auth.user.info.constants.UserInfoConstants;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class UserinfoApiServiceImplTest {

    @Test
    public void testUserinfoGet() throws Exception {

        UserinfoRequestHandler userinfoRequestHandler = Mockito.mock(UserinfoRequestHandler.class);
        Request request = Mockito.mock(Request.class);
        String authorization = "token";
        String schema = UserInfoConstants.OPENID;

        Mockito.when(userinfoRequestHandler.retrieveUserInfo(authorization, schema)).thenReturn("userInfo");
        UserinfoApiServiceImpl userinfoApiService = new UserinfoApiServiceImpl(userinfoRequestHandler);
        Response response = userinfoApiService.userinfoGet(authorization, schema, request);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testUserinfoGetForException() throws Exception {

        UserinfoRequestHandler userinfoRequestHandler = Mockito.mock(UserinfoRequestHandler.class);
        Request request = Mockito.mock(Request.class);
        String authorization = "token";
        String schema = UserInfoConstants.OPENID;

        UserInfoException userInfoException = new UserInfoException("ts", ExceptionCodes.INVALID_REQUEST);
        Mockito.when(userinfoRequestHandler.retrieveUserInfo(authorization, schema)).thenThrow(userInfoException);
        UserinfoApiServiceImpl userinfoApiService = new UserinfoApiServiceImpl(userinfoRequestHandler);
        Response response = userinfoApiService.userinfoGet(authorization, schema, request);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

}
