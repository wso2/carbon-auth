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

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Response;

public class ExceptionCodesTest {
    @Test
    public void testDuplicatedErrorCodes() throws Exception {
        Set<Long> codesSet = new HashSet<>();
        for (ExceptionCodes code : ExceptionCodes.values()) {
            if (codesSet.contains(code.getErrorCode())) {
                Assert.fail("Duplicated error code found: " + code.getErrorCode());
            } else {
                codesSet.add(code.getErrorCode());
            }
        }
    }

    @Test
    public void testDuplicatedErrorMessages() throws Exception {
        Set<String> messagesSet = new HashSet<>();
        for (ExceptionCodes code : ExceptionCodes.values()) {
            if (messagesSet.contains(code.getErrorMessage())) {
                Assert.fail("Duplicated error message found: \"" + code.getErrorMessage() + "\"");
            } else {
                messagesSet.add(code.getErrorMessage());
            }
        }
    }

    @Test
    public void testDuplicatedErrorDescriptions() throws Exception {
        Set<String> descriptionsSet = new HashSet<>();
        for (ExceptionCodes code : ExceptionCodes.values()) {
            if (descriptionsSet.contains(code.getErrorDescription())) {
                Assert.fail("Duplicated error description found: \"" + code.getErrorDescription() + "\"");
            } else {
                descriptionsSet.add(code.getErrorDescription());
            }
        }
    }

    @Test
    public void testErrorHTTPCodeValidity() throws Exception {
        for (ExceptionCodes code : ExceptionCodes.values()) {
            Response.Status fromStatusCode = Response.Status.fromStatusCode(code.getHttpStatusCode());
            Assert.assertNotNull(fromStatusCode, "Unsupported HTTP status code: " + code.getHttpStatusCode());
        }
    }

    @Test
    public void testValueOf() throws Exception {
        ExceptionCodes internalErrorExceptionCode = ExceptionCodes.valueOf(ExceptionCodes.INTERNAL_ERROR.name());
        Assert.assertNotNull(internalErrorExceptionCode);
        Assert.assertEquals(internalErrorExceptionCode.getErrorCode(), ExceptionCodes.INTERNAL_ERROR.getErrorCode());
        Assert.assertEquals(internalErrorExceptionCode.getHttpStatusCode(),
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
