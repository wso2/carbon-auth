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

package org.wso2.carbon.auth.client.registration.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.ClientRegistrationHandler;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.model.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of ClientRegistrationHandler Interface
 */
public class ClientRegistrationHandlerImpl implements ClientRegistrationHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationHandlerImpl.class);
    private ApplicationDAO applicationDAO;
    private Map<String, Application> applicationList = new HashMap<>();

    public ClientRegistrationHandlerImpl(ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;
    }

    @Override
    public Application getApplication(String clientId) {
        return applicationList.get(clientId);
    }

    @Override
    public Application registerApplication(Application newApplication) {
        newApplication.setClientId(getRandomString());
        applicationList.put(newApplication.getClientId(), newApplication);

        return newApplication;
    }

    @Override
    public Application updateApplication(String clientId, Application modifiedApplication) {
        applicationList.put(clientId, modifiedApplication);

        return modifiedApplication;
    }

    @Override
    public void deleteApplication(String clientId) {
        applicationList.remove(clientId);
    }

    private String getRandomString() {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 15) { // length of the random string.
            int index = (int) (rnd.nextFloat() * charSet.length());
            stringBuilder.append(charSet.charAt(index));
        }
        String randomString = stringBuilder.toString();
        return randomString;

    }
}
