package org.wso2.carbon.auth.token.introspection.rest.api.impl;

import com.google.gson.Gson;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;
import org.wso2.carbon.auth.token.introspection.impl.IntrospectionManagerImpl;
import org.wso2.carbon.auth.token.introspection.rest.api.IntrospectionApiService;
import org.wso2.carbon.auth.token.introspection.rest.api.NotFoundException;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.IntrospectionResponseDTO;
import org.wso2.carbon.auth.token.introspection.rest.api.utils.MappingUtil;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class IntrospectionApiServiceImpl extends IntrospectionApiService {
    @Override
    public Response introspect(String token, Request request) throws NotFoundException {
        IntrospectionManager introspectionManager;
        introspectionManager = new IntrospectionManagerImpl();
        IntrospectionResponse introspectionResponse = introspectionManager.introspect(token);
        IntrospectionResponseDTO introspectionResponseDTO = MappingUtil
                .applicationModelToApplicationDTO(introspectionResponse);
        Gson gson = new Gson();
        String json = gson.toJson(introspectionResponseDTO);
        return Response.status(Response.Status.OK).entity(json).build();
    }
}
