package org.wso2.carbon.auth.client.registration.rest.api;

import org.wso2.carbon.auth.client.registration.rest.api.*;
import org.wso2.carbon.auth.client.registration.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.client.registration.rest.api.dto.ApplicationDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.RegistrationRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.UpdateRequestDTO;

import java.util.List;
import org.wso2.carbon.auth.client.registration.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class RegisterApiService {
    public abstract Response deleteApplication(String clientId
 , Request request) throws NotFoundException;
    public abstract Response getApplication(String clientId
 , Request request) throws NotFoundException;
    public abstract Response registerApplication(RegistrationRequestDTO registrationRequest
 , Request request) throws NotFoundException;
    public abstract Response updateApplication(UpdateRequestDTO updateRequest
 ,String clientId
 , Request request) throws NotFoundException;
}
