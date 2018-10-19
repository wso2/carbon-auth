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

package org.wso2.carbon.auth.user.store.claim;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.auth.user.store.claim.model.AttributeMapping;
import org.wso2.carbon.auth.user.store.claim.model.ExternalClaim;
import org.wso2.carbon.auth.user.store.claim.model.LocalClaim;
import org.wso2.carbon.auth.user.store.exception.StoreException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default claim manager implementation
 */
public class DefaultClaimManager {

    private ArrayList<LocalClaim> localClaimsList = new ArrayList<>();
    private Map<String, ArrayList<ExternalClaim>> dialectList = new HashMap();
    private static DefaultClaimManager instance = new DefaultClaimManager();

    private DefaultClaimManager() throws StoreException {
        try {
            loadClaims();
        } catch (Exception e) {
            throw new StoreException("Error occurred while loading default claims.", e);
        }
    }

    public static DefaultClaimManager getInstance() {
        return instance;
    }

    public List<LocalClaim> getLocalClaims() throws StoreException {
        return localClaimsList;
    }

    public List<ExternalClaim> getExternalClaims(String externalDialectURI) throws StoreException {

        if (dialectList.containsKey(externalDialectURI)) {
            return dialectList.get(externalDialectURI);
        }
        throw new StoreException("External claim dialect not found.");
    }

    public Map<String, ArrayList<ExternalClaim>> getDialectList() {
        return dialectList;
    }

    public void loadClaims() throws Exception {
        String claimConfigFilePath =
                System.getProperty(ClaimConstants.CARBON_RUNTIME_DIR_PROP_NAME) + ClaimConstants.CLAIM_CONFIG_FILE_PATH;
        String json = new String(Files.readAllBytes(Paths.get(claimConfigFilePath)), Charset.defaultCharset());
        JSONObject dialects = new JSONObject(json);
        JSONArray dialectsObj = dialects.getJSONArray(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECTS);

        ArrayList<ExternalClaim> externalClaimsList;
        for (int i = 0; i < dialectsObj.length(); i++) {
            JSONObject dialect = dialectsObj.getJSONObject(i);
            JSONArray claims = dialect.getJSONArray(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM);
            String dialectName = dialect.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_URI);
            externalClaimsList = new ArrayList<>();

            for (int j = 0; j < claims.length(); j++) {
                JSONObject claim = claims.getJSONObject(j);
                String claimURI = claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_URI);
                String displayName = claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DISPLAY_NAME);
                String attributeID = claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_ATTRIBUTE_ID);
                String description = claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DESCRIPTION);

                Map<String, String> claimProperties = new HashMap<>();
                claimProperties.put(ClaimConstants.SCIM_CLAIM_PROP_CLAIM_DESCRIPTION, description);
                claimProperties.put(ClaimConstants.SCIM_CLAIM_PROP_CLAIM_DISPLAY_NAME, displayName);

                if (claim.has(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DISPLAY_ORDER)) {
                    claimProperties.put(ClaimConstants.SCIM_CLAIM_PROP_CLAIM_DISPLAY_ORDER,
                            claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_DISPLAY_ORDER));
                }
                if (claim.has(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_REQUIRED)) {
                    claimProperties.put(ClaimConstants.SCIM_CLAIM_PROP_CLAIM_REQUIRED,
                            claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_REQUIRED));
                }
                if (claim.has(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_SUPPORTED_BY_DEFAULT)) {
                    claimProperties.put(ClaimConstants.SCIM_CLAIM_PROP_CLAIM_SUPPORTED_BY_DEFAULT,
                            claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_SUPPORTED_BY_DEFAULT));
                }

                if (ClaimConstants.DEFAULT_CARBON_DIALECT.equals(dialectName)) {
                    List<AttributeMapping> attributeMappings = new ArrayList<>();
                    AttributeMapping attributeMapping =
                            new AttributeMapping(ClaimConstants.PRIMARY_DEFAULT_DOMAIN_NAME, attributeID);
                    attributeMappings.add(attributeMapping);
                    LocalClaim localClaim = new LocalClaim(claimURI, attributeMappings, claimProperties);
                    localClaimsList.add(localClaim);
                } else {
                    ExternalClaim externalClaim = new ExternalClaim(dialectName, claimURI,
                            claim.getString(ClaimConstants.SCIM_CLAIM_CONFIG_DIALECT_CLAIM_MAPPED_LOCAL_CLAIM));
                    externalClaimsList.add(externalClaim);
                }
            }
            dialectList.put(dialectName, externalClaimsList);
        }
    }

}
