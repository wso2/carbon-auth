/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.client.registration;

import org.wso2.carbon.auth.client.registration.model.Application;

/**
 * Handles Application related CRUD operations
 */
public interface ClientRegistrationHandler {

    /**
     * Get Application for a given Client ID
     * @param clientId
     * @return Application
     */
    Application getApplication(String clientId);

    /**
     * Register an Application using an application instance
     * @param newApplication
     * @return Application
     */
    Application registerApplication(Application newApplication);

    /**
     * Update an Application using an application instance
     * @param clientId
     * @param modifiedApplication
     * @return Application
     */
    Application updateApplication(String clientId, Application modifiedApplication);

    /**
     * Delete an Application of a given Client ID
     * @param clientId
     */
    void deleteApplication(String clientId);
}
