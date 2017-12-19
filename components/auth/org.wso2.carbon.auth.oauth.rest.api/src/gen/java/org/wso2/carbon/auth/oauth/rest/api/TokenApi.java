package org.wso2.carbon.auth.oauth.rest.api;


import io.swagger.annotations.ApiParam;

import org.wso2.carbon.auth.oauth.rest.api.dto.TokenErrorResponseDTO;
import org.wso2.carbon.auth.oauth.rest.api.dto.TokenResponseDTO;
import org.wso2.carbon.auth.oauth.rest.api.factories.TokenApiServiceFactory;

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
    name = "org.wso2.carbon.auth.oauth.rest.api.TokenApi",
    service = Microservice.class,
    immediate = true
)
@Path("/uvindra/oauth/1.[\\d]+/token")


@ApplicationPath("/token")
@io.swagger.annotations.Api(description = "the token API")
public class TokenApi implements Microservice  {
   private final TokenApiService delegate = TokenApiServiceFactory.getTokenApi();

    
    @POST
    
    @Consumes({ "application/x-www-form-urlencoded" })
    @Produces({ "application/json;charset=UTF-8" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "", response = TokenResponseDTO.class, tags={ "OAuth Token", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Successful response from token endpoint. ", response = TokenResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Error response from token endpoint due to malformed request. ", response = TokenResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized. Error response from token endpoint due to client authentication failure. ", response = TokenResponseDTO.class) })
    public Response tokenPost(@ApiParam(value = "Authentication scheme header" ,required=true)@HeaderParam("Authorization") String authorization
,@ApiParam(value = "Required OAuth grant type",required=true) @QueryParam("grant_type") String grantType
,@ApiParam(value = "Authorization code to be sent for authorization grant type") @QueryParam("code") String code
,@ApiParam(value = "Clients redirection endpoint") @QueryParam("redirect_uri") String redirectUri
,@ApiParam(value = "OAuth client identifier") @QueryParam("client_id") String clientId
,@ApiParam(value = "Refresh token issued to the client.") @QueryParam("refresh_token") String refreshToken
,@ApiParam(value = "OAuth scopes") @QueryParam("scope") String scope
,@ApiParam(value = "username")  @FormParam("username")  String username
,@ApiParam(value = "password")  @FormParam("password")  String password
 ,@Context Request request)
    throws NotFoundException {
        return delegate.tokenPost(authorization,grantType,code,redirectUri,clientId,refreshToken,scope,username,password,request);
    }
}
