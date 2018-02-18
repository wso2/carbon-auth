/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.scim.rest.api.util;

import com.google.gson.Gson;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.impl.constants.SCIMCommonConstants;
import org.wso2.carbon.auth.scim.rest.api.SCIMRESTAPIConstants;
import org.wso2.charon3.core.config.CharonConfiguration;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Utility functions for SCIM REST API
 * 
 */
public class SCIMRESTAPIUtils {

    private SCIMRESTAPIUtils() {

    }

    /* Build the javax-rs response from SCIM response
     *
      *@param scimResponse
     * @return Response
     */
    public static Response buildResponse(SCIMResponse scimResponse) throws AuthUserManagementException {
        Response.ResponseBuilder responseBuilder = Response.status(scimResponse.getResponseStatus());
        Map<String, String> httpHeaders = scimResponse.getHeaderParamMap();
        if (httpHeaders != null && !httpHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
                //skip the location header as it is supported by the framework to set the actual hostnames
                if (HttpHeaders.LOCATION.equals(entry.getKey())) {
                    try {
                        responseBuilder.location(new URI(entry.getValue()));
                    } catch (URISyntaxException e) {
                        throw new AuthUserManagementException(
                                "Error while setting Location URI for the resource in response", e);
                    }
                } else {
                    responseBuilder.header(entry.getKey(), entry.getValue());
                }
            }
        }
        if (scimResponse.getResponseMessage() != null) {
            responseBuilder.entity(scimResponse.getResponseMessage());
        }
        responseBuilder.status(scimResponse.getResponseStatus());
        return responseBuilder.build();
    }

    /**
     * Serialize the provided DTO and creates a JSON string
     * 
     * @param dto DTO object
     * @return serialized JSON string from DTO
     */
    public static String getSerializedJsonStringFromBody(Object dto) {
        return new Gson().toJson(dto);
    }
}
