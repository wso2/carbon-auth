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

package org.wso2.carbon.auth.core.exception;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("ThrowableNotThrown")
public class AuthExceptionTest {
    @Test
    public void testGetErrorHandler() throws Exception {
        AuthException authException1 = new AuthException();
        Assert.assertNotNull(authException1.getErrorHandler());
        Assert.assertEquals(authException1.getErrorHandler(), ExceptionCodes.INTERNAL_ERROR);

        AuthException authException2 = new AuthException("message");
        Assert.assertNotNull(authException2.getErrorHandler());
        Assert.assertEquals(authException2.getErrorHandler(), ExceptionCodes.INTERNAL_ERROR);

        AuthException authException3 = new AuthException("message", new Exception());
        Assert.assertNotNull(authException3.getErrorHandler());
        Assert.assertEquals(authException3.getErrorHandler(), ExceptionCodes.INTERNAL_ERROR);

        AuthException authException4 = new AuthException(new Exception());
        Assert.assertNotNull(authException4.getErrorHandler());
        Assert.assertEquals(authException4.getErrorHandler(), ExceptionCodes.INTERNAL_ERROR);

        AuthException authException5 = new AuthException("message", ExceptionCodes.DATA_NOT_FOUND);
        Assert.assertNotNull(authException5.getErrorHandler());
        Assert.assertEquals(authException5.getErrorHandler(), ExceptionCodes.DATA_NOT_FOUND);

        AuthException authException6 = new AuthException("message", new Exception(), ExceptionCodes.DATA_NOT_FOUND);
        Assert.assertNotNull(authException6.getErrorHandler());
        Assert.assertEquals(authException6.getErrorHandler(), ExceptionCodes.DATA_NOT_FOUND);
        
        
    }
}
