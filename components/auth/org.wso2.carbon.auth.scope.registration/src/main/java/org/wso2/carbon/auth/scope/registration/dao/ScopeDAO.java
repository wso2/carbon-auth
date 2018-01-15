/*
 * Copyright (c) 2018, WSO2 Inc. http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.scope.registration.dao;

import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;

import java.util.Set;

/**
 * 
 * 
 */
public interface ScopeDAO {
    void addScope(Scope scope) throws ScopeDAOException;
    Set<Scope> getAllScopes(int tenantID) throws ScopeDAOException;
    Set<Scope> getScopesWithPagination(Integer offset, Integer limit, int tenantID) throws ScopeDAOException;
    Scope getScopeByName(String name) throws ScopeDAOException;
    boolean isScopeExists(String scopeName) throws ScopeDAOException;
    int getScopeIDByName(String scopeName) throws ScopeDAOException;
    void deleteScopeByName(String name, int tenantID) throws ScopeDAOException;
    void updateScopeByName(Scope updatedScope, int tenantID) throws ScopeDAOException;
}
