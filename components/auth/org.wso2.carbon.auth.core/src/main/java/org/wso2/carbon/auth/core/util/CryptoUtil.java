/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.core.util;

import org.apache.axiom.om.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.auth.core.encryption.SymmetricEncryption;
import org.wso2.carbon.auth.core.exception.CryptoException;

/**
 * The utility class to encrypt/decrypt passwords to be stored in the
 * database.
 */
public class CryptoUtil {
    private static Log log = LogFactory.getLog(CryptoUtil.class);

    private static CryptoUtil instance = null;

    /**
     * This method is used to get the CryptoUtil object. This approach must be used if the CryptoUtil class is used
     * in the server startup,where the ServerConfigurationService may not be available at CarbonCoreDataHolder.
     *
     * @return The created or cached CryptoUtil instance
     */
    public static synchronized CryptoUtil getDefaultCryptoUtil() {
        if (instance == null) {
            instance = new CryptoUtil();
        }
        return instance;
    }

    /**
     * Encrypt a given plain text
     *
     * @param plainTextBytes The plaintext bytes to be encrypted
     * @return The cipher text bytes
     * @throws CryptoException On error during encryption
     */
    public byte[] encrypt(byte[] plainTextBytes) throws CryptoException {

        byte[] encryptedKey;
        SymmetricEncryption encryption = SymmetricEncryption.getInstance();

        try {
            encryption.generateSymmetricKey();
            encryptedKey = encryption.encryptWithSymmetricKey(plainTextBytes);
        } catch (Exception e) {
            throw new
                    CryptoException("Error during encryption", e);
        }
        return encryptedKey;
    }

    /**
     * Encrypt the given plain text and base64 encode the encrypted content.
     *
     * @param plainText The plaintext value to be encrypted and base64
     *                  encoded
     * @return The base64 encoded cipher text
     * @throws CryptoException On error during encryption
     */
    public String encryptAndBase64Encode(byte[] plainText) throws
            CryptoException {
        return Base64.encode(encrypt(plainText));
    }

    /**
     * Decrypt the given cipher text value using the WSO2 WSAS key
     *
     * @param cipherTextBytes The cipher text to be decrypted
     * @return Decrypted bytes
     * @throws CryptoException On an error during decryption
     */
    public byte[] decrypt(byte[] cipherTextBytes) throws CryptoException {

        byte[] decyptedValue;
        SymmetricEncryption encryption = SymmetricEncryption.getInstance();

        try {
            encryption.generateSymmetricKey();
            decyptedValue = encryption.decryptWithSymmetricKey(cipherTextBytes);
        } catch (Exception e) {
            throw new CryptoException("Error during decryption", e);
        }
        return decyptedValue;
    }

    /**
     * Base64 decode the given value and decrypt using the WSO2 WSAS key
     *
     * @param base64CipherText Base64 encoded cipher text
     * @return Base64 decoded, decrypted bytes
     * @throws CryptoException On an error during decryption
     */
    public byte[] base64DecodeAndDecrypt(String base64CipherText) throws
            CryptoException {
        return decrypt(Base64.decode(base64CipherText));
    }
}
