package org.wso2.carbon.auth.client.registration.rest.api.factories;

import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.dao.impl.DAOFactory;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.impl.ClientRegistrationHandlerImpl;
import org.wso2.carbon.auth.client.registration.rest.api.RegisterApiService;
import org.wso2.carbon.auth.client.registration.rest.api.impl.RegisterApiServiceImpl;

public class RegisterApiServiceFactory {

    public static RegisterApiService getRegisterApi() {
        try {
            ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
            return new RegisterApiServiceImpl(new ClientRegistrationHandlerImpl(applicationDAO));
        } catch (ClientRegistrationDAOException e) {
            throw new IllegalStateException("Could not create RegisterApiService", e);
        }
    }
}
