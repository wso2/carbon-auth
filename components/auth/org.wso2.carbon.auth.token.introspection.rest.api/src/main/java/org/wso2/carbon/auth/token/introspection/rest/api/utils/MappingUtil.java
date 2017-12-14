/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.token.introspection.rest.api.utils;

import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.IntrospectionResponseDTO;

/**
 * Rest API mapping util
 */
public class MappingUtil {
    public static IntrospectionResponseDTO applicationModelToApplicationDTO(
            IntrospectionResponse introspectionResponse) {
        IntrospectionResponseDTO introspectionResponseDTO = new IntrospectionResponseDTO();
        introspectionResponseDTO.setActive(introspectionResponse.isActive());
        introspectionResponseDTO.setClientId(introspectionResponse.getClientId());
        introspectionResponseDTO.setExp((int) introspectionResponse.getExp());
        introspectionResponseDTO.setIat((int) introspectionResponse.getIat());
        introspectionResponseDTO.setScope(introspectionResponse.getScope());
        introspectionResponseDTO.setTokenType(introspectionResponse.getTokenType());
        introspectionResponseDTO.setUsername(introspectionResponse.getUsername());

        return introspectionResponseDTO;
    }
}
