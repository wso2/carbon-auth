/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.scim.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;

/**
 * OSGi component for carbon security connectors.
 *
 * @since 1.0.0
 */
@Component(
        name = "org.wso2.carbon.auth.user.mgt",
        immediate = true
)
public class SCIMComponent {

    private static final Logger log = LoggerFactory.getLogger(SCIMComponent.class);

    @Reference(
            name = "org.wso2.carbon.auth.user.store",
            service = UserStoreConfigurationService.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterUserStoreConfigurationService"
    )
    protected void registerUserStoreConfigurationService(UserStoreConfigurationService service) {
        ServiceReferenceHolder.getInstance().setUserStoreConfigurationService(service);

        if (log.isDebugEnabled()) {
            log.debug("User store configuration service registered successfully.");
        }
    }

    protected void unregisterUserStoreConfigurationService(UserStoreConfigurationService service) {
        ServiceReferenceHolder.getInstance().setUserStoreConfigurationService(null);

        if (log.isDebugEnabled()) {
            log.debug("User store configuration service unregistered.");
        }
    }
}
