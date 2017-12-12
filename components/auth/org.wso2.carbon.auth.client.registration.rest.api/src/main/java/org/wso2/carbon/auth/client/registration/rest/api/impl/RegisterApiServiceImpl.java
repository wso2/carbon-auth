package org.wso2.carbon.auth.client.registration.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.dto.ClientRegistrationResponse;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationException;
import org.wso2.carbon.auth.client.registration.impl.ClientRegistrationFactory;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.client.registration.rest.api.*;
import org.wso2.carbon.auth.client.registration.rest.api.dto.*;
import org.wso2.carbon.auth.client.registration.rest.api.NotFoundException;
import org.wso2.carbon.auth.client.registration.rest.api.utils.MappingUtil;
import org.wso2.carbon.auth.client.registration.rest.api.utils.RestAPIUtil;
import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;

public class RegisterApiServiceImpl extends RegisterApiService {
    private static final Logger log = LoggerFactory.getLogger(RegisterApiServiceImpl.class);

    @Override
    public Response deleteApplication(String clientId, Request request) throws NotFoundException {
        ClientRegistrationHandler handler;
        try {
            handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();

            ClientRegistrationResponse registrationResponse = handler.deleteApplication(clientId);
            if (!registrationResponse.isSuccessful()) {
                ErrorDTO errorDTO = RestAPIUtil.getErrorDTO(registrationResponse.getErrorObject());
                return Response.status(registrationResponse.getErrorObject().getHTTPStatusCode()).entity(errorDTO)
                        .build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRegistrationException e) {
            log.error("Error while deleting application", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestAPIUtil.getInternalServerErrorDTO()).build();
        }
    }

    @Override
    public Response getApplication(String clientId, Request request) throws NotFoundException {
        ApplicationDTO applicationDTO;
        ClientRegistrationHandler handler;
        try {
            handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
            ClientRegistrationResponse registrationResponse = handler.getApplication(clientId);
            if (!registrationResponse.isSuccessful()) {
                ErrorDTO errorDTO = RestAPIUtil.getErrorDTO(registrationResponse.getErrorObject());
                return Response.status(registrationResponse.getErrorObject().getHTTPStatusCode()).entity(errorDTO)
                        .build();
            }
            applicationDTO = MappingUtil.applicationModelToApplicationDTO(registrationResponse.getApplication());

            return Response.status(Response.Status.OK).entity(applicationDTO).build();
        } catch (ClientRegistrationException e) {
            log.error("Error while getting application", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestAPIUtil.getInternalServerErrorDTO()).build();
        }
    }

    @Override
    public Response registerApplication(RegistrationRequestDTO registrationRequest, Request request)
            throws NotFoundException {
        ApplicationDTO applicationDTO;
        ClientRegistrationHandler handler;
        try {
            handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
            ClientRegistrationResponse registrationResponse = handler
                    .registerApplication(MappingUtil.registrationRequestToApplication(registrationRequest));

            if (!registrationResponse.isSuccessful()) {
                ErrorDTO errorDTO = RestAPIUtil.getErrorDTO(registrationResponse.getErrorObject());
                return Response.status(registrationResponse.getErrorObject().getHTTPStatusCode()).entity(errorDTO)
                        .build();
            }

            Application application = registrationResponse.getApplication();
            applicationDTO = MappingUtil.applicationModelToApplicationDTO(application);
            return Response.status(Response.Status.CREATED).entity(applicationDTO).build();
        } catch (ClientRegistrationException e) {
            log.error("Error while registering application", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestAPIUtil.getInternalServerErrorDTO()).build();
        }
    }

    @Override
    public Response updateApplication(UpdateRequestDTO updateRequest, String clientId, Request request)
            throws NotFoundException {
        ApplicationDTO applicationDTO;
        ClientRegistrationHandler handler;
        try {
            handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();

            ClientRegistrationResponse registrationResponse = handler
                    .updateApplication(clientId, MappingUtil.updateRequestToApplication(updateRequest));
            if (!registrationResponse.isSuccessful()) {
                ErrorDTO errorDTO = RestAPIUtil.getErrorDTO(registrationResponse.getErrorObject());
                return Response.status(registrationResponse.getErrorObject().getHTTPStatusCode()).entity(errorDTO)
                        .build();
            }
            Application application = registrationResponse.getApplication();
            applicationDTO = MappingUtil.applicationModelToApplicationDTO(application);

            return Response.status(Response.Status.OK).entity(applicationDTO).build();
        } catch (ClientRegistrationException e) {
            log.error("Error while updating application", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestAPIUtil.getInternalServerErrorDTO()).build();
        }
    }
}
