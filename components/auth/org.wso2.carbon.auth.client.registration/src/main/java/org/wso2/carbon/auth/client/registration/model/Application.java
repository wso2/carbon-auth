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

package org.wso2.carbon.auth.client.registration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Application Model
 */
public class Application {
    private String clientId;
    private String clientSecret;
    private String authUser;
    private String callBackUrl;
    private String grantTypes = "";
    private String clientName;
    private String oauthVersion;
    private String appState;
    private String userAccessTokenExpiryTime;
    private Long applicationAccessTokenExpiryTime;
    private String refreshTokenExpiryTime;
    private String tokenType;
    private List<String> audiences = new ArrayList<>();

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getOauthVersion() {
        return oauthVersion;
    }

    public void setOauthVersion(String oauthVersion) {
        this.oauthVersion = oauthVersion;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }

    public String getUserAccessTokenExpiryTime() {
        return userAccessTokenExpiryTime;
    }

    public void setUserAccessTokenExpiryTime(String userAccessTokenExpiryTime) {
        this.userAccessTokenExpiryTime = userAccessTokenExpiryTime;
    }

    public Long getApplicationAccessTokenExpiryTime() {
        return applicationAccessTokenExpiryTime;
    }

    public void setApplicationAccessTokenExpiryTime(Long applicationAccessTokenExpiryTime) {
        this.applicationAccessTokenExpiryTime = applicationAccessTokenExpiryTime;
    }

    public String getRefreshTokenExpiryTime() {
        return refreshTokenExpiryTime;
    }

    public void setRefreshTokenExpiryTime(String refreshTokenExpiryTime) {
        this.refreshTokenExpiryTime = refreshTokenExpiryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Application that = (Application) o;

        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientSecret, that.clientSecret) &&
                Objects.equals(authUser, that.authUser) &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(callBackUrl, that.callBackUrl) &&
                Objects.equals(grantTypes, that.grantTypes) &&
                Objects.equals(clientName, that.clientName);

    }

    public List<String> getAudiences() {
        return audiences;
    }

    public void setAudiences(List<String> audiences) {
        this.audiences = audiences;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientSecret, clientName);
    }

    @Override
    public String toString() {
        return "Application{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", authUser='" + authUser + '\'' +
                ", callBackUrl='" + callBackUrl + '\'' +
                ", grantTypes='" + grantTypes + '\'' +
                ", clientName='" + clientName + '\'' +
                ", oauthVersion='" + oauthVersion + '\'' +
                ", appState='" + appState + '\'' +
                ", userAccessTokenExpiryTime='" + userAccessTokenExpiryTime + '\'' +
                ", applicationAccessTokenExpiryTime='" + applicationAccessTokenExpiryTime + '\'' +
                ", refreshTokenExpiryTime='" + refreshTokenExpiryTime + '\'' +
                ",tokenType='" + tokenType + '\'' +
                '}';
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenType() {
        return tokenType;
    }
}
