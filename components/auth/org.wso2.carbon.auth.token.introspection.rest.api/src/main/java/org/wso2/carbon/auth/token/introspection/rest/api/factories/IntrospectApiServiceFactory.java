package org.wso2.carbon.auth.token.introspection.rest.api.factories;

import org.wso2.carbon.auth.token.introspection.rest.api.IntrospectApiService;
import org.wso2.carbon.auth.token.introspection.rest.api.impl.IntrospectApiServiceImpl;

public class IntrospectApiServiceFactory {
    private static final IntrospectApiService service = new IntrospectApiServiceImpl();

    public static IntrospectApiService getIntrospectApi() {
        return service;
    }
}
