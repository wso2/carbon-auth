/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.user.store.internal;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.carbon.auth.user.store.util.UserStoreUtil;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.util.Map;

/**
 * OSGi component for carbon security connectors.
 *
 * @since 1.0.0
 */
@Component(
        name = "org.wso2.carbon.auth.user.store",
        immediate = true
)
public class UserStoreComponent {

    private static final Logger log = LoggerFactory.getLogger(UserStoreComponent.class);
    private ServiceRegistration registration;

    @Reference(
            name = "org.wso2.carbon.datasource.DataSourceService",
            service = DataSourceService.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterDataSourceService"
    )
    protected void registerDataSourceService(DataSourceService service, Map<String, String> properties) {
        ServiceReferenceHolder.getInstance().setDataSourceService(service);

        if (log.isDebugEnabled()) {
            log.debug("Data source service registered successfully.");
        }
    }

    protected void unregisterDataSourceService(DataSourceService service) {
        ServiceReferenceHolder.getInstance().setDataSourceService(null);

        if (log.isDebugEnabled()) {
            log.debug("Data source service unregistered.");
        }
    }

    /**
     * Get the ConfigProvider service.
     * This is the bind method that gets called for ConfigProvider service registration that satisfy the policy.
     *
     * @param configProvider the ConfigProvider service that is registered as a service.
     */
    @Reference(name = "carbon.config.provider", service = ConfigProvider.class,
            cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterConfigProvider")
    protected void registerConfigProvider(ConfigProvider configProvider) {
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
    }

    /**
     * This is the unbind method for the above reference that gets called for ConfigProvider instance un-registrations.
     *
     * @param configProvider the ConfigProvider service that get unregistered.
     */
    protected void unregisterConfigProvider(ConfigProvider configProvider) {
        ServiceReferenceHolder.getInstance().setConfigProvider(null);
    }

    @Activate
    protected void activate(ComponentContext componentContext)
            throws ConfigurationException, UserStoreConnectorException {
        ConfigProvider configProvider = ServiceReferenceHolder.getInstance().getConfigProvider();
        UserStoreConfiguration config = configProvider.getConfigurationObject(UserStoreConfiguration.class);
        UserStoreConfigurationService userStoreConfigurationService = new UserStoreConfigurationService(config);
        ServiceReferenceHolder.getInstance().setUserStoreConfigurationService(userStoreConfigurationService);
        registration = componentContext.getBundleContext().registerService(
                UserStoreConfigurationService.class.getName(),
                userStoreConfigurationService, null);

        //adding admin user
        UserStoreUtil.addAdminUser(config);
    }

    @Deactivate
    protected void deactivate() {
        registration.unregister();
    }
    
/*    *//**
     * Register user store connectors as OSGi services.
     *
     * @param bundleContext Bundle Context.
     *//*
    @Activate
    public void registerCarbonSecurityConnectors(BundleContext bundleContext) {

        Dictionary<String, String> connectorProperties = new Hashtable<>();

        connectorProperties.put("connector-type", "JDBCIdentityStore");
        bundleContext.registerService(IdentityStoreConnectorFactory.class, new JDBCIdentityStoreConnectorFactory(),
                connectorProperties);

        connectorProperties = new Hashtable<>();
        connectorProperties.put("connector-type", "JDBCCredentialStore");
        bundleContext.registerService(CredentialStoreConnectorFactory.class, new JDBCCredentialStoreConnectorFactory(),
                connectorProperties);

//        connectorProperties = new Hashtable<>();
//        connectorProperties.put("connector-type", "JDBCAuthorizationStore");
//        bundleContext.registerService(AuthorizationStoreConnectorFactory.class,
//                                      new JDBCAuthorizationStoreConnectorFactory(), connectorProperties);

        log.info("JDBC user store bundle successfully activated.");
    }*/

    /*@Reference(
            name = "org.wso2.carbon.identity.mgt.util.PasswordHandler",
            service = PasswordHandler.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterPasswordHandler"
    )
    protected void registerPasswordHandler(PasswordHandler passwordHandler, Map<String, String> properties) {

        if (passwordHandler != null) {
            ConnectorDataHolder.getInstance().setPasswordHandler(
                    properties.get(IdentityMgtConstants.PASSWORD_HANDLER_NAME),
                    passwordHandler);
            if (log.isDebugEnabled()) {
                log.debug("Password handler for name {} registered.",
                          properties.get(IdentityMgtConstants.PASSWORD_HANDLER_NAME));
            }
        }
    }

    protected void unregisterPasswordHandler(PasswordHandler passwordHandler, Map<String, String> properties) {

        ConnectorDataHolder.getInstance().setPasswordHandler(
                properties.get(IdentityMgtConstants.PASSWORD_HANDLER_NAME), null);

        if (log.isDebugEnabled()) {
            log.debug("Password handler for name {} unregistered.",
                      properties.get(IdentityMgtConstants.PASSWORD_HANDLER_NAME));
        }
    }*/
}
