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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dto.Scope;

import java.util.ArrayList;
import java.util.List;

public class ScopeManagerImplTestCase {

    @Test
    public void registerScope() throws Exception {
        ScopeDAO scopeDAO = Mockito.mock(ScopeDAO.class);
        Scope scope = new Scope("scope_Name", "scope_display_name", "scope_description");
        Mockito.when(scopeDAO.getScopeByName(Mockito.anyString())).thenReturn(scope);
        ScopeManagerImpl scopeManager = new ScopeManagerImpl(scopeDAO);
        scopeManager.registerScope(scope);
        Assert.assertEquals(scopeManager.getScope("scope_Name").getName(), "scope_Name");
    }

    @Test
    public void getScope() throws Exception {
        ScopeDAO scopeDAO = Mockito.mock(ScopeDAO.class);
        Scope scope = new Scope("scope_Name", "scope_display_name", "scope_description");
        Mockito.when(scopeDAO.getScopeByName(Mockito.anyString())).thenReturn(scope);
        ScopeManagerImpl scopeManager = new ScopeManagerImpl(scopeDAO);
        Assert.assertNotNull(scopeManager.getScope("scope_Name").getName());
    }

    @Test
    public void updateScope() throws Exception {
        ScopeDAO scopeDAO = Mockito.mock(ScopeDAO.class);
        Scope scope = new Scope("scope_Name", "scope_display_name", "scope_description");
        Mockito.when(scopeDAO.getScopeByName(Mockito.anyString())).thenReturn(scope);
        ScopeManagerImpl scopeManager = new ScopeManagerImpl(scopeDAO);
        scopeManager.registerScope(scope);
        scopeManager.updateScope(scope);
        //Assert update here properly
        Assert.assertNotNull(scopeManager.getScope("scope_Name").getName());
    }

    @Test
    public void getScopes() throws Exception {
        ScopeDAO scopeDAO = Mockito.mock(ScopeDAO.class);
        Scope scope = new Scope("scope_Name", "scope_display_name", "scope_description");
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(scope);
        Mockito.when(scopeDAO.getScopesWithPagination(Mockito.anyInt(), Mockito.anyInt())).
                thenReturn(scopeList);
        ScopeManagerImpl scopeManager = new ScopeManagerImpl(scopeDAO);
        scopeManager.registerScope(scope);
        scopeManager.updateScope(scope);
        Assert.assertNotNull(scopeManager.getScopes(0, 2));
    }

    @Test
    public void isScopeExists() throws Exception {
        ScopeDAO scopeDAO = Mockito.mock(ScopeDAO.class);
        Scope scope = new Scope("scope_Name", "scope_display_name", "scope_description");
        Mockito.when(scopeDAO.getScopeByName(Mockito.anyString())).thenReturn(scope);
        ScopeManagerImpl scopeManager = new ScopeManagerImpl(scopeDAO);
        Assert.assertNotNull(scopeManager.isScopeExists("scope_Name"));
    }
}
