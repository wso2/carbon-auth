package org.wso2.carbon.auth.rest.api.commons.interceptors;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.rest.api.commons.RestApiConstants;
import org.wso2.carbon.auth.rest.api.commons.dto.ErrorDTO;
import org.wso2.carbon.auth.rest.api.commons.util.RestApiUtil;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.interceptor.RequestInterceptor;
import org.wso2.msf4j.internal.MSF4JConstants;

import java.lang.reflect.Method;
/**
 * Security Interceptor that does basic authentication for REST ApI requests.
 */
@Component(
        name = "org.wso2.carbon.auth.rest.api.commons.interceptors.RestAPIAuthCORSInterceptor",
        service = RequestInterceptor.class,
        immediate = true
)
public class RestAPIAuthCORSInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RestAPIAuthCORSInterceptor.class);

    @Override
    public boolean interceptRequest(Request request, Response response) {
        Method method = (Method) request.getProperty(MSF4JConstants.METHOD_PROPERTY_NAME);

        //CORS for Environments - Add allowed Origin when User-Agent sent 'Origin' header.
        String origin = request.getHeader(RestApiConstants.ORIGIN_HEADER);
        String allowedOrigin = RestApiUtil.getAllowedOrigin(origin);
        if (allowedOrigin != null) {
            response.setHeader(RestApiConstants.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, allowedOrigin)
                    .setHeader(RestApiConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
        }

        //CORS for Environments - Add allowed Methods and Headers when 'OPTIONS' method is called.
        if (request.getHttpMethod().equalsIgnoreCase(RestApiConstants.HTTP_OPTIONS)) {
            try {
                String definedHttpMethods =
                        RestApiUtil.getDefinedMethodHeadersInSwaggerContent(request, method);
                if (definedHttpMethods != null) {
                    response.setHeader(RestApiConstants.ACCESS_CONTROL_ALLOW_METHODS_HEADER, definedHttpMethods)
                            .setHeader(RestApiConstants.ACCESS_CONTROL_ALLOW_HEADERS_HEADER,
                                    RestApiConstants.ACCESS_CONTROL_ALLOW_HEADERS_LIST)
                            .setStatus(javax.ws.rs.core.Response.Status.OK.getStatusCode()).send();
                    return false;
                } else {
                    String msg = "Couldn't find declared HTTP methods in swagger.yaml";
                    ErrorDTO errorDTO = RestApiUtil.getInternalServerErrorDTO();
                    log.error(msg);
                    response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                            .setEntity(errorDTO).send();
                    return false;
                }
            } catch (AuthException e) {
                String msg = "Couldn't find declared HTTP methods in swagger.yaml";
                ErrorDTO errorDTO = RestApiUtil.getErrorDTO(e.getErrorHandler());
                log.error(msg, e);
                response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .setEntity(errorDTO).send();
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onRequestInterceptionError(Request request, Response response, Exception e) {
        return false;
    }
}
