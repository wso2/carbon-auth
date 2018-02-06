package org.wso2.carbon.auth.scim.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.scim.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import org.wso2.carbon.auth.scim.rest.api.factories.MeApiServiceFactory;

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
import javax.ws.rs.FormParam;
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
    name = "org.wso2.carbon.auth.scim.rest.api.MeApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/identity/scim2/v1.[\\d]+/Me")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@ApplicationPath("/Me")
@io.swagger.annotations.Api(description = "the Me API")
public class MeApi implements Microservice  {
   private final MeApiService delegate = MeApiServiceFactory.getMeApi();

    
    @DELETE
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete user.", notes = "Delete the currently authenticated user ", response = void.class, tags={ "Me", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. User successfully deleted. ", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. User to be deleted does not exist. ", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. The request has not been performed because one of the preconditions is not met. ", response = void.class) })
    public Response meDelete( @Context Request request)
    throws NotFoundException {
        return delegate.meDelete(request);
    }
    
    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve my user details.", notes = "Retrieve details of the currently authenticated user. ", response = UserDTO.class, tags={ "Me", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. User details returned.  ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 304, message = "Not Modified. Empty body because the client has already the latest version of the requested resource. ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported. ", response = UserDTO.class) })
    public Response meGet( @Context Request request)
    throws NotFoundException {
        return delegate.meGet(request);
    }
    
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create user.", notes = "Create a User ", response = UserDTO.class, tags={ "Me", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = UserDTO.class) })
    public Response mePost(@ApiParam(value = "User object that needs to be added " ,required=true) UserDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.mePost(body,request);
    }
    
    @PUT
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update user.", notes = "Update the currently authenticated user ", response = UserDTO.class, tags={ "Me", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = UserDTO.class) })
    public Response mePut(@ApiParam(value = "User object that needs to be added " ,required=true) UserDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.mePut(body,request);
    }
}
