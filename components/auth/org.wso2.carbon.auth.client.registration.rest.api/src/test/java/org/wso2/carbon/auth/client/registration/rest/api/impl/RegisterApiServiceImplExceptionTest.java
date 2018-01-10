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

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.rest.api.RegisterApi;
import org.wso2.carbon.auth.client.registration.rest.api.dto.RegistrationRequestDTO;
import org.wso2.carbon.auth.client.registration.rest.api.dto.UpdateRequestDTO;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
import org.wso2.carbon.auth.user.store.internal.ConnectorDataHolder;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

public class RegisterApiServiceImplExceptionTest extends AuthDAOIntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(RegisterApiServiceImplExceptionTest.class);
    private static final String DUMMY_CLIENT_ID = "some_dummy_client_id";
    private static final String CLIENT_NAME = "client1";
    private static final String REDIRECT_URL = "http://localhost/url1";
    private static final String GRANT_TYPE = "password";
    private DataSourceService dataSourceService;
    private Request request;

    public RegisterApiServiceImplExceptionTest() {
    }

    @BeforeClass
    public void init() throws Exception {
        super.init();
        log.info("Data sources initialized");
    }

    @BeforeMethod
    public void setupWithoutTables() throws Exception {
        //to make every sql execution throws exception
        super.setupWithoutTables();
        log.info("Created databases without any tables");
        dataSourceService = Mockito.mock(DataSourceService.class);
        request = Mockito.mock(Request.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic YWRtaW46YWRtaW4=");
        Mockito.when(dataSourceService.getDataSource("WSO2_UM_DB")).thenReturn(this.umDataSource.getDatasource());
        ConnectorDataHolder.getInstance().setDataSourceService(dataSourceService);
    }

    @AfterClass
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }

    @Test
    public void testRegisterApplicationUnsuccessful() throws Exception {
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO();
        registrationRequestDTO.setClientName(CLIENT_NAME);
        registrationRequestDTO.addRedirectUrisItem(REDIRECT_URL);
        registrationRequestDTO.addGrantTypesItem(GRANT_TYPE);

        RegisterApi registerApi = new RegisterApi();
        Response registrationResponse = registerApi.registerApplication(registrationRequestDTO, request);
        Assert.assertNotNull(registrationResponse);
        Assert.assertEquals(registrationResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testGetApplicationUnsuccessful() throws Exception {
        RegisterApi registerApi = new RegisterApi();
        Response getResponse = registerApi.getApplication(DUMMY_CLIENT_ID, null);
        Assert.assertNotNull(getResponse);
        Assert.assertEquals(getResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testUpdateApplicationUnsuccessful() throws Exception {
        RegisterApi registerApi = new RegisterApi();

        UpdateRequestDTO updateRequestDTO = new UpdateRequestDTO();
        updateRequestDTO.setClientName(CLIENT_NAME);
        updateRequestDTO.addRedirectUrisItem(REDIRECT_URL);
        updateRequestDTO.addGrantTypesItem(GRANT_TYPE);

        Response updateResponse = registerApi.updateApplication(updateRequestDTO, DUMMY_CLIENT_ID, null);
        Assert.assertNotNull(updateResponse);
        Assert.assertEquals(updateResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testDeleteApplicationUnsuccessful() throws Exception {
        RegisterApi registerApi = new RegisterApi();
        Response deleteResponse = registerApi.deleteApplication(DUMMY_CLIENT_ID, null);
        Assert.assertNotNull(deleteResponse);
        Assert.assertEquals(deleteResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

    }
}
