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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.auth.core.Constants;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.exception.LDAPConnectorException;

import java.util.HashMap;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LdapCtxFactory.class })
public class LDAPConnectionContextTest {

    @Test
    public void testInit() throws Exception {
        HashMap ldapProperties = new HashMap();
        ldapProperties.put(Constants.LDAP_CONNECTOR_CLASS,
                "org.wso2.carbon.auth.user.store.connector.ldap.LDAPUserStoreConnector");
        ldapProperties.put(Constants.LDAP_CONNECTION_URL, "ldap://localhost:10389");
        ldapProperties.put(Constants.LDAP_CONNECTION_NAME, "uid=admin,ou=system");
        ldapProperties.put(Constants.LDAP_CONNECTION_PASSWORD, "admin");
        ldapProperties.put(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapProperties.put(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
        ldapProperties.put(Constants.LDAP_USER_SEARCH_BASE, "ou=Users,dc=wso2,dc=org");
        ldapProperties.put(Constants.LDAP_USER_ENTRY_OBJECT_CLASS, "identityPerson");
        ldapProperties.put(Constants.LDAP_USERNAME_ATTRIBUTE, "uid");
        ldapProperties.put(Constants.LDAP_USERNAME_SEARCH_FILTER, "(&amp;(objectClass=person)(uid=?))");
        ldapProperties.put(Constants.LDAP_USERNAME_LIST_FILTER, "(objectClass=person)");

        UserStoreConfiguration configuration = new UserStoreConfiguration();
        LDAPConnectionContext context = new LDAPConnectionContext(configuration);
        Assert.assertNotNull(context);

        configuration.getLdapProperties().put(Constants.LDAP_CONNECTION_URL, null);
        configuration.getLdapProperties().put(Constants.LDAP_CONNECTION_NAME, null);
        configuration.getLdapProperties().put(Constants.LDAP_CONNECTION_PASSWORD, null);
        context = new LDAPConnectionContext(configuration);
        Assert.assertNotNull(context);
    }

    @Test
    public void testGetContext() throws Exception {
        PowerMockito.mockStatic(LdapCtxFactory.class);

        DirContext context = Mockito.mock(DirContext.class);
        Mockito.when(LdapCtxFactory.getLdapCtxInstance(Mockito.any(), Mockito.any())).thenReturn(context);
        UserStoreConfiguration configuration = new UserStoreConfiguration();
        LDAPConnectionContext connectionContext = new LDAPConnectionContext(configuration);
        DirContext dirContext = connectionContext.getContext();
        Assert.assertNotNull(dirContext);

        Mockito.when(LdapCtxFactory.getLdapCtxInstance(Mockito.any(), Mockito.any())).thenThrow(NamingException.class);
        try {
            connectionContext.getContext();
            Assert.fail("Exception expected");
        } catch (LDAPConnectorException e) {
            Assert.assertTrue(e.getMessage().contains("Error obtaining connection"));
        }
    }

}
