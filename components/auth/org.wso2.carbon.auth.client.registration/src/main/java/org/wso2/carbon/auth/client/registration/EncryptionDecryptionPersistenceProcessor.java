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

import org.apache.commons.io.Charsets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.auth.client.registration.exception.PersistenceProcessorException;
import org.wso2.carbon.auth.core.exception.CryptoException;
import org.wso2.carbon.auth.core.util.CryptoUtil;

/**
 * This class is used when storing encrypted tokens.
 */
public class EncryptionDecryptionPersistenceProcessor {

    protected Log log = LogFactory.getLog(EncryptionDecryptionPersistenceProcessor.class);

    public String getPreprocessedClientSecret(String processedClientSecret) throws PersistenceProcessorException {
        try {
            return decrypt(processedClientSecret);
        } catch (CryptoException e) {
            throw new PersistenceProcessorException("Error while retrieving preprocessed client secret", e);
        }
    }

    public String getProcessedClientSecret(String clientSecret) throws PersistenceProcessorException {
        try {
            return encrypt(clientSecret);
        } catch (CryptoException e) {
            throw new PersistenceProcessorException("Error while retrieving processed client secret", e);
        }
    }

    private String encrypt(String plainText) throws CryptoException {
        return  CryptoUtil.getDefaultCryptoUtil().encryptAndBase64Encode(
                plainText.getBytes(Charsets.UTF_8));
    }

    private String decrypt(String cipherText) throws CryptoException {
        return new String(CryptoUtil.getDefaultCryptoUtil().base64DecodeAndDecrypt(
                cipherText), Charsets.UTF_8);
    }
}
