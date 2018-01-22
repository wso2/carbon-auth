/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.auth.user.store.connector.ldap;

import com.sun.jndi.ldap.LdapCtxFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.Constants;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.exception.LDAPConnectorException;

import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

/**
 * LDAP connection initiation implementation
 */
public class LDAPConnectionContext {
    private static Logger log = LoggerFactory.getLogger(LDAPConnectionContext.class);
    private Hashtable environment;
    protected UserStoreConfiguration userStoreConfig;

    public LDAPConnectionContext(UserStoreConfiguration userStoreConfig) {
        this.userStoreConfig = userStoreConfig;
        environment = new Hashtable();
        Map map = userStoreConfig.getLdapProperties();
        String connectionURL = (String) map.get(Constants.LDAP_CONNECTION_URL);
        String connectionName = (String) map.get(Constants.LDAP_CONNECTION_NAME);
        String connectionPassword = (String) map.get(Constants.LDAP_CONNECTION_PASSWORD);
        String initialContextFactory = (String) map.get(Constants.LDAP_INITIAL_CONTEXT_FACTORY);
        String securityAuthentication = (String) map.get(Constants.LDAP_SECURITY_AUTHENTICATION);

        environment.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        environment.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        if (connectionName != null) {
            environment.put(Context.SECURITY_PRINCIPAL, connectionName);
        }
        if (connectionPassword != null) {
            environment.put(Context.SECURITY_CREDENTIALS, connectionPassword);
        }
        if (connectionURL != null) {
            environment.put(Context.PROVIDER_URL, connectionURL);
        }
    }

    public DirContext getContext() throws LDAPConnectorException {
        DirContext context = null;
        int retry = 5;

        while (retry > 0) {
            try {
                //                context = new InitialDirContext(environment);
                //                context = (DirContext) new LdapCtxFactory().getInitialContext(environment);
                context = LdapCtxFactory.getLdapCtxInstance(environment.get(Context.PROVIDER_URL), environment);
                return context;
            } catch (NamingException e) {
                log.error("Error obtaining connection. " + e.getMessage(), e);
                if (retry == 1) {
                    throw new LDAPConnectorException("Error obtaining connection. " + e.getMessage(), e);
                }
            }
            retry--;
            log.error("Trying again to get connection.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        return context;
    }
}
