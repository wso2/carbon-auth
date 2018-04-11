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
    name = "org.wso2.carbon.auth.oauth.rest.api.TokenApi",
    service = Microservice.class,
    immediate = true
)
@Path("/api/auth/oauth2/v1.[\\d]+/token")


@ApplicationPath("/token")
@io.swagger.annotations.Api(description = "the token API")
public class TokenApi implements Microservice  {
   private final TokenApiService delegate = TokenApiServiceFactory.getTokenApi();

    @OPTIONS
    @POST
    
    @Consumes({ "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "", response = TokenResponseDTO.class, tags={ "OAuth Token", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. Successful response from token endpoint. ", response = TokenResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. Error response from token endpoint due to malformed request. ", response = TokenResponseDTO.class),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized. Error response from token endpoint due to client authentication failure. ", response = TokenResponseDTO.class) })
    public Response tokenPost(@ApiParam(value = "Required OAuth grant type", required=true)  @FormParam("grant_type")  String grantType
,@ApiParam(value = "Authentication scheme header" )@HeaderParam("Authorization") String authorization
,@ApiParam(value = "Authorization code to be sent for authorization grant type")  @FormParam("code")  String code
,@ApiParam(value = "Clients redirection endpoint")  @FormParam("redirect_uri")  String redirectUri
,@ApiParam(value = "OAuth client identifier")  @FormParam("client_id")  String clientId
,@ApiParam(value = "OAuth client secret")  @FormParam("client_secret")  String clientSecret
,@ApiParam(value = "Refresh token issued to the client.")  @FormParam("refresh_token")  String refreshToken
,@ApiParam(value = "OAuth scopes")  @FormParam("scope")  String scope
,@ApiParam(value = "username")  @FormParam("username")  String username
,@ApiParam(value = "password")  @FormParam("password")  String password
,@ApiParam(value = "Validity period of token")  @FormParam("validity_period")  Long validityPeriod
 ,@Context Request request)
    throws NotFoundException {

        return delegate.tokenPost(grantType,authorization,code,redirectUri,clientId,clientSecret,refreshToken,scope,username,password,validityPeriod,request);
    }
}
