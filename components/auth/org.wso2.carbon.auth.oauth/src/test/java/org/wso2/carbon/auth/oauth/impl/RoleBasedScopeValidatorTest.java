/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.Scope;
import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.callback.ScopeValidatorCallback;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.exception.OAuthScopeException;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManager;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleBasedScopeValidatorTest {

    @Test
    public void testRoleToScopeValidationScenario1() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        List<String> bindings = Arrays.asList("admin", "provider");
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(bindings);
        org.wso2.carbon.auth.scope.registration.dto.Scope create = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        create.setBindings(bindings);
        org.wso2.carbon.auth.scope.registration.dto.Scope delete = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        delete.setBindings(bindings);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("admin");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create", "apim:api_delete"));
        Mockito.when(userStoreManage.getRoleListOfUser("admin")).thenReturn(Arrays.asList("admin"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(create);
        Mockito.when(scopeManager.getScope("apim:api_delete")).thenReturn(delete);
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 3);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_create"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_delete"));
    }

    @Test
    public void testRoleToScopeValidationScenario2() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        List<String> bindings = Arrays.asList("provider");
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(bindings);
        org.wso2.carbon.auth.scope.registration.dto.Scope create = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        create.setBindings(bindings);
        org.wso2.carbon.auth.scope.registration.dto.Scope delete = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        delete.setBindings(bindings);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("admin");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create", "apim:api_delete"));
        Mockito.when(userStoreManage.getRoleListOfUser("admin")).thenReturn(Arrays.asList("admin"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(create);
        Mockito.when(scopeManager.getScope("apim:api_delete")).thenReturn(delete);
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 1);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains(OAuthConstants
                .SCOPE_DEFAULT));
    }

    @Test
    public void testRoleToScopeValidationScenario3() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        List<String> binding1 = Arrays.asList("creator");
        List<String> binding2 = Arrays.asList("admin");
        List<String> binding3 = Collections.emptyList();
        org.wso2.carbon.auth.scope.registration.dto.Scope create = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        create.setBindings(binding1);
        org.wso2.carbon.auth.scope.registration.dto.Scope delete = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        delete.setBindings(binding2);
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(binding3);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("admin");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create", "apim:api_delete"));
        Mockito.when(userStoreManage.getRoleListOfUser("admin")).thenReturn(Arrays.asList("admin"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(create);
        Mockito.when(scopeManager.getScope("apim:api_delete")).thenReturn(delete);
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 2);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_delete"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
    }

    @Test
    public void testRoleToScopeValidationScenario4() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        List<String> binding1 = Arrays.asList("creator", "provider", "admin");
        List<String> binding2 = Arrays.asList("creator", "admin");
        List<String> binding3 = Arrays.asList("provider", "admin");
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(binding1);
        org.wso2.carbon.auth.scope.registration.dto.Scope create = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        create.setBindings(binding2);
        org.wso2.carbon.auth.scope.registration.dto.Scope delete = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        delete.setBindings(binding3);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();

        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("creator");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create", "apim:api_delete"));
        Mockito.when(userStoreManage.getRoleListOfUser("creator")).thenReturn(Arrays.asList("creator"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(create);
        Mockito.when(scopeManager.getScope("apim:api_delete")).thenReturn(delete);
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 2);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_create"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
    }

    @Test
    public void testRoleToScopeValidationScenario5() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        List<String> binding1 = Arrays.asList("creator", "provider", "admin");
        List<String> binding2 = Arrays.asList("creator", "admin");
        List<String> binding3 = Arrays.asList("provider", "admin");
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(binding1);
        org.wso2.carbon.auth.scope.registration.dto.Scope create = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        create.setBindings(binding2);
        org.wso2.carbon.auth.scope.registration.dto.Scope delete = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        delete.setBindings(binding3);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create", "apim:api_delete"));
        Mockito.when(userStoreManage.getRoleListOfUser("publisher")).thenReturn(Arrays.asList("provider"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(create);
        Mockito.when(scopeManager.getScope("apim:api_delete")).thenReturn(delete);
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 2);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_delete"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
    }

    @Test
    public void testRoleToScopeValidationScenario6() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        List<String> binding1 = Arrays.asList("creator", "provider", "admin");
        List<String> binding2 = Arrays.asList("creator", "admin");
        List<String> binding3 = Arrays.asList("provider", "admin");
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(binding1);
        org.wso2.carbon.auth.scope.registration.dto.Scope create = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        create.setBindings(binding2);
        org.wso2.carbon.auth.scope.registration.dto.Scope delete = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        delete.setBindings(binding3);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create", "apim:api_delete"));
        Mockito.when(userStoreManage.getRoleListOfUser("publisher")).thenReturn(Arrays.asList("creator",
                "provider"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(create);
        Mockito.when(scopeManager.getScope("apim:api_delete")).thenReturn(delete);
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.times(1)).getRoleListOfUser("publisher");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 3);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_delete"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_create"));
    }

    @Test(description = "whiteListed Scopes")
    public void testRoleToScopeValidationScenario7() throws OAuthScopeException, UserStoreException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("device_a", "device_b"));
        Mockito.when(userStoreManage.getRoleListOfUser("publisher")).thenReturn(Arrays.asList("creator",
                "provider"));
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.never()).getRoleListOfUser("publisher");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 2);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("device_a"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("device_b"));
    }

    @Test(description = "whiteListed Scope + scope without role mapping")
    public void testRoleToScopeValidationScenario8() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(new org.wso2.carbon.auth.scope.registration
                .dto.Scope());
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("device_a", "apim:api_view"));
        Mockito.when(userStoreManage.getRoleListOfUser("publisher")).thenReturn(Arrays.asList("creator",
                "provider"));
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.never()).getRoleListOfUser("publisher");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 2);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("device_a"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
    }

    @Test(description = "oidc scopes validation")
    public void testRoleToScopeValidationScenario9() throws OAuthScopeException, UserStoreException, ScopeDAOException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(new org.wso2.carbon.auth.scope.registration
                .dto.Scope());
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("device_a", "apim:api_view", "openid"));
        Mockito.when(userStoreManage.getRoleListOfUser("publisher")).thenReturn(Arrays.asList("creator",
                "provider"));
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.never()).getRoleListOfUser("publisher");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 3);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("device_a"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("openid"));

    }

    @Test(description = "default scope")
    public void testRoleToScopeValidationScenario10() throws OAuthScopeException, UserStoreException,
            ScopeDAOException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(new org.wso2.carbon.auth.scope.registration
                .dto.Scope());
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("default"));
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.never()).getRoleListOfUser("publisher");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 1);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("default"));

    }

    @Test(description = " file base scopes")
    public void testRoleToScopeValidationScenario12() throws OAuthScopeException, UserStoreException,
            ScopeDAOException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        Mockito.when(userStoreManage.getRoleListOfUser("admin")).thenReturn(Collections.singletonList("admin"));
        Map<String, List<String>> fileBaseScopeMap = new HashMap<>();
        fileBaseScopeMap.put("apim:api_create", Collections.singletonList("admin"));
        fileBaseScopeMap.put("apim:api_view", Collections.emptyList());
        oAuthConfiguration.setFileBaseScopes(fileBaseScopeMap);
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("admin");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_create", "apim:api_view"));
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.times(1)).getRoleListOfUser("admin");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 2);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_create"));
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));
    }

    @Test(description = "default scope")
    public void testRoleToScopeValidationScenario11() throws OAuthScopeException, UserStoreException,
            ScopeDAOException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(new org.wso2.carbon.auth.scope.registration
                .dto.Scope());
        Mockito.when(scopeManager.getScope("apim:api_create")).thenReturn(null);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view", "apim:api_create"));
        roleBasedScopeValidator.process(scopeValidatorCallback);
        Mockito.verify(userStoreManage, Mockito.never()).getRoleListOfUser("publisher");
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().size() == 1);
        Assert.assertTrue(scopeValidatorCallback.getApprovedScope().toStringList().contains("apim:api_view"));

    }

    @Test(description = "Can't retrieve Scope information")
    public void testRoleToScopeValidationScenarioNegative1() throws UserStoreException, ScopeDAOException {

        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("admin");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view"));
        Mockito.when(userStoreManage.getRoleListOfUser("admin")).thenReturn(Arrays.asList("admin"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenThrow(new ScopeDAOException("Error while retrieving " +
                "Scope information"));
        try {
            roleBasedScopeValidator.process(scopeValidatorCallback);
            Assert.fail();
        } catch (OAuthScopeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Error while retrieving Scope Information"));
        }
    }

    @Test
    public void testRoleToScopeValidationScenarioNegative2() throws UserStoreException,
            ScopeDAOException {

        List<String> binding1 = Arrays.asList("creator", "provider", "admin");
        org.wso2.carbon.auth.scope.registration.dto.Scope view = new org.wso2.carbon.auth.scope.registration.dto
                .Scope();
        view.setBindings(binding1);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        UserStoreManager userStoreManage = Mockito.mock(UserStoreManager.class);
        OAuthConfiguration oAuthConfiguration = new OAuthConfiguration();
        RoleBasedScopeValidator roleBasedScopeValidator = new RoleBasedScopeValidator(scopeManager, userStoreManage,
                oAuthConfiguration);
        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        scopeValidatorCallback.setAuthUser("publisher");
        scopeValidatorCallback.setRequestedScopes(new Scope("apim:api_view"));
        Mockito.when(userStoreManage.getRoleListOfUser("publisher")).thenThrow(new UserStoreException("Error while " +
                "retrieving roles of user"));
        Mockito.when(scopeManager.getScope("apim:api_view")).thenReturn(view);
        try {
            roleBasedScopeValidator.process(scopeValidatorCallback);
            Assert.fail();
        } catch (OAuthScopeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Error while retrieving user roles"));
        }
    }
}
