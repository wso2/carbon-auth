/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.auth.user.store.util;

import java.security.NoSuchAlgorithmException;

/**
 * Plug-able password handler.
 */
public interface PasswordHandler {

    /**
     * Hash the given password using given algorithm.
     *
     * @param password Password to be hashed.
     * @param salt     Salt to be used to hash the password.
     * @param hashAlgo Hashing algorithm to be used. (SHA1, SHA256, SHA512, etc..)
     * @return Hash as a <code>String</code>
     * @throws NoSuchAlgorithmException No such algorithm exception.
     */
    String hashPassword(char[] password, String salt, String hashAlgo) throws NoSuchAlgorithmException;

    /**
     * Set iteration count for the hash.
     *
     * @param iterationCount Iteration count.
     */
    void setIterationCount(int iterationCount);

    /**
     * Key length of the hash.
     *
     * @param keyLength Key length.
     */
    void setKeyLength(int keyLength);
}
