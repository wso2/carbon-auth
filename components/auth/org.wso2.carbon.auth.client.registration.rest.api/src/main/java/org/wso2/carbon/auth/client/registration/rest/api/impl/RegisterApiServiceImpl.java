package org.wso2.carbon.auth.client.registration.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.client.registration.rest.api.*;
import org.wso2.carbon.auth.client.registration.rest.api.dto.*;
import org.wso2.carbon.auth.client.registration.rest.api.NotFoundException;
import org.wso2.carbon.auth.client.registration.rest.api.utils.MappingUtil;
import org.wso2.msf4j.Request;
import javax.ws.rs.core.Response;

public class RegisterApiServiceImpl extends RegisterApiService {
    private static final Logger log = LoggerFactory.getLogger(RegisterApiServiceImpl.class);
    private ClientRegistrationHandler clientRegistrationHandler;

    public RegisterApiServiceImpl(ClientRegistrationHandler clientRegistrationHandler) {
        this.clientRegistrationHandler = clientRegistrationHandler;
    }

    @Override
    public Response deleteApplication(String clientId, Request request) throws NotFoundException {
        clientRegistrationHandler.deleteApplication(clientId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response getApplication(String clientId, Request request) throws NotFoundException {
        ApplicationDTO applicationDTO = clientRegistrationHandler.getApplication(clientId);
        return Response.status(Response.Status.OK).entity(applicationDTO).build();
    }

    @Override
    public Response registerApplication(RegistrationRequestDTO registrationRequest, Request request)
            throws NotFoundException {
        ApplicationDTO applicationDTO;
        Application application = clientRegistrationHandler
                .registerApplication(MappingUtil.registrationRequestToApplication(registrationRequest));

        applicationDTO = MappingUtil.applicationModelToApplicationDTO(application);

        return Response.status(Response.Status.CREATED).entity(applicationDTO).build();
    }

    @Override
    public Response updateApplication(UpdateRequestDTO updateRequest, String clientId, Request request)
            throws NotFoundException {
        ApplicationDTO applicationDTO;
        Application application = clientRegistrationHandler
                .updateApplication(clientId, MappingUtil.updateRequestToApplication(updateRequest));
        applicationDTO = MappingUtil.applicationModelToApplicationDTO(application);

        return Response.status(Response.Status.OK).entity(applicationDTO).build();
    }
}
