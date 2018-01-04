package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.impl.AuthRequestHandlerImpl;
import org.wso2.carbon.auth.oauth.rest.api.AuthorizeApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.AuthorizeApiServiceImpl;

public class AuthorizeApiServiceFactory {

    public static AuthorizeApiService getAuthorizeApi() {
        try {
            OAuthDAO oauthDAO = DAOFactory.getClientDAO();
            return new AuthorizeApiServiceImpl(new AuthRequestHandlerImpl(oauthDAO));
        } catch (OAuthDAOException e) {
           throw new IllegalStateException("Could not create AuthorizeApiService", e);
        }
    }
}
