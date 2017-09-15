package org.wso2.carbon.auth.client.registration.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.client.registration.rest.api.dto.ApplicationDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.RegistrationRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.UpdateRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.factories.RegisterApiServiceFactory;

import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.formparam.FileInfo;
import org.wso2.msf4j.formparam.FormDataParam;
import org.osgi.service.component.annotations.Component;

import java.io.InputStream;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component(
    name = "org.wso2.carbon.auth.client.registration.rest.api.RegisterApi",
    service = Microservice.class,
    immediate = true
)
@Path(".[\\d]+/register")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@ApplicationPath("/register")
@io.swagger.annotations.Api(description = "the register API")
public class RegisterApi implements Microservice  {
   private final RegisterApiService delegate = RegisterApiServiceFactory.getRegisterApi();

    @DELETE
    @Path("/{client_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete OAuth2 application ", notes = "This API is used to delete an OAuth2 application by client_id. ", response = void.class, tags={ "OAuth2 DCR", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "Successfully deleted", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = void.class) })
    public Response deleteApplication(@ApiParam(value = "Unique identifier of the OAuth2 client application.",required=true) @PathParam("client_id") String clientId
, @Context Request request)
    throws NotFoundException {
        return delegate.deleteApplication(clientId, request);
    }
    @GET
    @Path("/{client_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get OAuth2 application information ", notes = "This API is used to get/retrieve an OAuth2 application by client_id. ", response = ApplicationDTO.class, tags={ "OAuth2 DCR", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successfully Retrieved", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ApplicationDTO.class) })
    public Response getApplication(@ApiParam(value = "Unique identifier of the OAuth2 client application.",required=true) @PathParam("client_id") String clientId
, @Context Request request)
    throws NotFoundException {
        return delegate.getApplication(clientId, request);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Registers an OAuth2 application ", notes = "This API is used to create an OAuth2 application. ", response = ApplicationDTO.class, tags={ "OAuth2 DCR", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ApplicationDTO.class) })
    public Response registerApplication(@ApiParam(value = "Application information to register." ,required=true) RegistrationRequestDTO registrationRequest
, @Context Request request)
    throws NotFoundException {
        return delegate.registerApplication(registrationRequest, request);
    }
    @PUT
    @Path("/{client_id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Updates an OAuth2 application ", notes = "This API is used to update an OAuth2 application. ", response = ApplicationDTO.class, tags={ "OAuth2 DCR", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successfully updated", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict", response = ApplicationDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ApplicationDTO.class) })
    public Response updateApplication(@ApiParam(value = "Application information to update." ,required=true) UpdateRequestDTO updateRequest
,@ApiParam(value = "Unique identifier for the OAuth2 client application.",required=true) @PathParam("client_id") String clientId
, @Context Request request)
    throws NotFoundException {
        return delegate.updateApplication(updateRequest,clientId, request);
    }
}
