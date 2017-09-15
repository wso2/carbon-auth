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

package org.wso2.carbon.auth.client.registration.dao;

import org.wso2.carbon.auth.client.registration.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.model.Application;

import java.util.List;
import javax.annotation.CheckForNull;

public interface ApplicationDAO {

    /**
     * Retrieve a given instance of an Application
     *
     * @param clientId   The UUID that uniquely identifies an Application
     * @return valid {@link Application} object or null
     * @throws ClientRegistrationDAOException   If failed to get application.
     */
    @CheckForNull
    Application getApplication(String clientId) throws ClientRegistrationDAOException;

    /**
     * Delete an Application
     *
     * @param clientId Client ID of the application.
     * @throws ClientRegistrationDAOException   If failed to get application.
     */
    @CheckForNull
    void deleteApplication(String clientId) throws ClientRegistrationDAOException;

    /**
     * Creates an Application with the given instance.
     *
     * @param application application object to be created
     * @throws ClientRegistrationDAOException   If failed to get applications.
     */
    Application createApplication(Application application) throws ClientRegistrationDAOException;

    /**
     * Updates an Application with the given instance.
     *
     * @param application application object to be updated
     * @throws ClientRegistrationDAOException   If failed to get applications.
     */
    Application updateApplication(Application application) throws ClientRegistrationDAOException;

}
