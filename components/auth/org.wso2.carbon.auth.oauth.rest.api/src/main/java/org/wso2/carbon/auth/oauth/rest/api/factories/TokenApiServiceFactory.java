package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.impl.GrantHandlerFactory;
import org.wso2.carbon.auth.oauth.impl.TokenRequestHandlerImpl;
import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.TokenApiServiceImpl;
import org.wso2.carbon.auth.oauth.rest.api.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;

/**
 * Factory class to be used for Token API
 */
public class TokenApiServiceFactory {

    public static TokenApiService getTokenApi() {

        try {
            OAuthDAO oauthDAO = DAOFactory.getClientDAO();
            ApplicationDAO applicationDAO = org.wso2.carbon.auth.client.registration.dao.impl.DAOFactory
                    .getApplicationDAO();
            UserNameMapper userNameMapper = ServiceReferenceHolder.getInstance().getUserNameMapper();
            UserStoreManager userStoreManager = ServiceReferenceHolder.getInstance().getUserStoreManager();
            return new TokenApiServiceImpl(new TokenRequestHandlerImpl(oauthDAO, applicationDAO, new
                    GrantHandlerFactory(userStoreManager, userNameMapper)));
        } catch (OAuthDAOException | ClientRegistrationDAOException e) {
            throw new IllegalStateException("Could not create AuthorizeApiService", e);
        }
    }
}
