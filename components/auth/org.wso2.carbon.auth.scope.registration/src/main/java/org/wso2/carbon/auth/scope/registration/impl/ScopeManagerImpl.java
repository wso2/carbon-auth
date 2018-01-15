/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.scope.registration.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;

import java.util.Set;

/**
 * Scope Manager class which handle all scope management tasks
 */
public class ScopeManagerImpl implements ScopeManager {
    private static final Logger log = LoggerFactory.getLogger(ScopeManagerImpl.class);
    private ScopeDAO scopeDAO;

    public ScopeManagerImpl(ScopeDAO scopeDAO) {
        this.scopeDAO = scopeDAO;
    }

    /**
     * Register a scope with the bindings
     *
     * @param scope details of the scope to be registered
     * @throws ScopeDAOException
     */
    @Override
    public Scope registerScope(Scope scope) throws ScopeDAOException {
        scopeDAO.addScope(scope);
        return scopeDAO.getScopeByName(scope.getName());
    }

    /**
     * Retrieve the available scope list
     *
     * @param startIndex Start Index of the result set to enforce pagination
     * @param count      Number of elements in the result set to enforce pagination
     * @return Scope list
     * @throws ScopeDAOException
     */
    @Override
    public Set<Scope> getScopes(Integer startIndex, Integer count)
            throws ScopeDAOException {
        return null;
    }

    /**
     * @param name Name of the scope which need to get retrieved
     * @return Retrieved Scope
     * @throws ScopeDAOException
     */
    @Override
    public Scope getScope(String name) throws ScopeDAOException {
        return scopeDAO.getScopeByName(name);
    }

    /**
     * Check the existence of a scope
     *
     * @param name Name of the scope
     * @return true if scope with the given scope name exists
     * @throws ScopeDAOException
     */
    @Override
    public boolean isScopeExists(String name) throws ScopeDAOException {
        return scopeDAO.isScopeExists(name);
    }

    /**
     * Delete the scope for the given scope ID
     *
     * @param name Scope ID of the scope which need to get deleted
     * @throws ScopeDAOException
     */
    @Override
    public void deleteScope(String name) throws ScopeDAOException {

    }

    /**
     * Update the scope of the given scope ID
     *
     * @param updatedScope details of updated scope
     * @return updated scope
     * @throws ScopeDAOException
     */
    @Override
    public Scope updateScope(Scope updatedScope) throws ScopeDAOException {
        return null;
    }

    /**
     * List scopes with filtering
     *
     * @param startIndex Start Index of the result set to enforce pagination
     * @param count      Number of elements in the result set to enforce pagination
     * @return List of available scopes
     * @throws ScopeDAOException
     */
    /*private Set<Scope> listScopesWithPagination(Integer startIndex, Integer count)
            throws ScopeDAOException {
        return null;
    }*/
}
