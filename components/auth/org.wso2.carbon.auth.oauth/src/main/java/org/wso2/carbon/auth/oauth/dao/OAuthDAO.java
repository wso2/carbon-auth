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

import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.net.URI;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * DAO Interface to OAuth related data
 */
public interface OAuthDAO {
    /**
     * Get pre-registered redirectUri of a client
     *
     * @param clientId Client Id of client
     * @return Redirect Uri if exists else Optional.Empty
     * @throws OAuthDAOException if a DAO Error is encountered
     */
    Optional<Optional<String>> getRedirectUri(String clientId) throws OAuthDAOException;

    /**
     * Add Authorization code related information
     *
     * @param authCode Generated Authorization Code
     * @param clientId Client Id of client
     * @param redirectUri Redirect Uri
     * @throws OAuthDAOException if a DAO Error is encountered
     */
    void addAuthCodeInfo(String authCode, String clientId, String scope, @Nullable URI redirectUri)
            throws OAuthDAOException;

    /**
     * Get scope for matching persisted Authorization code information
     *
     * @param authCode Generated Authorization Code
     * @param clientId Client Id of client
     * @param redirectUri Redirect Uri
     * @return Scope
     * @throws OAuthDAOException if a DAO Error is encountered
     */
    @CheckForNull
    String getScopeForAuthCode(String authCode, String clientId, @Nullable URI redirectUri) throws OAuthDAOException;

    /**
     * Check if client Id/secret credentials are valid
     *
     * @param clientId Client Id of client
     * @param clientSecret Client Secret of client
     * @return true if client Id/secret is valid else false
     * @throws OAuthDAOException if a DAO Error is encountered
     */
    boolean isClientCredentialsValid(String clientId, String clientSecret) throws OAuthDAOException;

    /**
     * Add access token related information
     *
     * @param accessTokenData Access token related data
     * @throws OAuthDAOException if a DAO Error is encountered
     */
    void addAccessTokenInfo(AccessTokenData accessTokenData) throws OAuthDAOException;

    /**
     * Get access token related information
     *
     * @param authUser  authenticated user
     * @param grantType requested grant type
     * @param clientId  requested consumer key
     * @param scopes  requested scopes
     * @return return AccessTokenDTO
     * @throws OAuthDAOException throws if a error occurred
     */
    AccessTokenDTO getTokenInfo(String authUser, String grantType, String clientId, String scopes)
            throws OAuthDAOException;

    /**
     * Get access token related information
     *
     * @param accessToken accessToken
     * @return return AccessTokenDTO
     * @throws OAuthDAOException throws if a error occurred
     */
    AccessTokenDTO getTokenInfo(String accessToken) throws OAuthDAOException;

    /**
     * Get access token related information from both accessToken and consumer key
     *
     * @param refreshToken refreshToken
     * @param consumerKey  consumer key
     * @return return AccessTokenDTO
     * @throws OAuthDAOException throws if a error occurred
     */
    AccessTokenDTO getTokenInfo(String refreshToken, String consumerKey) throws OAuthDAOException;
}
