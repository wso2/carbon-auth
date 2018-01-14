package org.wso2.carbon.auth.scope.registration.rest.api.factories;

import org.wso2.carbon.auth.scope.registration.impl.ScopeManagerImpl;
import org.wso2.carbon.auth.scope.registration.rest.api.ScopesApiService;
import org.wso2.carbon.auth.scope.registration.rest.api.impl.ScopesApiServiceImpl;

public class ScopesApiServiceFactory {

    public static ScopesApiService getScopesApi() {
        return new ScopesApiServiceImpl(new ScopeManagerImpl());
    }
}
