package org.wso2.carbon.auth.token.introspection.rest.api;


import io.swagger.annotations.ApiParam;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.auth.token.introspection.rest.api.dto.IntrospectionResponseDTO;
import org.wso2.carbon.auth.token.introspection.rest.api.factories.IntrospectApiServiceFactory;
import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.Request;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component(
    name = "org.wso2.carbon.auth.token.introspection.rest.api.IntrospectApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/identity/oauth2/introspect/v1.[\\d]+/introspect")
@Consumes({ "application/x-www-form-urlencoded" })
@Produces({ "application/json" })
@ApplicationPath("/introspect")
@io.swagger.annotations.Api(description = "the introspect API")
public class IntrospectApi implements Microservice  {
   private final IntrospectApiService delegate = IntrospectApiServiceFactory.getIntrospectApi();

    @OPTIONS
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
