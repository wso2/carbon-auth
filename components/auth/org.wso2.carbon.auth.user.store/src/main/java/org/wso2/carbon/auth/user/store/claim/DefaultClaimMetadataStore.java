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

import org.wso2.carbon.auth.user.store.claim.api.ClaimMapping;
import org.wso2.carbon.auth.user.store.claim.model.Claim;
import org.wso2.carbon.auth.user.store.claim.model.ExternalClaim;
import org.wso2.carbon.auth.user.store.claim.model.LocalClaim;
import org.wso2.carbon.auth.user.store.exception.StoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default claim metadata store. Contain the default dialects.
 */
public class DefaultClaimMetadataStore implements ClaimMetadataStore {

    private DefaultClaimManager defaultClaimManager;

    public DefaultClaimMetadataStore(DefaultClaimManager defaultClaimManager) {
        this.defaultClaimManager = defaultClaimManager;
    }

    @Override
    public String getAttributeName(String claimURI) throws StoreException {
        return getAttributeName(ClaimConstants.PRIMARY_DEFAULT_DOMAIN_NAME, claimURI);
    }

    @Override
    public Claim getClaim(String var1) throws StoreException {
        // not yet implemented.
        return null;
    }

    @Override
    public ClaimMapping getClaimMapping(String claimURI) throws StoreException {
        try {
            List<LocalClaim> localClaims = defaultClaimManager.getLocalClaims();

            for (LocalClaim localClaim : localClaims) {
                if (localClaim.getClaimURI().equalsIgnoreCase(claimURI)) {
                    ClaimMapping claimMapping = ClaimMetadataUtils.convertLocalClaimToClaimMapping(localClaim);
                    return claimMapping;
                }
            }

            // For backward compatibility
            Map<String, ArrayList<ExternalClaim>> claimDialects = DefaultClaimManager.getInstance().getDialectList();
            for (Map.Entry<String, ArrayList<ExternalClaim>> entry : claimDialects.entrySet()) {
                if (ClaimConstants.LOCAL_CLAIM_DIALECT_URI.equalsIgnoreCase(entry.getKey())) {
                    continue;
                }
                List<ExternalClaim> externalClaims =
                        DefaultClaimManager.getInstance().getExternalClaims(entry.getKey());
                for (ExternalClaim externalClaim : externalClaims) {
                    if (externalClaim.getClaimURI().equalsIgnoreCase(claimURI)) {
                        for (LocalClaim localClaim : localClaims) {
                            if (localClaim.getClaimURI().equalsIgnoreCase(externalClaim.getMappedLocalClaim())) {
                                ClaimMapping claimMapping =
                                        ClaimMetadataUtils.convertLocalClaimToClaimMapping(localClaim);
                                return claimMapping;
                            }
                        }
                    }
                }
            }
            return null;
        } catch (StoreException e) {
            throw new StoreException("Error occured while getting Claim mapping information for claim: " + claimURI, e);
        }
    }

    @Override
    public ClaimMapping[] getAllSupportClaimMappingsByDefault() throws StoreException {
        // not yet implemented.
        return new ClaimMapping[0];
    }

    @Override
    public ClaimMapping[] getAllClaimMappings() throws StoreException {
        // not yet implemented.
        return new ClaimMapping[0];
    }

    @Override
    public ClaimMapping[] getAllClaimMappings(String dialectUri) throws StoreException {
        if (ClaimConstants.LOCAL_CLAIM_DIALECT_URI.equalsIgnoreCase(dialectUri)) {
            try {
                List<LocalClaim> localClaims = defaultClaimManager.getLocalClaims();

                List<ClaimMapping> claimMappings = new ArrayList<>();

                for (LocalClaim localClaim : localClaims) {
                    ClaimMapping claimMapping = ClaimMetadataUtils.convertLocalClaimToClaimMapping(localClaim);
                    claimMappings.add(claimMapping);
                }

                return claimMappings.toArray(new ClaimMapping[0]);
            } catch (StoreException e) {
                throw new StoreException("Error occurred while getting all local claims.", e);
            }
        } else {
            try {
                List<ExternalClaim> externalClaims = defaultClaimManager.getExternalClaims(dialectUri);
                List<LocalClaim> localClaims = defaultClaimManager.getLocalClaims();

                List<ClaimMapping> claimMappings = new ArrayList<>();

                for (ExternalClaim externalClaim : externalClaims) {
                    ClaimMapping claimMapping =
                            ClaimMetadataUtils.convertExternalClaimToClaimMapping(externalClaim, localClaims);
                    claimMappings.add(claimMapping);
                }

                return claimMappings.toArray(new ClaimMapping[0]);
            } catch (StoreException e) {
                throw new StoreException("Error occurred while getting all external claims.", e);
            }
        }
    }

    @Override
    public ClaimMapping[] getAllRequiredClaimMappings() throws StoreException {
        // not yet implemented.
        return new ClaimMapping[0];
    }

    @Override
    public String[] getAllClaimUris() throws StoreException {
        // not yet implemented.
        return new String[0];
    }

    @Override
    public void addNewClaimMapping(ClaimMapping var1) throws StoreException {
        // not yet implemented.
    }

    @Override
    public void deleteClaimMapping(ClaimMapping var1) throws StoreException {
        // not yet implemented.
    }

    @Override
    public void updateClaimMapping(ClaimMapping var1) throws StoreException {
        // not yet implemented.
    }

    @Override
    public String getAttributeName(String domainName, String claimURI) throws StoreException {
        List<LocalClaim> localClaimList = this.defaultClaimManager.getLocalClaims();

        for (LocalClaim localClaim : localClaimList) {
            if (localClaim.getClaimURI().equalsIgnoreCase(claimURI)) {
                return getMappedAttribute(ClaimConstants.PRIMARY_DEFAULT_DOMAIN_NAME, localClaim);
            }
        }

        // For backward compatibility
        Map<String, ArrayList<ExternalClaim>> claimDialects = DefaultClaimManager.getInstance().getDialectList();

        for (Map.Entry<String, ArrayList<ExternalClaim>> entry : claimDialects.entrySet()) {
            if (ClaimConstants.LOCAL_CLAIM_DIALECT_URI.equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            List<ExternalClaim> externalClaims = DefaultClaimManager.getInstance().getExternalClaims(entry.getKey());
            for (ExternalClaim externalClaim : externalClaims) {
                if (externalClaim.getClaimURI().equalsIgnoreCase(claimURI)) {

                    for (LocalClaim localClaim : localClaimList) {
                        if (localClaim.getClaimURI().equalsIgnoreCase(externalClaim.getMappedLocalClaim())) {
                            return getMappedAttribute(domainName, localClaim);
                        }
                    }

                }
            }
        }

        return null;
    }

    private String getMappedAttribute(String domainName, LocalClaim localClaim) throws StoreException {
        return localClaim.getMappedAttribute(domainName);
    }

}
