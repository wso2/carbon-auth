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

package org.wso2.carbon.auth.core.encryption;

import org.wso2.carbon.auth.core.CryptoConstants;
import org.wso2.carbon.auth.core.exception.CryptoException;
import org.wso2.carbon.secvault.SecureVault;
import org.wso2.carbon.secvault.SecureVaultFactory;
import org.wso2.carbon.secvault.exception.SecureVaultException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used to handle symmetric encryption related tasks
 */
public class SymmetricEncryption {

    private static SymmetricEncryption instance = null;

    private SecretKey symmetricKey = null;
    private boolean isSymmetricKeyFromFile = false;
    private static String symmetricKeyEncryptAlgoDefault = "AES";
    private String propertyKey = "symmetric.key";

    public static synchronized SymmetricEncryption getInstance() {
        if (instance == null) {
            instance = new SymmetricEncryption();
        }
        return instance;
    }

    public void generateSymmetricKey() throws CryptoException {

        String secretAlias;
        String encryptionAlgo = null;
        Properties properties;

        try {

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(CryptoConstants.SYMMETRIC_KEY_PROPERTIES_FILE_NAME).getFile());
            if (file.exists()) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    properties = new Properties();
                    properties.load(fileInputStream);
                }

                Path configPath = Paths.get("resources", CryptoConstants.SECURE_VAULT_CONFIG_YAML_FILE_NAME);

                SecureVault secureVault = new SecureVaultFactory().getSecureVault(configPath)
                        .orElseThrow(() -> new SecureVaultException("Error in getting secure vault instance"));

                secretAlias = properties.getProperty(propertyKey);
                encryptionAlgo = symmetricKeyEncryptAlgoDefault;

                symmetricKey = new SecretKeySpec(new String(secureVault.resolve(secretAlias)).getBytes(
                        Charset.defaultCharset()), 0,
                        secureVault.resolve(secretAlias).length, encryptionAlgo);
                isSymmetricKeyFromFile = true;
            }

            if (!isSymmetricKeyFromFile) {
                throw new CryptoException("Error in generating symmetric key. Symmetric key is not available.");
            }
        } catch (IOException e) {
            throw new CryptoException("Error in generating symmetric key", e);
        } catch (SecureVaultException e) {
            throw new CryptoException("Error in getting secure vault instance", e);
        }
    }

    public byte[] encryptWithSymmetricKey(byte[] plainText) throws CryptoException {
        Cipher c = null;
        byte[] encryptedData = null;
        String encryptionAlgo;
        try {
            encryptionAlgo = symmetricKeyEncryptAlgoDefault;
            c = Cipher.getInstance(encryptionAlgo);
            c.init(Cipher.ENCRYPT_MODE, symmetricKey);
            encryptedData = c.doFinal(plainText);
        } catch (Exception e) {
            throw new CryptoException("Error when encrypting data.", e);
        }
        return encryptedData;
    }

    public byte[] decryptWithSymmetricKey(byte[] encryptionBytes) throws CryptoException {
        Cipher c = null;
        byte[] decryptedData = null;
        String encryptionAlgo;
        try {
            encryptionAlgo = symmetricKeyEncryptAlgoDefault;
            c = Cipher.getInstance(encryptionAlgo);
            c.init(Cipher.DECRYPT_MODE, symmetricKey);
            decryptedData = c.doFinal(encryptionBytes);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException |
                NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new CryptoException("Error when decrypting data.", e);
        }
        return decryptedData;
    }

}
