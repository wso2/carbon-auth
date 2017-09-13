package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.TokenApiServiceImpl;

public class TokenApiServiceFactory {
    private static final TokenApiService service = new TokenApiServiceImpl();

    public static TokenApiService getTokenApi() {
        return service;
    }
}
