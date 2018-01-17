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

import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory ScopeDAO object used for mocking scope DAO layer
 * 
 */
public class ScopeTestDAO implements ScopeDAO {

    private List<Scope> scopes;

    public ScopeTestDAO () {
        scopes = new ArrayList<>();
    }

    @Override
    public void addScope(Scope scope) throws ScopeDAOException {
        scopes.add(scope);
    }

    @Override
    public List<Scope> getScopesWithPagination(Integer offset, Integer limit) throws ScopeDAOException {
        if (Integer.MAX_VALUE == limit) {
            return scopes;
        }
        return scopes.subList(offset, offset + limit);
    }

    @Override
    public Scope getScopeByName(String name) throws ScopeDAOException {
        for (Scope scope: scopes) {
            if (scope.getName().equals(name)) {
                return scope;
            }
        }
        return null;
    }

    @Override
    public boolean isScopeExists(String scopeName) throws ScopeDAOException {
        return getScopeByName(scopeName) != null;
    }

    @Override
    public void deleteScopeByName(String name) throws ScopeDAOException {
        for (Scope scope: scopes) {
            if (scope.getName().equals(name)) {
                scopes.remove(scope);
                return;
            }
        }
    }

    @Override
    public void updateScopeByName(Scope updatedScope) throws ScopeDAOException {
        for (Scope scope: scopes) {
            if (scope.getName().equals(updatedScope.getName())) {
                scopes.remove(scope);
                scopes.add(updatedScope);
                return;
            }
        }
    }
}
