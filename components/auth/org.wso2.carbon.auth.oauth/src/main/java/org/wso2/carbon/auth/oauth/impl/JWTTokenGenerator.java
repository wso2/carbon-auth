/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.Scope;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.auth.core.exception.AuthException;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token Generation implementation
 */
public class JWTTokenGenerator extends DefaultTokenGenerator {

    private static final String AUTHORIZATION_PARTY = "azp";
    private static final String SCOPE = "scope";
    private static final String NONE = "NONE";
    private static final String SHA256_WITH_RSA = "SHA256withRSA";
    private static final String SHA384_WITH_RSA = "SHA384withRSA";
    private static final String SHA512_WITH_RSA = "SHA512withRSA";
    private static final String SHA256_WITH_HMAC = "SHA256withHMAC";
    private static final String SHA384_WITH_HMAC = "SHA384withHMAC";
    private static final String SHA512_WITH_HMAC = "SHA512withHMAC";
    private static final String SHA256_WITH_EC = "SHA256withEC";
    private static final String SHA384_WITH_EC = "SHA384withEC";
    private static final String SHA512_WITH_EC = "SHA512withEC";
    private Algorithm signatureAlgorithm;

    public JWTTokenGenerator() throws AuthException {

        signatureAlgorithm = mapSignatureAlgorithm(ServiceReferenceHolder.getInstance().getAuthConfigurations()
                .getSignatureAlgorithm());
    }

    @Override
    public void generateAccessToken(AccessTokenContext context) {

        JWTClaimsSet jwtClaimsSet = getJWTClaimSet(context);
        String jwtToken;
        if (JWSAlgorithm.NONE.getName().equals(signatureAlgorithm.getName())) {
            jwtToken = new PlainJWT(jwtClaimsSet).serialize();
            context.getParams().put(OAuthConstants.TOKEN_ALIAS, jwtClaimsSet.getJWTID());
            context.getParams().put(OAuthConstants.TOKEN, jwtToken);
            super.generateAccessToken(context);
        } else {
            try {
                jwtToken = signJwt(jwtClaimsSet);
                context.getParams().put(OAuthConstants.TOKEN_ALIAS, jwtClaimsSet.getJWTID());
                context.getParams().put(OAuthConstants.TOKEN, jwtToken);
                super.generateAccessToken(context);
            } catch (AuthException e) {
                context.setSuccessful(false);
                context.setErrorObject(OAuth2Error.SERVER_ERROR);
            }
        }
    }

    @Override
    public boolean renewAccessTokenPerRequest() {

        return true;
    }

    private String signJwt(JWTClaimsSet jwtClaimsSet) throws AuthException {

        if (JWSAlgorithm.RS256.equals(signatureAlgorithm) || JWSAlgorithm.RS384.equals(signatureAlgorithm) ||
                JWSAlgorithm.RS512.equals(signatureAlgorithm)) {
            try {
                JWSSigner signer = new RSASSASigner(ServiceReferenceHolder.getInstance().getPrivateKey());
                JWSAlgorithm jwsAlgorithm;
                if (signatureAlgorithm instanceof JWSAlgorithm) {
                    jwsAlgorithm = (JWSAlgorithm) signatureAlgorithm;
                } else {
                    throw new AuthException("Signature Algorithm couldn't convert to JWSAlgorithm");
                }
                JWSHeader.Builder headerBuilder = new JWSHeader.Builder(jwsAlgorithm);
                String certThumbPrint = getThumbPrint(ServiceReferenceHolder.getInstance().getPublicKey());
                headerBuilder.keyID(certThumbPrint);
                headerBuilder.x509CertThumbprint(new Base64URL(certThumbPrint));
                SignedJWT signedJWT = new SignedJWT(headerBuilder.build(), jwtClaimsSet);
                signedJWT.sign(signer);
                return signedJWT.serialize();
            } catch (NoSuchAlgorithmException | CertificateEncodingException | JOSEException e) {
                throw new AuthException("Invalid signature algorithm provided. " + signatureAlgorithm);
            }
        } else {
            throw new AuthException("Invalid signature algorithm provided. " + signatureAlgorithm);
        }
    }

    private JWTClaimsSet getJWTClaimSet(AccessTokenContext context) {

        Scope scope = (Scope) context.getParams().get(OAuthConstants.SCOPES);
        long defaultValidityPeriod = (long) context.getParams().get(OAuthConstants.VALIDITY_PERIOD);
        long curTimeInMillis = Calendar.getInstance().getTimeInMillis();
        String sub = (String) context.getParams().get(OAuthConstants.AUTH_USER);
        String consumerKey = (String) context.getParams().get(OAuthConstants.CLIENT_ID);
        String issuer = ServiceReferenceHolder.getInstance().getAuthConfigurations().getTokenIssuer();
        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder();
        jwtClaimsSetBuilder.issuer(issuer);
        jwtClaimsSetBuilder.subject(sub);
        jwtClaimsSetBuilder.claim(AUTHORIZATION_PARTY, consumerKey);
        jwtClaimsSetBuilder.issueTime(new Date(curTimeInMillis));
        jwtClaimsSetBuilder.jwtID(UUID.randomUUID().toString());
        jwtClaimsSetBuilder.notBeforeTime(new Date(curTimeInMillis));
        jwtClaimsSetBuilder.claim(SCOPE, scope.toString());
        if (defaultValidityPeriod < 0) {
            jwtClaimsSetBuilder.expirationTime(new Date(Long.MAX_VALUE));
        } else {
            jwtClaimsSetBuilder.expirationTime(new Date(curTimeInMillis + defaultValidityPeriod * 1000));
        }
        jwtClaimsSetBuilder.audience(Collections.singletonList(consumerKey));

        return jwtClaimsSetBuilder.build();
    }

    /**
     * This method map signature algorithm define in identity.xml to nimbus signature algorithm format, Strings are
     * defined inline hence there are not being used any where
     *
     * @param signatureAlgorithm Signature algorithm.
     * @return JWS algorithm.
     * @throws AuthException Unsupported signature algorithm.
     */
    protected JWSAlgorithm mapSignatureAlgorithm(String signatureAlgorithm) throws AuthException {

        if (StringUtils.isNotBlank(signatureAlgorithm)) {
            switch (signatureAlgorithm) {
                case NONE:
                    return new JWSAlgorithm(JWSAlgorithm.NONE.getName());
                case SHA256_WITH_RSA:
                    return JWSAlgorithm.RS256;
                case SHA384_WITH_RSA:
                    return JWSAlgorithm.RS384;
                case SHA512_WITH_RSA:
                    return JWSAlgorithm.RS512;
                case SHA256_WITH_HMAC:
                    return JWSAlgorithm.HS256;
                case SHA384_WITH_HMAC:
                    return JWSAlgorithm.HS384;
                case SHA512_WITH_HMAC:
                    return JWSAlgorithm.HS512;
                case SHA256_WITH_EC:
                    return JWSAlgorithm.ES256;
                case SHA384_WITH_EC:
                    return JWSAlgorithm.ES384;
                case SHA512_WITH_EC:
                    return JWSAlgorithm.ES512;
            }
        }
        throw new AuthException("Unsupported Signature Algorithm");
    }

    private static String getThumbPrint(Certificate certificate) throws NoSuchAlgorithmException,
            CertificateEncodingException {
        // Generate the SHA-1 thumbprint of the certificate.
        MessageDigest digestValue = MessageDigest.getInstance("SHA-1");
        byte[] der = certificate.getEncoded();
        digestValue.update(der);
        byte[] digestInBytes = digestValue.digest();

        String publicCertThumbprint = hexify(digestInBytes);
        return new String(new Base64(0, null, true).encode(
                publicCertThumbprint.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
    }

    /**
     * Helper method to hexify a byte array.
     * TODO:need to verify the logic
     *
     * @param bytes
     * @return hexadecimal representation
     */
    private static String hexify(byte bytes[]) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
                +'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuilder buf = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
            buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
            buf.append(hexDigits[bytes[i] & 0x0f]);
        }

        return buf.toString();
    }

}
