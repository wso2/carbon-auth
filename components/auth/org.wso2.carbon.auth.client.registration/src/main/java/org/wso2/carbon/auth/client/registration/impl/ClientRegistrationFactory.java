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

package org.wso2.carbon.auth.client.registration.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.dao.impl.DAOFactory;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationException;

/**
 * Creates API Producers and API Consumers.
 */
public class ClientRegistrationFactory {

    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationFactory.class);

    private static final ClientRegistrationFactory instance = new ClientRegistrationFactory();

    private ClientRegistrationHandler defaultClientRegistrationHandler;

    private ClientRegistrationFactory() {

    }

    /**
     * Get APIManagerFactory instance
     *
     * @return APIManagerFactory object
     */
    public static ClientRegistrationFactory getInstance() {
        return instance;
    }

    /**
     * Get Default Client Registration Handler
     * 
     * @return Default client registration handler
     */
    public ClientRegistrationHandler getClientRegistrationHandler() throws ClientRegistrationException {
        if (defaultClientRegistrationHandler == null) {
            try {
                defaultClientRegistrationHandler = new ClientRegistrationHandlerImpl(DAOFactory.getApplicationDAO());
            } catch (ClientRegistrationDAOException e) {
                throw new ClientRegistrationException("Error occurred while initializing ClientRegistrationHandler", e);
            }
        }
        return defaultClientRegistrationHandler;
    }

}
