package org.wso2.carbon.auth.scim.rest.api.factories;

import org.wso2.carbon.auth.scim.rest.api.MeApiService;
import org.wso2.carbon.auth.scim.rest.api.impl.MeApiServiceImpl;

/**
 * Factory class for SCIM Me API
 *
 */
public class MeApiServiceFactory {
    private static final MeApiService service = new MeApiServiceImpl();

    public static MeApiService getMeApi() {
        return service;
    }
}
