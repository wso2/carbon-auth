package org.wso2.carbon.auth.scope.registration.rest.api.factories;

import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dao.impl.DAOFactory;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;
import org.wso2.carbon.auth.scope.registration.impl.ScopeManagerImpl;
import org.wso2.carbon.auth.scope.registration.rest.api.ScopesApiService;
import org.wso2.carbon.auth.scope.registration.rest.api.impl.ScopesApiServiceImpl;

public class ScopesApiServiceFactory {

    public static ScopesApiService getScopesApi() {
        ScopeDAO scopeDAO;
        try {
            scopeDAO = DAOFactory.getScopeDAO();
        } catch (ScopeDAOException e) {
            throw new IllegalStateException("Could not create ScopesApiService", e);
        }
        return new ScopesApiServiceImpl(new ScopeManagerImpl(scopeDAO));
    }
}
