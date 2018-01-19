/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.scope.registration.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.scope.registration.dao.impl.DAOFactory;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManagerImpl;

import java.util.ArrayList;
import java.util.List;


public class ScopeDAOIntegrationTest extends ScopeDAOIntegrationTestBase {

    private ScopeDAO scopeDAO;
    private ScopeManagerImpl scopeManager;
    private static final Logger log = LoggerFactory.getLogger(ScopeDAOIntegrationTest.class);

    public ScopeDAOIntegrationTest() {
    }

    @BeforeClass
    public void init() throws Exception {
        super.init();
        log.info("Data sources initialized");
    }

    @BeforeMethod
    public void setup() throws Exception {
        super.setup();
        log.info("Created databases");
        scopeDAO = DAOFactory.getScopeDAO();
        scopeManager = new ScopeManagerImpl(scopeDAO);
        Assert.assertTrue(DAOUtil.isAutoCommitAuth(), "Autocommit set to true in auth DB but was false");
    }

    @AfterClass
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }

    @Test
    public void testAddScopeWithoutBinding() throws Exception {
        Scope scope = new Scope("test_scope_name_without_binding", "test_scope_description",
                "test_scope_display_name");
        Assert.assertNotNull(scope.getBindings());
        scopeDAO.addScope(scope);
        scopeDAO.updateScopeByName(scope);
        //Test scope with binding is there in data store.
        Assert.assertNotNull(scopeDAO.getScopesWithPagination(0, 1));
        scopeDAO.deleteScopeByName(scope.getName());
    }

    @Test
    public void testAddAndGetScope() throws Exception {
        List<String> bindings = new ArrayList<>();
        bindings.add("test");
        Scope scope = new Scope();
        Assert.assertNotNull(scope.getBindings());
        scope.setBindings(bindings);
        scope.addBinding("test_binding");
        scope.setName("test_scope_name");
        scope.setDescription("test_scope_description");
        scope.setDisplayName("test_scope_display_name");
        //Add Scope with all attributes
        scopeManager.registerScope(scope);
        Scope savedScope = scopeManager.getScope("test_scope_name");
        //Check added scope is there in data store
        Assert.assertNotNull(savedScope);
        //Test attributes of saved scope against created name
        Assert.assertEquals("test_scope_description", savedScope.getDescription());
        //Test is scope exists functionality
        Assert.assertTrue(scopeManager.isScopeExists("test_scope_name"));
        scopeManager.deleteScope(scope.getName());
        //Check generated scope string is not null
        Assert.assertNotNull(scope.toString());
    }

    @Test
    public void testAddAndGetForNotExistingScope() throws Exception {
        List<String> bindings = new ArrayList<>();
        bindings.add("test");
        Scope scope = new Scope();
        scope.setBindings(bindings);
        scope.addBinding("test_binding");
        scope.setName("test_scope_name_test");
        scope.setDescription("test_scope_description");
        scope.setDisplayName("test_scope_display_name");
        scopeDAO.addScope(scope);
        scopeDAO.getScopesWithPagination(0, 1);
        //Check non existing scope is there in data store by giving name
        Assert.assertNull(scopeDAO.getScopeByName("not_existing_name"));
        Assert.assertFalse(scopeDAO.isScopeExists("not_existing_name"));
        scopeDAO.deleteScopeByName("not_existing_name");
    }

    @Test
    public void testDatabaseExceptions() throws Exception {
        Scope scope = new Scope();
        scope.setName("test_scope_name");
        scope.setDescription("test_scope_description");
        scope.setDisplayName("test_scope_display_name");
        //Delete table and check for database exceptions
        executeOnAuthDb("DROP TABLE AUTH_OAUTH2_SCOPE;");
        try {
            //Check added scope is there in data store after table deletion
            scopeDAO.getScopeByName("test_scope_name");
            Assert.fail("Exception not thrown for SQL Exception while getting Scope");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
        try {
            scopeDAO.isScopeExists("test_scope_name");
            Assert.fail("Exception not thrown for SQL Exception while checking Scope");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
        try {
            scopeDAO.getScopesWithPagination(0, 1);
            Assert.fail("Exception not thrown for SQL Exception while getting scopes with pagination");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
        try {
            scopeDAO.updateScopeByName(scope);
            Assert.fail("Exception not thrown for SQL Exception while update scope by name");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
        try {
            scopeDAO.deleteScopeByName(null);
            Assert.fail("Exception not thrown for SQL Exception while delete scope by name");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
        try {
            scopeDAO.addScope(scope);
            Assert.fail("Exception not thrown for SQL Exception while adding scope");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
        try {
            scopeDAO.addScope(null);
            Assert.fail("Exception not thrown for adding null scope");
        } catch (ScopeDAOException e) {
            // Just catch the exception so that we can continue execution
        }
    }

}
