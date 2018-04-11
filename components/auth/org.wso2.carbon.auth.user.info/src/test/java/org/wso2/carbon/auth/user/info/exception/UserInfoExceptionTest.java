/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.user.info.exception;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;

@SuppressWarnings("ThrowableNotThrown")
public class UserInfoExceptionTest {
    @Test
    public void testGetErrorHandler() {

        UserInfoException userInfoException1 = new UserInfoException("message");
        Assert.assertNotNull(userInfoException1.getErrorHandler());
        Assert.assertEquals(userInfoException1.getErrorHandler(), ExceptionCodes.INVALID_REQUEST);

        UserInfoException userInfoException2 = new UserInfoException("message", new Exception());
        Assert.assertNotNull(userInfoException2.getErrorHandler());
        Assert.assertEquals(userInfoException2.getErrorHandler(), ExceptionCodes.INVALID_REQUEST);

        UserInfoException userInfoException3 = new UserInfoException("message", new Exception(),
                ExceptionCodes.DATA_NOT_FOUND);
        Assert.assertNotNull(userInfoException3.getErrorHandler());
        Assert.assertEquals(userInfoException3.getErrorHandler(), ExceptionCodes.DATA_NOT_FOUND);
    }
}
