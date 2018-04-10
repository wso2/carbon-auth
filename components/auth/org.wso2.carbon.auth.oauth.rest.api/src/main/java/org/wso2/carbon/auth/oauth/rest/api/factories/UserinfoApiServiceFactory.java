package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.rest.api.UserinfoApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.UserinfoApiServiceImpl;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.impl.IntrospectionManagerImpl;
import org.wso2.carbon.auth.user.info.UserInfoResponseBuilder;
import org.wso2.carbon.auth.user.info.exception.UserInfoException;
import org.wso2.carbon.auth.user.info.impl.UserInfoFactory;
import org.wso2.carbon.auth.user.info.impl.UserInfoRequestHandlerImpl;

/**
 * Factory class to be used for Userinfo API
 */
public class UserinfoApiServiceFactory {

    public static UserinfoApiService getUserinfoApi() {

        IntrospectionManager introspectionManager = new IntrospectionManagerImpl();
        try {
            UserInfoResponseBuilder userInfoResponseBuilder = UserInfoFactory.getUserInfoResponseBuilder();
            return new UserinfoApiServiceImpl(new UserInfoRequestHandlerImpl(introspectionManager,
                    userInfoResponseBuilder));
        } catch (UserInfoException e) {
            throw new IllegalStateException("Could not create UserInfoService", e);
        }
    }

}
