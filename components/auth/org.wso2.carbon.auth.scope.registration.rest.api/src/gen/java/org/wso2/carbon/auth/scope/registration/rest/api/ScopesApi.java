package org.wso2.carbon.auth.scope.registration.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.scope.registration.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.dto.ScopeToUpdateDTO;
import org.wso2.carbon.auth.scope.registration.rest.api.factories.ScopesApiServiceFactory;

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
    name = "org.wso2.carbon.auth.scope.registration.rest.api.ScopesApi",
    service = Microservice.class,
    immediate = true
)
@Path(".[\\d]+/scopes")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@ApplicationPath("/scopes")
@io.swagger.annotations.Api(description = "the scopes API")
public class ScopesApi implements Microservice  {
   private final ScopesApiService delegate = ScopesApiServiceFactory.getScopesApi();

    @DELETE
    @Path("/name/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Deletes a Scope ", notes = "This API is used to delete scope by scope name. ", response = String.class, tags={ "Scope Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 204, message = "Successful deleted", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = String.class) })
    public Response deleteScope(@ApiParam(value = "scope name of the scope which need to get deleted",required=true) @PathParam("name") String name
, @Context Request request)
    throws NotFoundException {
        return delegate.deleteScope(name, request);
    }
    @GET
    @Path("/name/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns a Scope by Scope Name ", notes = "This API is used to get a scope by given scope name. ", response = ScopeDTO.class, tags={ "Scope Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful Retrieved", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ScopeDTO.class) })
    public Response getScope(@ApiParam(value = "scope name of the scope which the details to be retrieved",required=true) @PathParam("name") String name
, @Context Request request)
    throws NotFoundException {
        return delegate.getScope(name, request);
    }
    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns all available Scopes ", notes = "This API is used to get all the available scopes. ", response = ScopeDTO.class, responseContainer = "List", tags={ "Scope Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful Retrieved", response = ScopeDTO.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = ScopeDTO.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ScopeDTO.class, responseContainer = "List") })
    public Response getScopes(@ApiParam(value = "start index of the list of scopes to be retrieved") @QueryParam("startIndex") Integer startIndex
,@ApiParam(value = "a limited number of scopes to be retrieved") @QueryParam("count") Integer count
, @Context Request request)
    throws NotFoundException {
        return delegate.getScopes(startIndex,count, request);
    }
    @HEAD
    @Path("/name/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Check Scope Existance using Scope Name ", notes = "This API is used to check scope existance using scope name. ", response = String.class, tags={ "Scope Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Scope Exists", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = String.class) })
    public Response isScopeExists(@ApiParam(value = "scope name of the scope which the existance should be checked",required=true) @PathParam("name") String name
, @Context Request request)
    throws NotFoundException {
        return delegate.isScopeExists(name, request);
    }
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Registers a Scope ", notes = "This API is used to create a scope. ", response = ScopeDTO.class, tags={ "Scope Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Successfully Created", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ScopeDTO.class) })
    public Response registerScope(@ApiParam(value = "a scope with the bindings which to be registered" ,required=true) ScopeDTO scope
, @Context Request request)
    throws NotFoundException {
        return delegate.registerScope(scope, request);
    }
    @PUT
    @Path("/name/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Updates a Scope ", notes = "This API is used to update a scope by scope name. ", response = ScopeDTO.class, tags={ "Scope Management", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful updated", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict", response = ScopeDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = ScopeDTO.class) })
    public Response updateScope(@ApiParam(value = "updated scope" ,required=true) ScopeToUpdateDTO scope
,@ApiParam(value = "scope name of the scope which need to get updated",required=true) @PathParam("name") String name
, @Context Request request)
    throws NotFoundException {
        return delegate.updateScope(scope,name, request);
    }
}
