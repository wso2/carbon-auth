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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Application {
    private String clientId;
    private String clientSecret;
    private String clientSecretExpiresAt;
    private List<String> redirectUris;
    private List<String> grantTypes;
    private String clientName;
    private String createdUser;
    private LocalDateTime createdTime;
    private String updatedUser;
    private LocalDateTime updatedTime;
    private String permissionString;
    private HashMap permissionMap;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public String getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public void setClientSecretExpiresAt(String clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
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

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }

    public HashMap getPermissionMap() {
        return permissionMap;
    }

    public void setPermissionMap(HashMap permissionMap) {
        this.permissionMap = permissionMap;
    }

    public List<String> getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(List<String> grantTypes) {
        this.grantTypes = grantTypes;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Application that = (Application) o;

        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientSecret, that.clientSecret) &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(clientSecretExpiresAt, that.clientSecretExpiresAt) &&
                Objects.equals(redirectUris, that.redirectUris) &&
                Objects.equals(grantTypes, that.grantTypes) &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(permissionString, that.permissionString) &&
                Objects.equals(createdUser, that.createdUser);

    }

    @Override public int hashCode() {
        return Objects.hash(clientId, clientSecret, clientName, clientSecretExpiresAt);
    }

    @Override public String toString() {
        return "Application{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", clientSecretExpiresAt='" + clientSecretExpiresAt + '\'' +
                ", redirectUris=" + redirectUris +
                ", grantTypes=" + grantTypes +
                ", clientName='" + clientName + '\'' +
                ", createdUser='" + createdUser + '\'' +
                ", createdTime=" + createdTime +
                ", updatedUser='" + updatedUser + '\'' +
                ", updatedTime=" + updatedTime +
                ", permissionString='" + permissionString + '\'' +
                ", permissionMap=" + permissionMap +
                '}';
    }
}
