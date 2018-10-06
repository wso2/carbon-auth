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
package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.ScopeValidator;
import org.wso2.carbon.auth.oauth.callback.ScopeValidatorCallback;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.exception.OAuthScopeException;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dao.impl.DAOFactory;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManager;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManagerImpl;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.mgt.UserStoreManagerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default implemented role based scope validator
 */
public class RoleBasedScopeValidator implements ScopeValidator {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedScopeValidator.class);

    private ScopeManager scopeManager;
    private UserStoreManager userStoreManager;
    private OAuthConfiguration oAuthConfiguration;

    protected RoleBasedScopeValidator(ScopeManager scopeManager, UserStoreManager userStoreManager,
                                      OAuthConfiguration oAuthConfiguration) {

        this.scopeManager = scopeManager;
        this.userStoreManager = userStoreManager;
        this.oAuthConfiguration = oAuthConfiguration;
    }

    public RoleBasedScopeValidator() throws OAuthScopeException {

        oAuthConfiguration = ServiceReferenceHolder.getInstance().getAuthConfigurations();

        try {
            ScopeDAO scopeDAO = DAOFactory.getScopeDAO();
            scopeManager = new ScopeManagerImpl(scopeDAO);
        } catch (ScopeDAOException e) {
            log.error("Error while retrieving Data Access for Scopes", e);
            throw new OAuthScopeException("Error while Initializing RoleBasedScopeValidator");
        }
        try {
            userStoreManager = UserStoreManagerFactory.getUserStoreManager();
        } catch (UserStoreException e) {
            throw new OAuthScopeException("User manager initialization failed");
        }
    }

    @Override
    public void process(ScopeValidatorCallback callback) throws OAuthScopeException {

        Scope requestedScopes = callback.getRequestedScopes();
        String username = callback.getAuthUser();
        List<String> rolesList = null;
        Scope approvedScopes = new Scope();
        List<String> whiteListedScopes = oAuthConfiguration.getWhiteListedScopes();
        Map<String, List<String>> fileBaseScopes = oAuthConfiguration.getFileBaseScopes();
        List<String> scopeList = requestedScopes.toStringList();
        Iterator<String> scopeIterator = scopeList.iterator();
        while (scopeIterator.hasNext()) {
            String scope = scopeIterator.next();
            for (String whiteListedScope : whiteListedScopes) {
                if (scope.matches(whiteListedScope)) {
                    approvedScopes.add(scope);
                    scopeIterator.remove();
                    break;
                }
            }
            for (String oidcScope : oAuthConfiguration.getOidcScopes()) {
                if (oidcScope.equals(scope)) {
                    approvedScopes.add(scope);
                    scopeIterator.remove();
                    break;
                }
            }
        }
        for (String scopeName : scopeList) {
            if (OAuthConstants.SCOPE_DEFAULT.equals(scopeName)) {
                approvedScopes.add(OAuthConstants.SCOPE_DEFAULT);
            } else {
                if (fileBaseScopes.containsKey(scopeName)) {
                    List<String> fileBaseRoleList = fileBaseScopes.get(scopeName);
                    if (fileBaseRoleList.isEmpty()) {
                        approvedScopes.add(scopeName);
                    } else {
                        for (String role : fileBaseRoleList) {
                            rolesList = initializeRolesList(rolesList, username);
                            if (rolesList.contains(role)) {
                                approvedScopes.add(scopeName);
                                break;
                            }
                        }
                    }
                } else {
                    try {
                        org.wso2.carbon.auth.scope.registration.dto.Scope scope = scopeManager.getScope(scopeName);
                        if (scope != null) {
                            if (scope.getBindings().isEmpty()) {
                                approvedScopes.add(scopeName);
                            } else {
                                rolesList = initializeRolesList(rolesList, username);
                                for (String binding : scope.getBindings()) {
                                    if (rolesList.contains(binding)) {
                                        approvedScopes.add(scopeName);
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (ScopeDAOException e) {
                        throw new OAuthScopeException("Error while retrieving Scope Information", e);
                    }
                }
            }
        }
        if (approvedScopes.toStringList().isEmpty()) {
            approvedScopes.add(OAuthConstants.SCOPE_DEFAULT);
        }
        callback.setApprovedScope(approvedScopes);
    }

    private List<String> initializeRolesList(List<String> rolesList, String username) throws OAuthScopeException {

        if (rolesList == null) {
            try {
                return userStoreManager.getRoleListOfUser(username);
            } catch (UserStoreException e) {
                throw new OAuthScopeException("Error while retrieving user roles", e);
            }
        }
        return rolesList;
    }
}
