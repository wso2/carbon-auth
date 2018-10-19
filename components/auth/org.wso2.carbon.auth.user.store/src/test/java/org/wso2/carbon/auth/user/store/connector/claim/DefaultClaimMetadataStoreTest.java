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

package org.wso2.carbon.auth.user.store.connector.claim;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.carbon.auth.user.store.claim.ClaimConstants;
import org.wso2.carbon.auth.user.store.claim.ClaimMetadataStore;
import org.wso2.carbon.auth.user.store.claim.DefaultClaimManager;
import org.wso2.carbon.auth.user.store.claim.DefaultClaimMetadataStore;
import org.wso2.carbon.auth.user.store.claim.api.ClaimMapping;
import org.wso2.carbon.auth.user.store.connector.Constants;
import org.wso2.carbon.auth.user.store.exception.StoreException;

import java.io.File;

public class DefaultClaimMetadataStoreTest {

    private ClaimMetadataStore claimMetadataStore;

    @Before
    public void setup() throws Exception {
        System.setProperty(ClaimConstants.CARBON_RUNTIME_DIR_PROP_NAME,
                System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                        + "resources" + File.separator + "runtime.home" + File.separator);
        DefaultClaimManager defaultClaimManager = DefaultClaimManager.getInstance();
        claimMetadataStore = new DefaultClaimMetadataStore(defaultClaimManager);
    }

    @Test
    public void testGetClaimMapping() throws Exception {
        //check external claim
        ClaimMapping claimMapping = claimMetadataStore.getClaimMapping(Constants.USER_DISPLAY_NAME_URI);
        Assert.assertNotNull(claimMapping);

        //check local claim
        String localClaim = "http://wso2.org/claims/givenname";
        claimMapping = claimMetadataStore.getClaimMapping(localClaim);
        Assert.assertNotNull(claimMapping);

        //check no such claim
        String claim = "http://wso2.org/claims/noSuchClaim";
        claimMapping = claimMetadataStore.getClaimMapping(claim);
        Assert.assertNull(claimMapping);
    }

    @Test
    public void testGetAllClaimMappings() throws Exception {
        //check external claim
        ClaimMapping[] claimMappings;

        //check local claim dialect
        claimMappings = claimMetadataStore.getAllClaimMappings(ClaimConstants.LOCAL_CLAIM_DIALECT_URI);
        Assert.assertNotNull(claimMappings);
        Assert.assertTrue(claimMappings.length > 0);

        //check external claim dialect
        String dialectURI = "urn:scim:schemas:core:1.0";
        claimMappings = claimMetadataStore.getAllClaimMappings(dialectURI);
        Assert.assertNotNull(claimMappings);
        Assert.assertTrue(claimMappings.length > 0);

        //check no such claim dialect
        dialectURI = "urn:empty:schemas:empty:dialect";
        try {
            claimMappings = claimMetadataStore.getAllClaimMappings(dialectURI);
            Assert.fail("StoreException expected when dialect is not exist.");
        } catch (StoreException e) {
            Assert.assertEquals("Error occurred while getting all external claims.", e.getMessage());

        }
    }

    @Test
    public void testGetAttributeName() throws Exception {
        //check external claim
        String attributeName = claimMetadataStore.getAttributeName(Constants.USER_DISPLAY_NAME_URI);
        Assert.assertEquals("displayName", attributeName);

        //check local claim
        String localClaim = "http://wso2.org/claims/givenname";
        attributeName = claimMetadataStore.getAttributeName(localClaim);
        Assert.assertEquals("givenName", attributeName);

        //check no such claim
        String claim = "http://wso2.org/claims/noSuchClaim";
        attributeName = claimMetadataStore.getAttributeName(claim);
        Assert.assertNull(attributeName);
    }
}
