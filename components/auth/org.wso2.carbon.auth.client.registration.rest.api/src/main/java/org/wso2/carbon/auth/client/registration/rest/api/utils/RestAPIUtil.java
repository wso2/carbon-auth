package org.wso2.carbon.auth.client.registration.rest.api.utils;

import com.nimbusds.oauth2.sdk.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ErrorDTO;
import org.wso2.carbon.auth.core.exception.ErrorHandler;

import java.util.Map;

/**
 * Utility class for Client Registration REST APIS.
 */
public class RestAPIUtil {
    private static final Logger log = LoggerFactory.getLogger(RestAPIUtil.class);

    /**
     * Returns a generic errorDTO
     *
     * @param errorObject The error handler object.
     * @return A generic errorDTO with the specified details
     */
    public static ErrorDTO getErrorDTO(ErrorObject errorObject) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(errorObject.getCode());
        errorDTO.setHttpStatusCode(errorObject.getHTTPStatusCode());
        errorDTO.setDescription(errorObject.getDescription());
        return errorDTO;
    }
}
