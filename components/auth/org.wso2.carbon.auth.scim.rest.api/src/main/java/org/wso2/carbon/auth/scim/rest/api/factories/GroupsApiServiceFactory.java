package org.wso2.carbon.auth.scim.rest.api.factories;

import org.wso2.carbon.auth.scim.rest.api.GroupsApiService;
import org.wso2.carbon.auth.scim.rest.api.impl.GroupsApiServiceImpl;

/**
 * Factory class for SCIM Groups API
 * 
 */
public class GroupsApiServiceFactory {
    private static final GroupsApiService service = new GroupsApiServiceImpl();

    public static GroupsApiService getGroupsApi() {
        return service;
    }
}
