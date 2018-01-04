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

package org.wso2.carbon.auth.client.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.exception.PersistenceProcessorException;
import org.wso2.carbon.auth.client.registration.impl.ClientRegistrationHandlerImplTest;
import org.wso2.carbon.auth.core.AuthConstants;

import java.io.File;

public class EncryptionDecryptionPersistenceProcessorTest {
    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationHandlerImplTest.class);
    private static final String SECRET = "secret123";
    private static final String ENCRYPTED_SECRET = "FCW2hghkImChSYMNlYY0cA==";
    private static final String TEST_FOLDER_RELATIVE = "src" + File.separator + "test";
    
    @Test
    public void testGetPreprocessedClientSecret() throws Exception {
        System.setProperty(AuthConstants.WSO2_RUNTIME_PATH, TEST_FOLDER_RELATIVE);
        EncryptionDecryptionPersistenceProcessor processor = new EncryptionDecryptionPersistenceProcessor();
        try {
            String secret = processor.getPreprocessedClientSecret(ENCRYPTED_SECRET);
            Assert.assertEquals(secret, SECRET);
        } catch (Exception e) {
            log.error("Error while decryption - " + ENCRYPTED_SECRET, e);
            Assert.fail();
        }
    }

    @Test
    public void testGetProcessedClientSecret() throws Exception {
        System.setProperty(AuthConstants.WSO2_RUNTIME_PATH, TEST_FOLDER_RELATIVE);
        EncryptionDecryptionPersistenceProcessor processor = new EncryptionDecryptionPersistenceProcessor();
        try {
            String afterEncryption = processor.getProcessedClientSecret(SECRET);
            Assert.assertEquals(afterEncryption, ENCRYPTED_SECRET);
        } catch (Exception e) {
            log.error("Error while encryption - " + SECRET, e);
            Assert.fail();
        }
    }

    @Test
    public void testGetPreprocessedClientSecretWithoutSettingConfigsProperly() throws Exception {
        System.clearProperty(AuthConstants.WSO2_RUNTIME_PATH);
        EncryptionDecryptionPersistenceProcessor processor = new EncryptionDecryptionPersistenceProcessor();
        try {
            processor.getPreprocessedClientSecret(ENCRYPTED_SECRET);
            Assert.fail();
        } catch (PersistenceProcessorException e) {
            log.debug("Expected error while decryption - " + ENCRYPTED_SECRET, e);
        }
    }

    @Test
    public void testGetProcessedClientSecretWithoutSettingConfigsProperly() throws Exception {
        System.clearProperty(AuthConstants.WSO2_RUNTIME_PATH);
        EncryptionDecryptionPersistenceProcessor processor = new EncryptionDecryptionPersistenceProcessor();
        try {
            processor.getProcessedClientSecret(SECRET);
            Assert.fail();
        } catch (PersistenceProcessorException e) {
            log.debug("Expected error while encryption - " + SECRET, e);
        }
    }

    @AfterClass
    public void after() {
        System.clearProperty(AuthConstants.WSO2_RUNTIME_PATH);
    }

}
