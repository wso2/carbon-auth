/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.user.info.exception;

import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.core.exception.ExceptionCodes;

/**
 * Class to be used for User Info related exceptions
 */
public class UserInfoException extends AuthException {

    public UserInfoException(String msg, ExceptionCodes code) {
        super(msg, code);
    }

    public UserInfoException(String msg, Throwable e, ExceptionCodes code) {
        super(msg, e, code);
    }

    public UserInfoException(String msg) {
        super(msg, ExceptionCodes.INVALID_REQUEST);
    }

    public UserInfoException(String msg, Throwable e) {
        super(msg, e, ExceptionCodes.INVALID_REQUEST);
    }

}
