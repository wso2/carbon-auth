/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.oauth.rest.api.internal;

import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

/**
 * Holder to keep UserStoreManager
 */
public class ServiceReferenceHolder {
    private UserStoreManager userStoreManager;
    private static ServiceReferenceHolder instance = new ServiceReferenceHolder();
    private UserNameMapper userNameMapper;
    public static ServiceReferenceHolder getInstance() {

        return instance;
    }

    private ServiceReferenceHolder() {

    }

    public UserStoreManager getUserStoreManager() {

        return userStoreManager;
    }

    public void setUserStoreManager(UserStoreManager userStoreManager) {

        this.userStoreManager = userStoreManager;
    }

    public UserNameMapper getUserNameMapper() {

        return userNameMapper;
    }

    public void setUserNameMapper(UserNameMapper userNameMapper) {

        this.userNameMapper = userNameMapper;
    }
}
