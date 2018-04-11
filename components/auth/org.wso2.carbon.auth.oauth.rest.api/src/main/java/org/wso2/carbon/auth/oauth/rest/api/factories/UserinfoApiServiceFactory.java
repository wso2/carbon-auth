package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.rest.api.UserinfoApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.UserinfoApiServiceImpl;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.impl.IntrospectionManagerImpl;
import org.wso2.carbon.auth.user.info.impl.UserInfoRequestHandlerImpl;

/**
 * Factory class to be used for Userinfo API
 */
public class UserinfoApiServiceFactory {

    public static UserinfoApiService getUserinfoApi() {

        IntrospectionManager introspectionManager = new IntrospectionManagerImpl();
        return new UserinfoApiServiceImpl(new UserInfoRequestHandlerImpl(introspectionManager));
    }

}
