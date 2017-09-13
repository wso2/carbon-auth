package org.wso2.carbon.auth.scim.rest.api.factories;

import org.wso2.carbon.auth.scim.rest.api.UsersApiService;
import org.wso2.carbon.auth.scim.rest.api.impl.UsersApiServiceImpl;

public class UsersApiServiceFactory {
    private static final UsersApiService service = new UsersApiServiceImpl();

    public static UsersApiService getUsersApi() {
        return service;
    }
}
