package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.impl.AuthCodeManagerImpl;
import org.wso2.carbon.auth.oauth.impl.ClientRegistryImpl;
import org.wso2.carbon.auth.oauth.rest.api.AuthorizeApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.AuthorizeApiServiceImpl;

public class AuthorizeApiServiceFactory {
    private static final AuthorizeApiService service = new AuthorizeApiServiceImpl(new ClientRegistryImpl(),
            new AuthCodeManagerImpl());

    public static AuthorizeApiService getAuthorizeApi() {
        return service;
    }
}
