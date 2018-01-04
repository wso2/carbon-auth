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

public class UpdateRequestDTOTest {
    private static final String CLIENT_NAME = "ClientName";
    private static final String GRANT_TYPE = "password";
    private static final String REDIRECT_URI = "http://localhost/reirect";
    private static List<String> grants = new ArrayList<>();
    private static List<String> redirects = new ArrayList<>();

    static {
        grants.add(GRANT_TYPE);
        redirects.add(REDIRECT_URI);
    }

    @Test
    public void testUpdateRequestFromSetters() {
        UpdateRequestDTO updateRequestDTO = getSampleUpdateRequestFormSetters();
        assertSampleUpdateRequest(updateRequestDTO);
    }

    @Test
    public void testUpdateRequestFromBuilderPattern() {
        UpdateRequestDTO updateRequestDTO = getSampleUpdateRequestFormBuilderPattern();
        assertSampleUpdateRequest(updateRequestDTO);
    }

    @Test
    public void testUpdateRequestEquality() {
        UpdateRequestDTO updateRequestDTOFromSetters = getSampleUpdateRequestFormBuilderPattern();
        UpdateRequestDTO updateRequestDTOFromBuilderPattern = getSampleUpdateRequestFormBuilderPattern();
        Assert.assertEquals(updateRequestDTOFromSetters, updateRequestDTOFromBuilderPattern);
        Assert.assertEquals(updateRequestDTOFromSetters.hashCode(), updateRequestDTOFromBuilderPattern.hashCode());
    }

    private UpdateRequestDTO getSampleUpdateRequestFormSetters() {
        UpdateRequestDTO updateRequestDTO = new UpdateRequestDTO();
        updateRequestDTO.setClientName(CLIENT_NAME);
        updateRequestDTO.setGrantTypes(grants);
        updateRequestDTO.setRedirectUris(redirects);
        return updateRequestDTO;
    }

    private UpdateRequestDTO getSampleUpdateRequestFormBuilderPattern() {
        return new UpdateRequestDTO().clientName(CLIENT_NAME).grantTypes(grants)
                .redirectUris(redirects);
    }

    private void assertSampleUpdateRequest(UpdateRequestDTO updateRequestDTO) {
        Assert.assertEquals(updateRequestDTO.getClientName(), CLIENT_NAME);
        Assert.assertTrue(updateRequestDTO.getGrantTypes().contains(GRANT_TYPE));
        Assert.assertTrue(updateRequestDTO.getRedirectUris().contains(REDIRECT_URI));

        Assert.assertTrue(updateRequestDTO.toString().contains(CLIENT_NAME));
    }
}
