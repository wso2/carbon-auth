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

package org.wso2.carbon.auth.core.util;

import org.apache.commons.io.Charsets;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.AuthConstants;

import java.io.File;

public class CryptoUtilTest {
    private static final String SAMPLE = "sample_data";
    private static final String SAMPLE_ENCRYPTED_BASE64_ENCODED = "VzkSx4EVhBJnopCwUa6Zbg==";
    private static final String TEST_FOLDER_RELATIVE = "src" + File.separator + "test";

    @BeforeClass
    public void before() {
        System.setProperty(AuthConstants.WSO2_RUNTIME_PATH, TEST_FOLDER_RELATIVE);
    }
    
    @Test
    public void testEncryptAndBase64Encode() throws Exception {
        String base64Encoded = CryptoUtil.getDefaultCryptoUtil()
                .encryptAndBase64Encode(SAMPLE.getBytes(Charsets.UTF_8));
        Assert.assertEquals(base64Encoded, SAMPLE_ENCRYPTED_BASE64_ENCODED,
                "Encrypted and base64 encoded " + SAMPLE + " is wrong");
    }

    @Test
    public void testBase64DecodeAndDecrypt() throws Exception {
        String decrypted = new String(CryptoUtil.getDefaultCryptoUtil()
                .base64DecodeAndDecrypt(SAMPLE_ENCRYPTED_BASE64_ENCODED));
        Assert.assertEquals(decrypted, SAMPLE,
                "Base64 decoded and decrypted " + SAMPLE_ENCRYPTED_BASE64_ENCODED + " is wrong");
    }

    @AfterClass
    public void after() {
        System.clearProperty(AuthConstants.WSO2_RUNTIME_PATH);
    }
}
