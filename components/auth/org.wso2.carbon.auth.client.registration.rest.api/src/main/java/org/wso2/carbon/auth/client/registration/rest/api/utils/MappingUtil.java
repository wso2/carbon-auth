/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.client.registration.rest.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.client.registration.constants.ClientRegistrationConstants;
import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.client.registration.rest.api.StringUtil;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ApplicationDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.RegistrationRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.UpdateRequestDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for mapping rest api DTOs to Models
 */
public class MappingUtil {
    /**
     * This method convert the Dto object into Model
     *
     * @param application Model instance with application data
     * @return ApplicationDTO Contains data of an application
     */
    public static ApplicationDTO applicationModelToApplicationDTO(Application application) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setClientName(application.getClientName());
        applicationDTO.setClientSecret(application.getClientSecret());
        applicationDTO.setClientId(application.getClientId());
        List<String> redirectUrisList = new ArrayList<String>();
        redirectUrisList.add(application.getCallBackUrl());
        applicationDTO.setRedirectUris(redirectUrisList);

        return applicationDTO;
    }

    /**
     * This method convert the Dto object into Model
     *
     * @param registrationRequestDTO Contains data of an application
     * @return Application model instance with application data
     */
    public static Application registrationRequestToApplication(RegistrationRequestDTO registrationRequestDTO) {
        String grantTypes = StringUtils.join(registrationRequestDTO.getGrantTypes(), " ");
        Application newApplication = new Application();
        newApplication.setClientName(registrationRequestDTO.getClientName());
        newApplication.setCallBackUrl(
                getCallbackUrl(registrationRequestDTO.getRedirectUris(), registrationRequestDTO.getGrantTypes()));
        newApplication.setGrantTypes(grantTypes);

        return newApplication;
    }

    /**
     * This method convert the Dto object into Model
     *
     * @param updateRequestDTO Contains data of an application
     * @return Application model instance with application data
     */
    public static Application updateRequestToApplication(UpdateRequestDTO updateRequestDTO) {
        String grantTypes = StringUtils.join(updateRequestDTO.getGrantTypes(), " ");
        Application updatedApplication = new Application();
        updatedApplication.setClientName(updateRequestDTO.getClientName());
        updatedApplication.setCallBackUrl(
                getCallbackUrl(updateRequestDTO.getRedirectUris(), updateRequestDTO.getGrantTypes()));
        updatedApplication.setGrantTypes(grantTypes);

        return updatedApplication;
    }

    private static String getCallbackUrl(List<String> redirectUris, List<String> grantTypes) {

        //TODO: After implement multi-urls to the oAuth application, we have to change this API call
        //TODO: need to validate before processing request
        if (redirectUris.size() == 0 && (grantTypes.contains(
                ClientRegistrationConstants.GrantTypes.AUTHORIZATION_CODE) || grantTypes.
                contains(ClientRegistrationConstants.GrantTypes.IMPLICIT))) {
            throw new IllegalStateException("Valid input has not been provided");
        } else if (redirectUris.size() == 1) {
            String redirectUri = redirectUris.get(0);
            if (DCRMUtils.isRedirectionUriValid(redirectUri)) {
                return redirectUri;
            } else {
                throw new IllegalStateException("Valid redirectUri has not been provided");
            }

        } else {
            return ClientRegistrationConstants.CALLBACK_URL_REGEXP_PREFIX + createRegexPattern(redirectUris);
        }
    }

    private static String createRegexPattern(List<String> redirectURIs) {
        StringBuilder regexPattern = new StringBuilder();
        for (String redirectURI : redirectURIs) {
            if (DCRMUtils.isRedirectionUriValid(redirectURI)) {
                if (regexPattern.length() > 0) {
                    regexPattern.append("|").append(redirectURI);
                } else {
                    regexPattern.append("(").append(redirectURI);
                }
            } else {
                throw new IllegalStateException("Valid redirectUri has not been provided");
            }
        }
        if (regexPattern.length() > 0) {
            regexPattern.append(")");
        }
        return regexPattern.toString();
    }
}
