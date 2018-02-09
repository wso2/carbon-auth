package org.wso2.carbon.auth.client.registration.rest.api.impl;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.apache.commons.lang3.StringUtils;
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
import org.wso2.carbon.auth.client.registration.rest.api.utils.ParseException;
import org.wso2.carbon.auth.client.registration.rest.api.utils.RestAPIUtil;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.mgt.UserStoreManagerFactory;
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
            UserStoreManager userStoreManager = UserStoreManagerFactory.getUserStoreManager();
            handler = ClientRegistrationFactory.getInstance().getClientRegistrationHandler();
            Application newApp = MappingUtil.registrationRequestToApplication(registrationRequest);
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.isEmpty(authHeader)) {
                return Response.status(OAuth2Error.INVALID_REQUEST.getHTTPStatusCode())
                        .entity(RestAPIUtil.getErrorDTO(OAuth2Error.INVALID_REQUEST)).build();
            }
            Object[] cred = RestAPIUtil.parse(authHeader);
            String user = (String) cred[0];
            Object pass = cred[1];
            boolean valid = userStoreManager.doAuthenticate(user, pass);
            if (!valid) {
                ErrorObject error = new ErrorObject("invalid_user", "User authentication failed", 401);
                ErrorDTO errorDTO = RestAPIUtil.getErrorDTO(error);
                return Response.status(error.getHTTPStatusCode()).entity(errorDTO).build();
            }
            newApp.setAuthUser(user);
            ClientRegistrationResponse registrationResponse = handler.registerApplication(newApp);

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
        } catch (UserStoreException e) {
            log.error("Error while validating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(RestAPIUtil.getInternalServerErrorDTO()).build();
        } catch (ParseException e) {
            log.error("Error while parsing authorization header", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(RestAPIUtil.getInternalServerErrorDTO()).build();
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
