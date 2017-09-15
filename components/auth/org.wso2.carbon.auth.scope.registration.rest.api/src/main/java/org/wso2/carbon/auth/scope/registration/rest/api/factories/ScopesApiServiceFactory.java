package org.wso2.carbon.auth.scope.registration.rest.api.factories;

import org.wso2.carbon.auth.scope.registration.rest.api.ScopesApiService;
import org.wso2.carbon.auth.scope.registration.rest.api.impl.ScopesApiServiceImpl;

public class ScopesApiServiceFactory {
    private static final ScopesApiService service = new ScopesApiServiceImpl();

    public static ScopesApiService getScopesApi() {
        return service;
    }
}
