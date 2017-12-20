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

package org.wso2.carbon.auth.client.registration.rest.api.dto;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDTOTest {
    private static final String CLIENT_ID = "ClientId";
    private static final String CLIENT_NAME = "ClientName";
    private static final String CLIENT_SECRET = "Secret";
    private static final String CLIENT_SECRET_EXPIRES_AT = "2020/12/12";
    private static final String REDIRECT_URI = "http://localhost/reirect";

    @Test
    public void testApplicationFromSetters() {
        ApplicationDTO applicationDTO = getSampleApplicationFormSetters();
        assertSampleApplication(applicationDTO);
    }

    @Test
    public void testApplicationFromBuilderPattern() {
        ApplicationDTO applicationDTO = getSampleApplicationFormBuilderPattern();
        assertSampleApplication(applicationDTO);
    }

    @Test
    public void testApplicationEquality() {
        ApplicationDTO applicationDTOFromSetters = getSampleApplicationFormBuilderPattern();
        ApplicationDTO applicationDTOFromBuilderPattern = getSampleApplicationFormBuilderPattern();
        Assert.assertEquals(applicationDTOFromSetters, applicationDTOFromBuilderPattern);
        Assert.assertEquals(applicationDTOFromSetters.hashCode(), applicationDTOFromBuilderPattern.hashCode());
    }

    private ApplicationDTO getSampleApplicationFormSetters() {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setClientId(CLIENT_ID);
        applicationDTO.setClientName(CLIENT_NAME);
        applicationDTO.setClientSecret(CLIENT_SECRET);
        applicationDTO.setClientSecretExpiresAt(CLIENT_SECRET_EXPIRES_AT);
        List<String> uris = new ArrayList<>();
        uris.add(REDIRECT_URI);
        applicationDTO.setRedirectUris(uris);
        return applicationDTO;
    }

    private ApplicationDTO getSampleApplicationFormBuilderPattern() {
        List<String> uris = new ArrayList<>();
        uris.add(REDIRECT_URI);
        return new ApplicationDTO().clientId(CLIENT_ID).clientName(CLIENT_NAME)
                .clientSecret(CLIENT_SECRET).clientSecretExpiresAt(CLIENT_SECRET_EXPIRES_AT).redirectUris(uris);
    }

    private void assertSampleApplication(ApplicationDTO applicationDTO) {
        Assert.assertEquals(applicationDTO.getClientId(), CLIENT_ID);
        Assert.assertEquals(applicationDTO.getClientName(), CLIENT_NAME);
        Assert.assertEquals(applicationDTO.getClientSecret(), CLIENT_SECRET);
        Assert.assertEquals(applicationDTO.getClientSecretExpiresAt(), CLIENT_SECRET_EXPIRES_AT);
        Assert.assertNotNull(applicationDTO.getRedirectUris());
        Assert.assertTrue(applicationDTO.getRedirectUris().contains(REDIRECT_URI));

        Assert.assertTrue(applicationDTO.toString().contains(CLIENT_ID));
        Assert.assertTrue(applicationDTO.toString().contains(CLIENT_NAME));
    }
}