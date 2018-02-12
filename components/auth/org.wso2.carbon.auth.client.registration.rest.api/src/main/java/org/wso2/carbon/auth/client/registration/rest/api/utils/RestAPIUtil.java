package org.wso2.carbon.auth.client.registration.rest.api.utils;

import com.nimbusds.oauth2.sdk.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ErrorDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Utility class for Client Registration REST APIS.
 * 
 * @deprecated Use the RestAPIUtil inside rest.api.commons package for common methods
 */
@Deprecated
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

    /**
     * Returns an Internal Server Error DTO
     *
     * @return an Internal Server Error DTO
     */
    public static ErrorDTO getInternalServerErrorDTO() {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode("500");
        errorDTO.setHttpStatusCode(500);
        errorDTO.setDescription("Internal Server Error");
        return errorDTO;
    }

    /**
     * Parse user credentials authorization header
     *
     * @param header authorization header value
     * @return Object array with username/password
     * @throws ParseException throws if a parse exception occurred
     */
    public static Object[] parse(String header) throws ParseException {
        Charset charsetUTF8 = Charset.forName("UTF-8");
        String[] parts = header.split("\\s");
        if (parts.length != 2) {
            throw new ParseException(
                    "Malformed user basic authentication: Unexpected number of HTTP Authorization header value parts: "
                            + parts.length);
        } else if (!parts[0].equalsIgnoreCase("Basic")) {
            throw new ParseException("HTTP authentication must be \"Basic\"");
        } else {
            String credentialsString = new String(Base64.getDecoder().decode(parts[1]), charsetUTF8);
            String[] credentials = credentialsString.split(":", 2);
            if (credentials.length != 2) {
                throw new ParseException("Malformed basic authentication: Missing credentials delimiter \":\"");
            } else {
                try {
                    String username = URLDecoder.decode(credentials[0], charsetUTF8.name());
                    String password = URLDecoder.decode(credentials[1], charsetUTF8.name());
                    return new Object[] { username, password };
                } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                    throw new ParseException("Malformed basic authentication: Invalid URL encoding", e);
                }
            }
        }
    }
}
