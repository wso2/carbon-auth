package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;
import org.wso2.carbon.auth.oauth.impl.TokenRequestHandlerImpl;
import org.wso2.carbon.auth.oauth.rest.api.TokenApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.TokenApiServiceImpl;

public class TokenApiServiceFactory {

    public static TokenApiService getTokenApi() {
        try {
            OAuthDAO oauthDAO = DAOFactory.getClientDAO();
            return new TokenApiServiceImpl(new TokenRequestHandlerImpl(oauthDAO));
        } catch (OAuthDAOException e) {
            throw new IllegalStateException("Could not create AuthorizeApiService", e);
        }
    }
}
