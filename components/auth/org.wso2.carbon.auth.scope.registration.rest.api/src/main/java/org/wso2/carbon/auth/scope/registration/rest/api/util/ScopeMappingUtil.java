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

package org.wso2.carbon.auth.scope.registration.rest.api.util;

import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;

/**
 * Utility class for mapping Scope implementation core models to REST API DTOs and vise versa.
 */
public class ScopeMappingUtil {

    /**
     * This method convert the scope model object into DTO
     *
     * @param scope model object
     * @return ScopeDTO DTO object representing the model object
     */
    public static ScopeDTO scopeModelToDTO(Scope scope) {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName(scope.getName());
        scopeDTO.setDescription(scope.getDescription());
        scopeDTO.setBindings(scope.getBindings());
        return scopeDTO;
    }

    /**
     * This method convert the scope DTO to scope model object
     *
     * @param scopeDTO Scope DTO object
     * @return Scope model object representing the scope DTO object
     */
    public static Scope scopeDTOToModel(ScopeDTO scopeDTO) {
        Scope scope = new Scope();
        scope.setName(scopeDTO.getName());
        scope.setDisplayName(scopeDTO.getName());
        scope.setDescription(scopeDTO.getDescription());
        scope.setBindings(scopeDTO.getBindings());
        return scope;
    }
}
