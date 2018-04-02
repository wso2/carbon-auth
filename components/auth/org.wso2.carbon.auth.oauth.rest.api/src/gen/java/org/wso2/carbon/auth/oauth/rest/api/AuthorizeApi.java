package org.wso2.carbon.auth.oauth.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.oauth.rest.api.factories.AuthorizeApiServiceFactory;

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
import javax.ws.rs.OPTIONS;
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
    name = "org.wso2.carbon.auth.oauth.rest.api.AuthorizeApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/auth/oauth2/v1.[\\d]+/authorize")


@ApplicationPath("/authorize")
@io.swagger.annotations.Api(description = "the authorize API")
public class AuthorizeApi implements Microservice  {
   private final AuthorizeApiService delegate = AuthorizeApiServiceFactory.getAuthorizeApi();

    @OPTIONS
    @GET
    
    @Consumes({ "application/x-www-form-urlencoded" })
    @Produces({ "application/x-www-form-urlencoded" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "", response = void.class, tags={ "OAuth Authorization", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 302, message = "Response from authorization endpoint", response = void.class) })
    public Response authorizeGet(@ApiParam(value = "Expected response type",required=true) @QueryParam("response_type") String responseType
,@ApiParam(value = "OAuth client identifier",required=true) @QueryParam("client_id") String clientId
,@ApiParam(value = "Clients redirection endpoint") @QueryParam("redirect_uri") String redirectUri
,@ApiParam(value = "OAuth scopes") @QueryParam("scope") String scope
,@ApiParam(value = "Opaque value used by the client to maintain state between the request and callback") @QueryParam("state") String state
 ,@Context Request request)
    throws NotFoundException {
        return delegate.authorizeGet(responseType,clientId,redirectUri,scope,state,request);
    }
}
