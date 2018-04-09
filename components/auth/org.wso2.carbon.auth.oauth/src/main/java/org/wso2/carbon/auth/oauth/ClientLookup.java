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

package org.wso2.carbon.auth.oauth;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;

import java.util.Map;

/**
 * Interface for looking up client information
 */
public interface ClientLookup {
    /**
     * Get Client Id based on authorization header
     *
     * @param authorization Authorization header
     * @param context       AccessTokenContext object that stores context information during request processing
     * @param haltExecution State variable indicating if an error has occurred which should halt further execution
     * @return Client id
     */
    String getClientId(String authorization, AccessTokenContext context, Map<String, String> queryParameters,
            MutableBoolean haltExecution);
}
