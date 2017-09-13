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

import java.util.UUID;

/**
 * Identity Management Util.
 */
public class UserStoreUtil {

    private UserStoreUtil() {

    }

    /**
     * Generate UUID.
     *
     * @return UUID as a string.
     */
    public static String generateUUID() {

        String random = UUID.randomUUID().toString();
        random = random.replace("/", "_");
        random = random.replace("=", "a");
        random = random.replace("+", "f");
        return random;
    }

}
