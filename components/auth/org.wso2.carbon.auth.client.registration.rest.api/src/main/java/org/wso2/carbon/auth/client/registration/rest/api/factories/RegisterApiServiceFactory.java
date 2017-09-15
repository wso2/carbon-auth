package org.wso2.carbon.auth.client.registration.rest.api.factories;

import org.wso2.carbon.auth.client.registration.impl.ClientRegistrationHandlerImpl;
import org.wso2.carbon.auth.client.registration.rest.api.RegisterApiService;
import org.wso2.carbon.auth.client.registration.rest.api.impl.RegisterApiServiceImpl;

public class RegisterApiServiceFactory {
    private static final RegisterApiService service = new RegisterApiServiceImpl(new ClientRegistrationHandlerImpl());

    public static RegisterApiService getRegisterApi() {
        return service;
    }
}
