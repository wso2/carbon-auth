package org.wso2.carbon.auth.scim.rest.api;

import org.wso2.carbon.auth.scim.rest.api.*;
import org.wso2.carbon.auth.scim.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.scim.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;

import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class MeApiService {
    public abstract Response meGet( Request request) throws NotFoundException;
}
