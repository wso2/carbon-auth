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

package org.wso2.carbon.auth.scope.registration.rest.api;

import org.mockito.Mockito;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManagerImpl;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.impl.ScopesApiServiceImpl;
import org.wso2.msf4j.Request;

import java.util.ArrayList;

/**
 * Utility class used for generating test objects
 */
public class ScopeTestObjectCreator {
    public static final String SCOPE_NAME_1 = "scope1";
    public static final String SCOPE_NAME_2 = "scope2";
    public static final String SCOPE_NAME_3 = "scope3";

    /**
     * Creates a mocked Request object.
     *
     * @return mocked Request object
     */
    public static Request getNewMockedRequest() {
        return Mockito.mock(Request.class);
    }

    /**
     * Create a ScopesApiServiceImpl based on an in memory DAO
     *
     * @return a ScopesApiServiceImpl based on an in memory DAO
     */
    public static ScopesApiServiceImpl getNewScopesApiServiceImpl() {
        return new ScopesApiServiceImpl(new ScopeManagerImpl(new ScopeTestDAO()));
    }

    /**
     * Create a ScopesApiServiceImpl based on an in erroneous memory DAO
     *
     * @return a ScopesApiServiceImpl based on an in erroneous memory DAO
     */
    public static ScopesApiServiceImpl getNewErroneousScopesApiServiceImpl() {
        return new ScopesApiServiceImpl(new ScopeManagerImpl(new ScopeTestExceptionDAO()));
    }

    /**
     * Creates a scope DTO
     *
     * @return a ScopeDTO based on the name
     */
    public static ScopeDTO createScopeDTO(String name) {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName(name);
        scopeDTO.setDescription(name + "-description");
        scopeDTO.setBindings(new ArrayList<String>() {
            {
                for (int i = 0; i < name.length(); i++) {
                    add("role" + i);
                }
            }
        });
        return scopeDTO;
    }
}
