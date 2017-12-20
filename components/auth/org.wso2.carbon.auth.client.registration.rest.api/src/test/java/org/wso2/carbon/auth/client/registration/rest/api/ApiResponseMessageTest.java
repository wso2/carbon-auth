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

package org.wso2.carbon.auth.client.registration.rest.api;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.wso2.carbon.auth.client.registration.rest.api.ApiResponseMessage.ERROR;
import static org.wso2.carbon.auth.client.registration.rest.api.ApiResponseMessage.INFO;
import static org.wso2.carbon.auth.client.registration.rest.api.ApiResponseMessage.OK;
import static org.wso2.carbon.auth.client.registration.rest.api.ApiResponseMessage.TOO_BUSY;
import static org.wso2.carbon.auth.client.registration.rest.api.ApiResponseMessage.WARNING;

public class ApiResponseMessageTest {
    
    @Test
    public void testConstructor() {
        ApiResponseMessage apiResponseMessageError = new ApiResponseMessage(ERROR, "ERROR");
        ApiResponseMessage apiResponseMessageWarn = new ApiResponseMessage(WARNING, "WARNING");
        ApiResponseMessage apiResponseMessageInfo = new ApiResponseMessage(INFO, "INFO");
        ApiResponseMessage apiResponseMessageOk = new ApiResponseMessage(OK, "OK");
        ApiResponseMessage apiResponseMessageBusy = new ApiResponseMessage(TOO_BUSY, "TOO_BUSY");
        ApiResponseMessage apiResponseMessageUnknown = new ApiResponseMessage(-1, "UNKNOWN");

        Assert.assertEquals(apiResponseMessageError.getCode(), ERROR);
        Assert.assertEquals(apiResponseMessageWarn.getCode(), WARNING);
        Assert.assertEquals(apiResponseMessageInfo.getCode(), INFO);
        Assert.assertEquals(apiResponseMessageOk.getCode(), OK);
        Assert.assertEquals(apiResponseMessageBusy.getCode(), TOO_BUSY);
        Assert.assertEquals(apiResponseMessageUnknown.getCode(), -1);

        Assert.assertEquals(apiResponseMessageError.getMessage(), "ERROR");
        Assert.assertEquals(apiResponseMessageWarn.getMessage(), "WARNING");
        Assert.assertEquals(apiResponseMessageInfo.getMessage(), "INFO");
        Assert.assertEquals(apiResponseMessageOk.getMessage(), "OK");
        Assert.assertEquals(apiResponseMessageBusy.getMessage(), "TOO_BUSY");
        Assert.assertEquals(apiResponseMessageUnknown.getMessage(), "UNKNOWN");

        Assert.assertEquals(apiResponseMessageError.getType(), "error");
        Assert.assertEquals(apiResponseMessageWarn.getType(), "warning");
        Assert.assertEquals(apiResponseMessageInfo.getType(), "info");
        Assert.assertEquals(apiResponseMessageOk.getType(), "ok");
        Assert.assertEquals(apiResponseMessageBusy.getType(), "too busy");
        Assert.assertEquals(apiResponseMessageUnknown.getType(), "unknown");
    }

    @Test
    public void testSetCodeMessage() {
        ApiResponseMessage apiResponseMessage = new ApiResponseMessage();
        apiResponseMessage.setCode(ERROR);
        apiResponseMessage.setMessage("ERROR");
        Assert.assertEquals(apiResponseMessage.getCode(), ERROR);
        Assert.assertEquals(apiResponseMessage.getMessage(), "ERROR");
    }
}
