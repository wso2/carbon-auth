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
import org.wso2.carbon.auth.user.store.exception.StoreException;

/**
 * Claim metadata holder interface
 */
public interface ClaimMetadataStore {
    String getAttributeName(String claimURI) throws StoreException;

    Claim getClaim(String var1) throws StoreException;

    ClaimMapping getClaimMapping(String var1) throws StoreException;

    ClaimMapping[] getAllSupportClaimMappingsByDefault() throws StoreException;

    ClaimMapping[] getAllClaimMappings() throws StoreException;

    ClaimMapping[] getAllClaimMappings(String var1) throws StoreException;

    ClaimMapping[] getAllRequiredClaimMappings() throws StoreException;

    String[] getAllClaimUris() throws StoreException;

    void addNewClaimMapping(ClaimMapping var1) throws StoreException;

    void deleteClaimMapping(ClaimMapping var1) throws StoreException;

    void updateClaimMapping(ClaimMapping var1) throws StoreException;

    String getAttributeName(String var1, String var2) throws StoreException;
}
