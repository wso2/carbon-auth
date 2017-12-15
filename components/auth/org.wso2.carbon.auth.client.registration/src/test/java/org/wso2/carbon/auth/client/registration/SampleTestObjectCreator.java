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

package org.wso2.carbon.auth.client.registration;

import org.wso2.carbon.auth.client.registration.model.Application;

import java.util.UUID;

/**
 * Creates sample objects used for test cases.
 */
public class SampleTestObjectCreator {

    public static Application createDefaultApplication() {
        Application application = new Application();
        application.setClientName("calculator-app");
        application.setClientId(UUID.randomUUID().toString());
        application.setClientSecret(UUID.randomUUID().toString());
        application.setGrantTypes("password");
        application.setCallBackUrl("http://localhost/callback");
        application.setOauthVersion("2");
        return application;
    }
}
