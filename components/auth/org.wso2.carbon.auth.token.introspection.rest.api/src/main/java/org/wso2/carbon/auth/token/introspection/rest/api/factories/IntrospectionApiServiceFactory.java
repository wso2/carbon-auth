package org.wso2.carbon.auth.token.introspection.rest.api.factories;

import org.wso2.carbon.auth.token.introspection.rest.api.IntrospectionApiService;
import org.wso2.carbon.auth.token.introspection.rest.api.impl.IntrospectionApiServiceImpl;

public class IntrospectionApiServiceFactory {
    private static final IntrospectionApiService service = new IntrospectionApiServiceImpl();

    public static IntrospectionApiService getIntrospectionApi() {
        return service;
    }
}
