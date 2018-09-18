package org.wso2.carbon.auth.scim.rest.api;

import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public abstract class MeApiService {
    public abstract Response meGet( Request request) throws NotFoundException;
}
