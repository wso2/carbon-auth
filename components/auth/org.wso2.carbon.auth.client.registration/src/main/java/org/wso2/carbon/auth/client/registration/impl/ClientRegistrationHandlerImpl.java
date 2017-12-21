/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.client.registration.impl;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.dto.ClientRegistrationResponse;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;

/**
 * Implementation of ClientRegistrationHandler Interface
 */
public class ClientRegistrationHandlerImpl implements ClientRegistrationHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationHandlerImpl.class);
    private ApplicationDAO applicationDAO;

    public ClientRegistrationHandlerImpl(ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;
    }

    @Override
    public ClientRegistrationResponse getApplication(String clientId) {
        ClientRegistrationResponse clientRegistrationResponse = new ClientRegistrationResponse();
        try {
            Application application = applicationDAO.getApplication(clientId);
            if (application != null) {
                clientRegistrationResponse.setApplication(application);
                clientRegistrationResponse.setIsSuccessful(true);
            } else {
                log.error("Application with Client Id: " + clientId + ", does not exist ");
                ErrorObject error = new ErrorObject(OAuth2Error.UNAUTHORIZED_CLIENT.getCode(),
                        OAuth2Error.UNAUTHORIZED_CLIENT.getDescription(),
                        OAuth2Error.UNAUTHORIZED_CLIENT.getHTTPStatusCode());
                clientRegistrationResponse.setErrorObject(error);
            }
        } catch (ClientRegistrationDAOException e) {
            log.error("Error while retrieving the Client Application");
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode(),
                    OAuth2Error.SERVER_ERROR.getDescription(),
                    OAuth2Error.SERVER_ERROR.getHTTPStatusCode());
            clientRegistrationResponse.setErrorObject(error);
        }
        return clientRegistrationResponse;
    }

    @Override
    public ClientRegistrationResponse registerApplication(Application newApplication) {
        ClientRegistrationResponse clientRegistrationResponse = new ClientRegistrationResponse();

        try {
            Application application = applicationDAO.createApplication(newApplication);
            if (application != null) {
                clientRegistrationResponse.setApplication(application);
                clientRegistrationResponse.setIsSuccessful(true);
            } else {
                log.error("Application with Client Id: " + newApplication.getClientId() + ", does not exist ");
                ErrorObject error = new ErrorObject(OAuth2Error.UNAUTHORIZED_CLIENT.getCode(),
                        OAuth2Error.UNAUTHORIZED_CLIENT.getDescription(),
                        OAuth2Error.UNAUTHORIZED_CLIENT.getHTTPStatusCode());
                clientRegistrationResponse.setErrorObject(error);
            }
        } catch (ClientRegistrationDAOException e) {
            log.error("Error while registering the Client Application with client ID: " + newApplication.getClientId(),
                    e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode(),
                    OAuth2Error.SERVER_ERROR.getDescription(),
                    OAuth2Error.SERVER_ERROR.getHTTPStatusCode());
            clientRegistrationResponse.setErrorObject(error);
        }

        return clientRegistrationResponse;
    }

    @Override
    public ClientRegistrationResponse updateApplication(String clientId, Application modifiedApplication) {
        ClientRegistrationResponse clientRegistrationResponse = new ClientRegistrationResponse();

        try {
            Application application = applicationDAO.updateApplication(clientId, modifiedApplication);
            if (application != null) {
                clientRegistrationResponse.setApplication(application);
                clientRegistrationResponse.setIsSuccessful(true);
            } else {
                log.error("Application with Client Id: " + clientId + ", does not exist ");
                ErrorObject error = new ErrorObject(OAuth2Error.UNAUTHORIZED_CLIENT.getCode(),
                        OAuth2Error.UNAUTHORIZED_CLIENT.getDescription(),
                        OAuth2Error.UNAUTHORIZED_CLIENT.getHTTPStatusCode());
                clientRegistrationResponse.setErrorObject(error);
            }
        } catch (ClientRegistrationDAOException e) {
            log.error("Error while updating the Client Application with client ID: " + clientId, e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode(),
                    OAuth2Error.SERVER_ERROR.getDescription(),
                    OAuth2Error.SERVER_ERROR.getHTTPStatusCode());
            clientRegistrationResponse.setErrorObject(error);
        }

        return clientRegistrationResponse;
    }

    @Override
    public ClientRegistrationResponse deleteApplication(String clientId) {
        ClientRegistrationResponse clientRegistrationResponse = new ClientRegistrationResponse();
        try {
            applicationDAO.deleteApplication(clientId);
            clientRegistrationResponse.setIsSuccessful(true);
        } catch (ClientRegistrationDAOException e) {
            log.error("Error while deleting the Client Application with client ID: " + clientId, e);
            ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode(),
                    OAuth2Error.SERVER_ERROR.getDescription(),
                    OAuth2Error.SERVER_ERROR.getHTTPStatusCode());
            clientRegistrationResponse.setErrorObject(error);
        }

        return  clientRegistrationResponse;
    }

}
