package org.wso2.carbon.auth.scim.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.scim.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupListDTO;
import org.wso2.carbon.auth.scim.rest.api.factories.GroupsApiServiceFactory;

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
    name = "org.wso2.carbon.auth.scim.rest.api.GroupsApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/scim2/v1.[\\d]+/Groups")
@Consumes({ "application/scim+json" })
@Produces({ "application/scim+json" })
@ApplicationPath("/Groups")
@io.swagger.annotations.Api(description = "the Groups API")
public class GroupsApi implements Microservice  {
   private final GroupsApiService delegate = GroupsApiServiceFactory.getGroupsApi();

    
    @GET
    
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve groups", notes = "Retrieve list of available groups qualifying under a given filter condition ", response = GroupListDTO.class, tags={ "Groups", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Groups returned.  ", response = GroupListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 304, message = "Not Modified. Empty body because the client has already the latest version of the requested resource. ", response = GroupListDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported. ", response = GroupListDTO.class) })
    public Response groupsGet(@ApiParam(value = "The index of the first element in the result.  ", defaultValue="0") @DefaultValue("0") @QueryParam("startIndex") Integer startIndex
,@ApiParam(value = "Number of elements returned in the paginated result. ", defaultValue="25") @DefaultValue("25") @QueryParam("count") Integer count
,@ApiParam(value = "A filter expression to request a subset of the result. ") @QueryParam("filter") String filter
 ,@Context Request request)
    throws NotFoundException {
        return delegate.groupsGet(startIndex,count,filter,request);
    }
    
    @DELETE
    @Path("/{id}")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "delete group", notes = "Delete a group ", response = void.class, tags={ "Group (Individual)", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Group successfully deleted. ", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. User to be deleted does not exist. ", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. The request has not been performed because one of the preconditions is not met. ", response = void.class) })
    public Response groupsIdDelete(@ApiParam(value = "Resource Id of User or Group ",required=true) @PathParam("id") String id
 ,@Context Request request)
    throws NotFoundException {
        return delegate.groupsIdDelete(id,request);
    }
    
    @GET
    @Path("/{id}")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Get group", notes = "Get details of a group ", response = GroupDTO.class, tags={ "Group (Individual)", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Group details returned.  ", response = GroupDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 304, message = "Not Modified. Empty body because the client has already the latest version of the requested resource. ", response = GroupDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable. The requested media type is not supported. ", response = GroupDTO.class) })
    public Response groupsIdGet(@ApiParam(value = "Resource Id of User or Group ",required=true) @PathParam("id") String id
 ,@Context Request request)
    throws NotFoundException {
        return delegate.groupsIdGet(id,request);
    }
    
    @PUT
    @Path("/{id}")
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "update group", notes = "Update details of a group ", response = GroupDTO.class, tags={ "Group (Individual)", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = GroupDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = GroupDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = GroupDTO.class) })
    public Response groupsIdPut(@ApiParam(value = "Resource Id of User or Group ",required=true) @PathParam("id") String id
,@ApiParam(value = "Group object that needs to be added " ,required=true) GroupDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.groupsIdPut(id,body,request);
    }
    
    @POST
    
    @Consumes({ "application/scim+json" })
    @Produces({ "application/scim+json" })
    @io.swagger.annotations.ApiOperation(value = "Create a group", notes = "Create a new group ", response = GroupDTO.class, tags={ "Groups", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity. ", response = GroupDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = GroupDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported media type. The entity of the request was in a not supported format. ", response = GroupDTO.class) })
    public Response groupsPost(@ApiParam(value = "Group object that needs to be added " ,required=true) GroupDTO body
 ,@Context Request request)
    throws NotFoundException {
        return delegate.groupsPost(body,request);
    }
}
