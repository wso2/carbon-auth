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

package org.wso2.carbon.auth.client.registration.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.EncryptionDecryptionPersistenceProcessor;
import org.wso2.carbon.auth.client.registration.exception.PersistenceProcessorException;
import org.wso2.carbon.auth.client.registration.impl.ClientRegistrationHandlerImplTest;
import org.wso2.carbon.auth.core.AuthConstants;

public class EncryptionDecryptionPersistenceProcessorExceptionTest {
    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationHandlerImplTest.class);
    private static final String SECRET = "secret123";
    private static final String ENCRYPTED_SECRET = "FCW2hghkImChSYMNlYY0cA==";

    @BeforeClass
    public void before() {
        System.clearProperty(AuthConstants.WSO2_RUNTIME_PATH);
    }

    @Test
    public void testGetPreprocessedClientSecretWithoutSettingConfigsProperly() throws Exception {
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
        EncryptionDecryptionPersistenceProcessor processor = new EncryptionDecryptionPersistenceProcessor();
        try {
            processor.getProcessedClientSecret(SECRET);
            Assert.fail();
        } catch (PersistenceProcessorException e) {
            log.debug("Expected error while encryption - " + SECRET, e);
        }
    }

}
