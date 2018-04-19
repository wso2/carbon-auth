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

package org.wso2.carbon.auth.oauth.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO for Access token related data
 */
public class AccessTokenData {
    private String accessToken;
    private String authUser;
    private List<String> scopes;
    private String hashedScopes;
    private String refreshToken;
    private String clientId;
    private String grantType;
    private Instant accessTokenCreatedTime;
    private Instant refreshTokenCreatedTime;
    private long accessTokenValidityPeriod;
    private long refreshTokenValidityPeriod;
    private TokenState tokenState;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public Instant getAccessTokenCreatedTime() {
        return accessTokenCreatedTime;
    }

    public void setAccessTokenCreatedTime(Instant accessTokenCreatedTime) {
        this.accessTokenCreatedTime = accessTokenCreatedTime;
    }

    public Instant getRefreshTokenCreatedTime() {
        return refreshTokenCreatedTime;
    }

    public void setRefreshTokenCreatedTime(Instant refreshTokenCreatedTime) {
        this.refreshTokenCreatedTime = refreshTokenCreatedTime;
    }

    public long getAccessTokenValidityPeriod() {
        return accessTokenValidityPeriod;
    }

    public void setAccessTokenValidityPeriod(long accessTokenValidityPeriod) {
        this.accessTokenValidityPeriod = accessTokenValidityPeriod;
    }

    public long getRefreshTokenValidityPeriod() {
        return refreshTokenValidityPeriod;
    }

    public void setRefreshTokenValidityPeriod(long refreshTokenValidityPeriod) {
        this.refreshTokenValidityPeriod = refreshTokenValidityPeriod;
    }

    public TokenState getTokenState() {
        return tokenState;
    }

    public void setTokenState(TokenState tokenState) {
        this.tokenState = tokenState;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getHashedScopes() {
        return hashedScopes;
    }

    public void setHashedScopes(String hashedScopes) {
        this.hashedScopes = hashedScopes;
    }
}
