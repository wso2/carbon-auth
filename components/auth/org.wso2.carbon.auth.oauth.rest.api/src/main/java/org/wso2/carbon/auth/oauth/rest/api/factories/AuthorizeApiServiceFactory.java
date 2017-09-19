package org.wso2.carbon.auth.oauth.rest.api.factories;

import org.wso2.carbon.auth.oauth.dao.ClientDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;
import org.wso2.carbon.auth.oauth.impl.AuthCodeManagerImpl;
import org.wso2.carbon.auth.oauth.rest.api.AuthorizeApiService;
import org.wso2.carbon.auth.oauth.rest.api.impl.AuthorizeApiServiceImpl;

public class AuthorizeApiServiceFactory {

    public static AuthorizeApiService getAuthorizeApi() {
        try {
            ClientDAO clientDAO = DAOFactory.getClientDAO();
            return new AuthorizeApiServiceImpl(new AuthCodeManagerImpl(clientDAO));
        } catch (ClientDAOException e) {
           throw new IllegalStateException("Could not create AuthorizeApiService", e);
        }
    }
}
