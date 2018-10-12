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
package org.wso2.carbon.auth.oauth;

import com.nimbusds.oauth2.sdk.Scope;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.configuration.models.KeyManagerConfiguration;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;

/**
 * utils function related to oauth component
 */
public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static boolean isAccessTokenExpired(AccessTokenDTO accessTokenDTO) {
        // check whether the grant is expired
        if (accessTokenDTO.getValidityPeriod() < 0) {
            if (log.isDebugEnabled()) {
                log.debug("Access Token has infinite lifetime");
            }
        } else {
            if (getAccessTokenExpireMillis(accessTokenDTO) == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Access Token has expired");
                }
                return true;
            }
        }
        return false;
    }

    public static long getAccessTokenExpireMillis(AccessTokenDTO accessTokenDTO) {

        long validityPeriodMillis = accessTokenDTO.getValidityPeriod() * 1000L;
        long issuedTime = accessTokenDTO.getTimeCreated();
        long validityMillis = calculateValidityInMillis(issuedTime, validityPeriodMillis);
        if (validityMillis > 1000) {
            return validityMillis;
        } else {
            return 0;
        }
    }

    public static long calculateValidityInMillis(long issuedTimeInMillis, long validityPeriodMillis) {
        //todo: need to timestampSkew configurable
        long timestampSkew = 5 * 1000;
        return issuedTimeInMillis + validityPeriodMillis - (System.currentTimeMillis() - timestampSkew);
    }

    public static String hashScopes(Scope scope) {

        List<String> scopes = scope.toStringList();
        Collections.sort(scopes);
        String scopeString = String.join(" ", scopes);
        return DigestUtils.md5Hex(scopeString);
    }

    public static PrivateKey extractPrivateKeyFromCertificate() throws AuthException {

        KeyManagerConfiguration keyManagerConfiguration = ServiceReferenceHolder.getInstance().getAuthConfiguration()
                .getKeyManagerConfigs();
        Key key;

        try (InputStream inputStream = new FileInputStream(keyManagerConfiguration.getKeyStoreLocation())) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(inputStream, keyManagerConfiguration.getKeyStorePassword().toCharArray());
            key = keyStore.getKey(keyManagerConfiguration.getKeyStoreAlias(), keyManagerConfiguration.getKeyPassword
                    ().toCharArray());

        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                UnrecoverableKeyException e) {
            throw new AuthException("Error while retrieving private key from certificate", e);
        }
        if (!(key instanceof PrivateKey)) {
            throw new AuthException("Error while retrieving private key from certificate");
        }
        return (PrivateKey) key;

    }

    public static Certificate extractPublicKeyFromCertificate() throws AuthException {

        KeyManagerConfiguration keyManagerConfiguration = ServiceReferenceHolder.getInstance().getAuthConfiguration()
                .getKeyManagerConfigs();

        try (InputStream inputStream = new FileInputStream(keyManagerConfiguration.getKeyStoreLocation())) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(inputStream, keyManagerConfiguration.getKeyStorePassword().toCharArray());
            return keyStore.getCertificate(keyManagerConfiguration.getKeyStoreAlias());
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new AuthException("Error while retrieving public key from certificate", e);
        }

    }

}
