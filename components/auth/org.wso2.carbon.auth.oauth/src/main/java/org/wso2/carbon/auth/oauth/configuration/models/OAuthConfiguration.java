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
package org.wso2.carbon.auth.oauth.configuration.models;

import com.nimbusds.oauth2.sdk.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.impl.AuthCodeGrantHandlerImpl;
import org.wso2.carbon.auth.oauth.impl.ClientCredentialsGrantHandlerImpl;
import org.wso2.carbon.auth.oauth.impl.DefaultTokenGenerator;
import org.wso2.carbon.auth.oauth.impl.PasswordGrantHandlerImpl;
import org.wso2.carbon.auth.oauth.impl.RefreshGrantHandler;
import org.wso2.carbon.auth.oauth.impl.RoleBasedScopeValidator;
import org.wso2.carbon.config.annotation.Configuration;
import org.wso2.carbon.config.annotation.Element;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keep the key manager OAuth related configurations
 */
@Configuration(namespace = "wso2.carbon.auth.oauth", description = "Key Management oAuth Configurations")
public class OAuthConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OAuthConfiguration.class);

    @Element(description = "Access token default validity period")
    private long defaultTokenValidityPeriod = 3600L;
    @Element(description = "Access token default validity period")
    private long defaultRefreshTokenValidityPeriod = 3600L;
    @Element(description = "Token generator class implementation")
    private String tokenGenerator = DefaultTokenGenerator.class.getName();
    @Element(description = "Scope validator class implementation")
    private String scopeValidator = RoleBasedScopeValidator.class.getName();
    @Element(description = "Default grant types")
    private Map<String, String> grantTypes = populateDefaultGrantTypes();
    @Element(description = "White listed Scopes")
    private List<String> whiteListedScopes = Arrays.asList(new String[]{"^device_.*"});
    @Element(description = "OIDC Scopes")
    private List<String> oidcScopes = Arrays.asList("openid", "profile", "email", "address", "phone");
    @Element(description = "File Base Scopes")
    private Map<String, List<String>> fileBaseScopes = new HashMap<>();

    public long getDefaultTokenValidityPeriod() {

        return defaultTokenValidityPeriod;
    }

    public void setDefaultTokenValidityPeriod(long defaultTokenValidityPeriod) {

        this.defaultTokenValidityPeriod = defaultTokenValidityPeriod;
    }

    public long getDefaultRefreshTokenValidityPeriod() {

        return defaultRefreshTokenValidityPeriod;
    }

    public void setDefaultRefreshTokenValidityPeriod(long defaultRefreshTokenValidityPeriod) {

        this.defaultRefreshTokenValidityPeriod = defaultRefreshTokenValidityPeriod;
    }

    public Map<String, String> getGrantTypes() {

        return grantTypes;
    }

    public void setGrantTypes(Map<String, String> grantTypes) {

        this.grantTypes = grantTypes;
    }

    public String getTokenGenerator() {

        return tokenGenerator;
    }

    public void setTokenGenerator(String tokenGenerator) {

        this.tokenGenerator = tokenGenerator;
    }

    public String getScopeValidator() {

        return scopeValidator;
    }

    public List<String> getWhiteListedScopes() {

        return whiteListedScopes;
    }

    public void setWhiteListedScopes(List<String> whiteListedScopes) {

        this.whiteListedScopes = whiteListedScopes;
    }

    public List<String> getOidcScopes() {

        return oidcScopes;
    }

    public void setOidcScopes(List<String> oidcScopes) {

        this.oidcScopes = oidcScopes;
    }

    public Map<String, List<String>> getFileBaseScopes() {

        return fileBaseScopes;
    }

    public void setFileBaseScopes(Map<String, List<String>> fileBaseScopes) {

        this.fileBaseScopes = fileBaseScopes;
    }

    public void setScopeValidator(String scopeValidator) {

        this.scopeValidator = scopeValidator;
    }

    private Map<String, String> populateDefaultGrantTypes() {

        Map<String, String> grantTypes = new HashMap();

        grantTypes.put(GrantType.AUTHORIZATION_CODE.getValue(), AuthCodeGrantHandlerImpl.class.getName());
        grantTypes.put(GrantType.IMPLICIT.getValue(), AuthCodeGrantHandlerImpl.class.getName());
        grantTypes.put(GrantType.REFRESH_TOKEN.getValue(), RefreshGrantHandler.class.getName());
        grantTypes.put(GrantType.PASSWORD.getValue(), PasswordGrantHandlerImpl.class.getName());
        grantTypes.put(GrantType.CLIENT_CREDENTIALS.getValue(), ClientCredentialsGrantHandlerImpl.class.getName());
        return grantTypes;
    }
}
