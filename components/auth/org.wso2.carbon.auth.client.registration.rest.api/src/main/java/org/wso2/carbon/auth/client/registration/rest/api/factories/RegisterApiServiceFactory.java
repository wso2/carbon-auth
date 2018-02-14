package org.wso2.carbon.auth.client.registration.rest.api.factories;

import org.wso2.carbon.auth.client.registration.rest.api.RegisterApiService;
import org.wso2.carbon.auth.client.registration.rest.api.impl.RegisterApiServiceImpl;

/**
 * Factory class for DCRM service 
 * 
 */
public class RegisterApiServiceFactory {
    private static final RegisterApiService service = new RegisterApiServiceImpl();

    public static RegisterApiService getRegisterApi() {
        return service;
    }
}
