package org.wso2.carbon.auth.scim.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.scim.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserListDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserSearchDTO;
import org.wso2.carbon.auth.scim.rest.api.factories.UsersApiServiceFactory;

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
    name = "org.wso2.carbon.auth.scim.rest.api.UsersApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/scim2/v1.[\\d]+/Users")
@Consumes({ "application/scim+json" })
@Produces({ "application/scim+json" })
@ApplicationPath("/Users")
@io.swagger.annotations.Api(description = "the Users API")
public class UsersApi implements Microservice  {
   private final UsersApiService delegate = UsersApiServiceFactory.getUsersApi();

    @GET
    
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve users", notes = "Retrieve list of available users qualifying under a given filter condition ", response = UserListDTO.class, tags={ "Users", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Users returned.  ", response = UserListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 304, message = "Not Modified. Empty body because the client has already the latest version of the requested resource. ", response = UserListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported. ", response = UserListDTO.class) })
    public Response usersGet(@ApiParam(value = "The index of the first element in the result.  ", defaultValue="0") @DefaultValue("0") @QueryParam("startIndex") Integer startIndex
,@ApiParam(value = "Number of elements returned in the paginated result. ", defaultValue="0") @DefaultValue("0") @QueryParam("count") Integer count
,@ApiParam(value = "A filter expression to request a subset of the result. ", defaultValue="0") @DefaultValue("0") @QueryParam("filter") String filter
 ,@Context Request request)
    throws NotFoundException {
        return delegate.usersGet(startIndex,count,filter,request);
    }
    @DELETE
    @Path("/{id}")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "delete user", notes = "Delete a user ", response = void.class, tags={ "User (Individual)", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. User successfully deleted. ", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. User to be deleted does not exist. ", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. The request has not been performed because one of the preconditions is not met. ", response = void.class) })
    public Response usersIdDelete(@ApiParam(value = "Resource Id of User or Group ",required=true) @PathParam("id") String id
 ,@Context Request request)
    throws NotFoundException {
        return delegate.usersIdDelete(id,request);
    }
    @GET
    @Path("/{id}")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Get user", notes = "Get details of a users ", response = UserDTO.class, tags={ "User (Individual)", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Users returned.  ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 304, message = "Not Modified. Empty body because the client has already the latest version of the requested resource. ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported. ", response = UserDTO.class) })
    public Response usersIdGet(@ApiParam(value = "Resource Id of User or Group ",required=true) @PathParam("id") String id
 ,@Context Request request)
    throws NotFoundException {
        return delegate.usersIdGet(id,request);
    }
    @PUT
    @Path("/{id}")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "update user", notes = "Update details of a users ", response = UserDTO.class, tags={ "User (Individual)", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = UserDTO.class) })
    public Response usersIdPut(@ApiParam(value = "Resource Id of User or Group ",required=true) @PathParam("id") String id
,@ApiParam(value = "User object that needs to be added " ,required=true) UserDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.usersIdPut(id,body,request);
    }
    @POST
    
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new user", notes = "Create a new user ", response = UserDTO.class, tags={ "Users", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = UserDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = UserDTO.class) })
    public Response usersPost(@ApiParam(value = "User object that needs to be added " ,required=true) UserDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.usersPost(body,request);
    }
    @POST
    @Path("/.search")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Search users", notes = "Create a new user ", response = UserListDTO.class, tags={ "Search Users", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = UserListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = UserListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = UserListDTO.class) })
    public Response usersSearchPost(@ApiParam(value = "User Search object " ,required=true) UserSearchDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.usersSearchPost(body,request);
    }
}
