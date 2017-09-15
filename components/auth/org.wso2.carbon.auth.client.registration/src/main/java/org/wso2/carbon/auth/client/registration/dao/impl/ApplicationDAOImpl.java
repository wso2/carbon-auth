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

package org.wso2.carbon.auth.client.registration.dao.impl;

import org.wso2.carbon.auth.client.registration.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.model.Application;

/**
 * Default implementation of the ApplicationDAO interface. Uses SQL syntax that is common to H2 and MySQL DBs.
 * Hence is considered as the default due to its re-usability.
 */
public class ApplicationDAOImpl implements ApplicationDAO{

    @Override
    public Application getApplication(String clientId) throws ClientRegistrationDAOException {
        return null;
    }

    @Override
    public void deleteApplication(String clientId) throws ClientRegistrationDAOException {

    }

    @Override
    public Application createApplication(Application application) throws ClientRegistrationDAOException {
        return null;
    }

    @Override
    public Application updateApplication(Application application) throws ClientRegistrationDAOException {
        return null;
    }
}
