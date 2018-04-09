/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.user.info;

import org.wso2.carbon.auth.user.info.exception.UserInfoException;


/**
 * Handles OAuth2 token related functionality
 */
public interface UserinfoRequestHandler {

    /**
     * Retrieve user information for the user based on the OAuth2 Access token.
     *
     * @param authorization Authorization header
     * @return Access token
     * @throws UserInfoException if validation failed to retrieve token
     */
    String retrieveUserInfo(String authorization, String schema) throws UserInfoException;

}
