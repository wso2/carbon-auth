/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.core.configuration.models;

import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;

/**
 * Class to hold key manager configurations
 */
@Configuration(description = "Key Management Configurations")
public class KeyManagerConfiguration {

    @Element(description = "Access token default validity period")
    private long defaultTokenValidityPeriod = 3600L;

    @Element(description = "KeyStore Location")
    private String keyStoreLocation = "${carbon.home}/resources/security/wso2carbon.jks";
    @Element(description = "Keystore password")
    private String keyStorePassword = "wso2carbon";
    @Element(description = "Keystore Alias")
    private String keyStoreAlias = "wso2carbon";
    @Element(description = "KeyPassword")
    private String keyPassword = "wso2carbon";

    public long getDefaultTokenValidityPeriod() {
        return defaultTokenValidityPeriod;
    }

    public void setDefaultTokenValidityPeriod(long defaultTokenValidityPeriod) {
        this.defaultTokenValidityPeriod = defaultTokenValidityPeriod;
    }

    public String getKeyStoreLocation() {

        return keyStoreLocation;
    }

    public void setKeyStoreLocation(String keyStoreLocation) {

        this.keyStoreLocation = keyStoreLocation;
    }

    public String getKeyStorePassword() {

        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {

        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreAlias() {

        return keyStoreAlias;
    }

    public void setKeyStoreAlias(String keyStoreAlias) {

        this.keyStoreAlias = keyStoreAlias;
    }

    public String getKeyPassword() {

        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {

        this.keyPassword = keyPassword;
    }
}
