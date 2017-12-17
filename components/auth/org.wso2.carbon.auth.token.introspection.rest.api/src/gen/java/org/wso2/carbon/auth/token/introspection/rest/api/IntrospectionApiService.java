package org.wso2.carbon.auth.token.introspection.rest.api;

import org.wso2.carbon.auth.token.introspection.rest.api.*;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.Request;

import org.wso2.carbon.auth.token.introspection.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.IntrospectionResponseDTO;

import java.util.List;
import org.wso2.carbon.auth.token.introspection.rest.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class IntrospectionApiService {
    public abstract Response registerApplication(String token
  ,Request request) throws NotFoundException;
}
