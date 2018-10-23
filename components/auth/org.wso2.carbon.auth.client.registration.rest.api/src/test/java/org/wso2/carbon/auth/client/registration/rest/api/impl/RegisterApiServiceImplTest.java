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

package org.wso2.carbon.auth.client.registration.rest.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.rest.api.RegisterApi;
import org.wso2.carbon.auth.client.registration.rest.api.dto.ApplicationDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.RegistrationRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.UpdateRequestDTO;
import org.wso2.carbon.auth.core.ServiceReferenceHolder;
import org.wso2.carbon.auth.core.configuration.models.AuthConfiguration;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
import org.wso2.carbon.auth.user.store.configuration.UserStoreConfigurationService;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.config.provider.ConfigProvider;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class RegisterApiServiceImplTest extends AuthDAOIntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(RegisterApiServiceImplTest.class);
    private static final String CLIENT_NAME_1 = "client1";
    private static final String CLIENT_NAME_2 = "client2";
    private static final String REDIRECT_URL_1 = "http://localhost/url1";
    private static final String REDIRECT_URL_2 = "http://localhost/url2";
    private static final String REDIRECT_URL_UPDATED = "http://localhost/updated/url1";
    private static final String GRANT_TYPE = "password";
    private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    private DataSourceService dataSourceService;
    private Request request;

    public RegisterApiServiceImplTest() {
    }

    @BeforeClass
    public void init() throws Exception {
        super.init();
        log.info("Data sources initialized");
    }

    @BeforeMethod
    public void setup() throws Exception {
        super.setup();
        log.info("Created databases");
        dataSourceService = Mockito.mock(DataSourceService.class);
        request = Mockito.mock(Request.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic YWRtaW46YWRtaW4=");
        Mockito.when(dataSourceService.getDataSource("WSO2_UM_DB")).thenReturn(this.umDataSource.getDatasource());

        UserStoreConfiguration userStoreConfiguration = new UserStoreConfiguration();
        AuthConfiguration authConfig = new AuthConfiguration();
        UserStoreConfigurationService userStoreConfigurationService = new UserStoreConfigurationService(
                userStoreConfiguration);

        ConfigProvider configProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(configProvider.getConfigurationObject(AuthConfiguration.class)).thenReturn(authConfig);
        ServiceReferenceHolder.getInstance().setConfigProvider(configProvider);
        org.wso2.carbon.auth.user.mgt.internal.ServiceReferenceHolder.getInstance()
                .setUserStoreConfigurationService(userStoreConfigurationService);

        org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder.getInstance()
                .setUserStoreConfigurationService(userStoreConfigurationService);
        org.wso2.carbon.auth.user.store.internal.ServiceReferenceHolder.getInstance()
                .setDataSourceService(dataSourceService);

    }

    @AfterClass
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }

    @Test
    public void testRegisterGetUpdateDeleteApplication() throws Exception {
        try {
            RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO();
            registrationRequestDTO.setClientName(CLIENT_NAME_1);
            registrationRequestDTO.addRedirectUrisItem(REDIRECT_URL_1);
            registrationRequestDTO.addGrantTypesItem(GRANT_TYPE);
            registrationRequestDTO.addGrantTypesItem(AUTHORIZATION_CODE_GRANT_TYPE);
            RegisterApi registerApi = new RegisterApi();
            Response registrationResponse = registerApi.registerApplication(registrationRequestDTO, request);

            Assert.assertNotNull(registrationResponse);
            Assert.assertNotNull(registrationResponse.getEntity());
            Assert.assertEquals(registrationResponse.getStatus(), Response.Status.CREATED.getStatusCode());
            Assert.assertTrue(registrationResponse.getEntity() instanceof ApplicationDTO);
            ApplicationDTO responseDTO = (ApplicationDTO) registrationResponse.getEntity();
            Assert.assertEquals(responseDTO.getClientName(), CLIENT_NAME_1);
            Assert.assertTrue(responseDTO.getRedirectUris().contains(REDIRECT_URL_1));

            String clientId = responseDTO.getClientId();

            Assert.assertTrue(StringUtils.isNotBlank(responseDTO.getClientId()));
            Assert.assertTrue(StringUtils.isNotBlank(responseDTO.getClientSecret()));

            Response getResponse = registerApi.getApplication(clientId, null);
            Assert.assertNotNull(getResponse);
            Assert.assertEquals(getResponse.getStatus(), Response.Status.OK.getStatusCode());
            Assert.assertNotNull(getResponse.getEntity());
            Assert.assertTrue(getResponse.getEntity() instanceof ApplicationDTO);
            ApplicationDTO getResponseDTO = (ApplicationDTO) getResponse.getEntity();
            Assert.assertEquals(getResponseDTO.getClientName(), CLIENT_NAME_1);

            UpdateRequestDTO updateRequestDTO = new UpdateRequestDTO();
            updateRequestDTO.setClientName(CLIENT_NAME_1);
            updateRequestDTO.addRedirectUrisItem(REDIRECT_URL_UPDATED);
            updateRequestDTO.addGrantTypesItem(AUTHORIZATION_CODE_GRANT_TYPE);
            Response updateResponse = registerApi.updateApplication(updateRequestDTO, clientId, null);
            Assert.assertNotNull(updateResponse);
            Assert.assertEquals(updateResponse.getStatus(), Response.Status.OK.getStatusCode());
            Assert.assertNotNull(updateResponse.getEntity());
            Assert.assertTrue(updateResponse.getEntity() instanceof ApplicationDTO);
            ApplicationDTO updateResponseDTO = (ApplicationDTO) updateResponse.getEntity();
            Assert.assertEquals(updateResponseDTO.getClientName(), CLIENT_NAME_1);
            Assert.assertTrue(updateResponseDTO.getRedirectUris().contains(REDIRECT_URL_UPDATED));

            Response deleteResponse = registerApi.deleteApplication(clientId, null);
            Assert.assertEquals(deleteResponse.getStatus(), Response.Status.NO_CONTENT.getStatusCode());
        } catch (Exception e) {
            log.error("Error while running testRegisterApplication()", e);
            throw e;
        }
    }

    @Test
    public void testRegisterApplicationWithMultipleRedirectUrls() throws Exception {
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO();
        registrationRequestDTO.setClientName(CLIENT_NAME_2);
        registrationRequestDTO.addRedirectUrisItem(REDIRECT_URL_1);
        registrationRequestDTO.addRedirectUrisItem(REDIRECT_URL_2);
        registrationRequestDTO.addGrantTypesItem(GRANT_TYPE);
        registrationRequestDTO.addGrantTypesItem(AUTHORIZATION_CODE_GRANT_TYPE);

        RegisterApi registerApi = new RegisterApi();
        Response registrationResponse = registerApi.registerApplication(registrationRequestDTO, request);

        Assert.assertNotNull(registrationResponse);
        Assert.assertNotNull(registrationResponse.getEntity());
        Assert.assertEquals(registrationResponse.getStatus(), Response.Status.CREATED.getStatusCode());
        Assert.assertTrue(registrationResponse.getEntity() instanceof ApplicationDTO);
        ApplicationDTO responseDTO = (ApplicationDTO) registrationResponse.getEntity();
        Assert.assertEquals(responseDTO.getClientName(), CLIENT_NAME_2);
        Assert.assertEquals(responseDTO.getRedirectUris().size(), 2);
        Assert.assertTrue(responseDTO.getRedirectUris().contains(REDIRECT_URL_1));
        Assert.assertTrue(responseDTO.getRedirectUris().contains(REDIRECT_URL_2));
    }
}
