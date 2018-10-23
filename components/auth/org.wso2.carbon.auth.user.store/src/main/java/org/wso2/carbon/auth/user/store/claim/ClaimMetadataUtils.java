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

import org.wso2.carbon.auth.user.store.claim.api.Claim;
import org.wso2.carbon.auth.user.store.claim.api.ClaimMapping;
import org.wso2.carbon.auth.user.store.claim.model.AttributeMapping;
import org.wso2.carbon.auth.user.store.claim.model.ExternalClaim;
import org.wso2.carbon.auth.user.store.claim.model.LocalClaim;
import org.wso2.carbon.auth.user.store.exception.StoreException;

import java.util.List;
import java.util.Map;

/**
 * Claim related util functions
 */
public class ClaimMetadataUtils {
    public static ClaimMapping convertExternalClaimToClaimMapping(ExternalClaim externalClaim,
            List<LocalClaim> localClaims) throws StoreException {

        ClaimMapping claimMapping = null;

        if (localClaims != null) {
            for (LocalClaim localClaim : localClaims) {
                if (externalClaim.getMappedLocalClaim().equalsIgnoreCase(localClaim.getClaimURI())) {
                    claimMapping = convertLocalClaimToClaimMapping(localClaim);
                    break;
                }
            }
        }
        if (claimMapping == null) {
            claimMapping = new ClaimMapping();
        }
        if (claimMapping.getClaim() == null) {
            Claim claim = new Claim();
            claimMapping.setClaim(claim);
        }
        claimMapping.getClaim().setDialectURI(externalClaim.getClaimDialectURI());
        claimMapping.getClaim().setClaimUri(externalClaim.getClaimURI());
        return claimMapping;
    }

    public static ClaimMapping convertLocalClaimToClaimMapping(LocalClaim localClaim) throws StoreException {

        ClaimMapping claimMapping = new ClaimMapping();
        Claim claim = new Claim();
        claim.setClaimUri(localClaim.getClaimURI());
        claim.setDialectURI(localClaim.getClaimDialectURI());
        Map<String, String> claimProperties = localClaim.getClaimProperties();

        if (claimProperties.containsKey(ClaimConstants.DISPLAY_NAME_PROPERTY)) {
            claim.setDisplayTag(claimProperties.get(ClaimConstants.DISPLAY_NAME_PROPERTY));
        }
        if (claimProperties.containsKey(ClaimConstants.DESCRIPTION_PROPERTY)) {
            claim.setDescription(claimProperties.get(ClaimConstants.DESCRIPTION_PROPERTY));
        }
        if (claimProperties.containsKey(ClaimConstants.REGULAR_EXPRESSION_PROPERTY)) {
            claim.setRegEx(claimProperties.get(ClaimConstants.REGULAR_EXPRESSION_PROPERTY));
        }
        if (claimProperties.containsKey(ClaimConstants.DISPLAY_ORDER_PROPERTY)) {
            claim.setDisplayOrder(Integer.parseInt(claimProperties.get(ClaimConstants.DISPLAY_ORDER_PROPERTY)));
        }
        if (claimProperties.containsKey(ClaimConstants.SUPPORTED_BY_DEFAULT_PROPERTY)) {
            claim.setSupportedByDefault(
                    Boolean.parseBoolean(claimProperties.get(ClaimConstants.SUPPORTED_BY_DEFAULT_PROPERTY)));
        }
        if (claimProperties.containsKey(ClaimConstants.REQUIRED_PROPERTY)) {
            claim.setRequired(Boolean.parseBoolean(claimProperties.get(ClaimConstants.REQUIRED_PROPERTY)));
        }
        if (claimProperties.containsKey(ClaimConstants.READ_ONLY_PROPERTY)) {
            claim.setReadOnly(Boolean.parseBoolean(claimProperties.get(ClaimConstants.READ_ONLY_PROPERTY)));
        }
        claimMapping.setClaim(claim);

        List<AttributeMapping> mappedAttributes = localClaim.getMappedAttributes();
        for (AttributeMapping attributeMapping : mappedAttributes) {
            claimMapping.setMappedAttribute(attributeMapping.getUserStoreDomain(), attributeMapping.getAttributeName());
        }
        if (claimProperties.containsKey(ClaimConstants.DEFAULT_ATTRIBUTE)) {
            claimMapping.setMappedAttribute(claimProperties.get(ClaimConstants.DEFAULT_ATTRIBUTE));
        } else {
            claimMapping.setMappedAttribute(localClaim.getMappedAttribute(ClaimConstants.PRIMARY_DEFAULT_DOMAIN_NAME));
        }

        return claimMapping;
    }
}
