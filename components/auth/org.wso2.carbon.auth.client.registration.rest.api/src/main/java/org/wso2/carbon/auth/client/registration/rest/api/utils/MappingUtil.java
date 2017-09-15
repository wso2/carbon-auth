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

import org.wso2.carbon.auth.client.registration.model.Application;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ApplicationDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.RegistrationRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.UpdateRequestDTO;

/**
 * Utility class for mapping rest api DTOs to Models
 */
public class MappingUtil {
    /**
     * This method convert the Dto object into Model
     *
     * @param Application Model instance with application data
     * @return ApplicationDTO Contains data of an application
     */
    public static ApplicationDTO applicationModelToApplicationDTO(Application application) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setClientName(application.getClientName());
        applicationDTO.setClientSecret(application.getClientSecret());
        applicationDTO.setClientId(application.getClientId());
        applicationDTO.setRedirectUris(application.getRedirectUris());

        return applicationDTO;
    }

    /**
     * This method convert the Dto object into Model
     *
     * @param RegistrationRequestDTO Contains data of an application
     * @return Application model instance with application data
     */
    public static Application registrationRequestToApplication(RegistrationRequestDTO registrationRequestDTO) {
        Application newApplication = new Application();
        newApplication.setClientName(registrationRequestDTO.getClientName());
        newApplication.setRedirectUris(registrationRequestDTO.getRedirectUris());
        newApplication.setGrantTypes(registrationRequestDTO.getGrantTypes());

        return newApplication;
    }

    /**
     * This method convert the Dto object into Model
     *
     * @param UpdateRequestDTO Contains data of an application
     * @return Application model instance with application data
     */
    public static Application updateRequestToApplication(UpdateRequestDTO updateRequestDTO) {
        Application updatedApplication = new Application();
        updatedApplication.setClientName(updateRequestDTO.getClientName());
        updatedApplication.setRedirectUris(updateRequestDTO.getRedirectUris());
        updatedApplication.setGrantTypes(updateRequestDTO.getGrantTypes());

        return updatedApplication;
    }
}
