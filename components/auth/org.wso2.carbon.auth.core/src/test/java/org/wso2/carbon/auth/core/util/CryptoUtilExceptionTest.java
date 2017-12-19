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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.AuthConstants;
import org.wso2.carbon.auth.core.exception.CryptoException;

/**
 * Testing without providing resources needed for encryption
 * <p>
 * For that, setting System.setProperty(AuthConstants.WSO2_RUNTIME_PATH, "src/test") is avoided
 */
public class CryptoUtilExceptionTest {
    private static final String SAMPLE = "sample_data";
    private static final String SAMPLE_ENCRYPTED_BASE64_ENCODED = "VzkSx4EVhBJnopCwUa6Zbg==";
    private static final Logger log = LoggerFactory.getLogger(CryptoUtilExceptionTest.class);

    @BeforeClass
    public void before() {
        System.clearProperty(AuthConstants.WSO2_RUNTIME_PATH);
    }

    @Test
    public void testEncryptAndBase64Encode() throws Exception {
        try {
            CryptoUtil.getDefaultCryptoUtil()
                    .encryptAndBase64Encode(SAMPLE.getBytes(Charsets.UTF_8));
            Assert.fail();
        } catch (CryptoException e) {
            log.debug("Expected error while encryption - " + SAMPLE, e);
        }
    }

    @Test
    public void testBase64DecodeAndDecrypt() throws Exception {
        try {
            CryptoUtil.getDefaultCryptoUtil()
                    .base64DecodeAndDecrypt(SAMPLE_ENCRYPTED_BASE64_ENCODED);
            Assert.fail();
        } catch (CryptoException e) {
            log.debug("Expected error while decryption - " + SAMPLE_ENCRYPTED_BASE64_ENCODED, e);
        }
    }
}
