package org.wso2.carbon.auth.token.introspection.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.token.introspection.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.IntrospectionResponseDTO;
import org.wso2.carbon.auth.token.introspection.rest.api.factories.IntrospectionApiServiceFactory;

import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.formparam.FormDataParam;
import org.osgi.service.component.annotations.Component;

import java.io.InputStream;
import javax.ws.rs.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component(
    name = "org.wso2.carbon.auth.token.introspection.rest.api.IntrospectionApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/identity/oauth2/introspect/v1.[\\d]+/introspection")
@Consumes({ "application/x-www-form-urlencoded" })
@Produces({ "application/json" })
@ApplicationPath("/introspection")
@io.swagger.annotations.Api(description = "the introspection API")
public class IntrospectionApi implements Microservice  {
   private final IntrospectionApiService delegate = IntrospectionApiServiceFactory.getIntrospectionApi();

    
    @POST
    
    @Consumes({ "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Token introspection ", notes = "This API is used to introspection. ", response = IntrospectionResponseDTO.class, tags={ "OAuth2 DCR", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Ok", response = IntrospectionResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = IntrospectionResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict", response = IntrospectionResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = IntrospectionResponseDTO.class) })
    public Response introspect(@ApiParam(value = "token to be introspect.", required=true)  @FormParam("token")  String token
 ,@Context Request request)
    throws NotFoundException {
        return delegate.introspect(token,request);
    }
}
