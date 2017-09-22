/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.oauth.dao;

import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.net.URI;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * DAO Interface to Client related data
 */
public interface ClientDAO {
    /**
     * Get public information related to a client including the clientId and redirectUri
     *
     * @param clientId Client Id of client
     * @return Redirect Uri if exists else Optional.Empty
     * @throws ClientDAOException if a DOA Error is encountered
     */
    Optional<Optional<String>> getRedirectUri(String clientId) throws ClientDAOException;

    /**
     * Add Authorization code related information
     *
     * @param authCode Generated Authorization Code
     * @param clientId Client Id of client
     * @param redirectUri Redirect Uri
     * @throws ClientDAOException if a DOA Error is encountered
     */
    void addAuthCodeInfo(String authCode, String clientId, String scope, @Nullable URI redirectUri)
            throws ClientDAOException;

    /**
     * Get scope for matching persisted Authorization code information
     *
     * @param authCode Generated Authorization Code
     * @param clientId Client Id of client
     * @param redirectUri Redirect Uri
     * @return Scope
     * @throws ClientDAOException if a DOA Error is encountered
     */
    @CheckForNull
    String getScopeForAuthCode(String authCode, String clientId, @Nullable URI redirectUri) throws ClientDAOException;

    /**
     * Check if client Id/secret credentials are valid
     *
     * @param clientId Client Id of client
     * @param clientSecret Client Secret of client
     * @return true if client Id/secret is valid else false
     * @throws ClientDAOException
     */
    boolean isClientCredentialsValid(String clientId, String clientSecret) throws ClientDAOException;
}
