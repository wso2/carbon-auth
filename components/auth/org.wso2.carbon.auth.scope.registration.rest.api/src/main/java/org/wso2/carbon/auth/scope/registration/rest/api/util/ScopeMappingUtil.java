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

import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.util.RestApiUtil;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.PaginationDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeListDTO;

import java.util.List;
import java.util.Map;

/**
 * Utility class for mapping Scope implementation core models to REST API DTOs and vise versa.
 */
public class ScopeMappingUtil {

    /**
     * This method convert the scope model object into DTO
     *
     * @param scope model object
     * @return ScopeDTO DTO object representing the DTO object
     */
    public static ScopeDTO scopeModelToDTO(Scope scope) {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName(scope.getName());
        scopeDTO.setDescription(scope.getDescription());
        scopeDTO.setBindings(scope.getBindings());
        return scopeDTO;
    }

    /**
     * This method convert the scope model object into DTO
     *
     * @param scopeList model object list
     * @param offset starting index
     * @param limit total scopes objects in the response array
     * @param total total scopes objects available in the system
     * @return ScopeListDTO DTO object representing a list of DTO objects
     */
    public static ScopeListDTO scopeModelListToListDTO(List<Scope> scopeList, Integer offset, Integer limit,
            Integer total) {
        ScopeListDTO scopeListDTO = new ScopeListDTO();
        for (Scope scope : scopeList) {
            ScopeDTO scopeDTO = scopeModelToDTO(scope);
            scopeListDTO.addListItem(scopeDTO);
        }
        scopeListDTO.setCount(scopeList.size());
        PaginationDTO paginationDTO = getScopePaginationDTO(offset, limit, total);
        scopeListDTO.setPagination(paginationDTO);
        return scopeListDTO;
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

    /**
     * Get a pagination DTO based on the input parameters
     * 
     * @param offset starting index
     * @param limit total objects in the response array
     * @param total total objects available in the system 
     * @return PaginationDTO based on the input parameters
     */
    private static PaginationDTO getScopePaginationDTO(Integer offset, Integer limit, Integer total) {
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setLimit(limit == null ? RestApiConstants.PAGINATION_LIMIT_DEFAULT : limit);
        paginationDTO.setOffset(offset == null ? RestApiConstants.PAGINATION_OFFSET_DEFAULT : offset);
        paginationDTO.setTotal(total);

        //acquiring pagination parameters and setting pagination urls
        Map<String, Integer> paginatedParams = RestApiUtil.getPaginationParams(offset, limit, total);
        String paginatedPrevious = "";
        String paginatedNext = "";

        if (paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) != null) {
            paginatedPrevious = RestApiUtil
                    .getScopePaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT));
        }

        if (paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET) != null) {
            paginatedNext = RestApiUtil
                    .getScopePaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_NEXT_LIMIT));
        }

        paginationDTO.setNext(paginatedNext);
        paginationDTO.setPrevious(paginatedPrevious);
        return paginationDTO;
    }
}
