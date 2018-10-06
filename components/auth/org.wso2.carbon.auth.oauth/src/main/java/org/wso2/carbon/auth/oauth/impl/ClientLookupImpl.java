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

package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.util.Map;

/**
 * Client lookup implementation
 */
public class ClientLookupImpl implements ClientLookup {

    private static final Logger log = LoggerFactory.getLogger(ClientLookupImpl.class);
    private OAuthDAO oauthDAO;

    ClientLookupImpl(OAuthDAO oauthDAO) {

        this.oauthDAO = oauthDAO;
    }

    @Override
    public String getClientId(String authorization, AccessTokenContext context, Map<String, String> queryParameters,
                              MutableBoolean haltExecution) {

        log.debug("Calling getClientId");
        ClientID clientId;
        Secret clientSecret;
        if (!StringUtils.isEmpty(authorization)) {
            ClientSecretBasic clientCredentials;
            try {
                clientCredentials = ClientSecretBasic.parse(authorization);
            } catch (ParseException e) {
                log.info("Error while parsing client credentials: ", e.getMessage());
                context.setErrorObject(OAuth2Error.INVALID_REQUEST);
                haltExecution.setTrue();
                return null;
            }
            clientId = clientCredentials.getClientID();
            clientSecret = clientCredentials.getClientSecret();
        } else if (queryParameters.get(OAuthConstants.CLIENT_ID_QUERY_PARAM) != null
                && queryParameters.get(OAuthConstants.CLIENT_SECRET_QUERY_PARAM) != null) {
            log.debug("Authorization header is missing");
            clientId = new ClientID(queryParameters.get(OAuthConstants.CLIENT_ID_QUERY_PARAM));
            clientSecret = new Secret(queryParameters.get(OAuthConstants.CLIENT_SECRET_QUERY_PARAM));
        } else {
            log.debug("clientId or clientSecret is missing in request");
            context.setErrorObject(OAuth2Error.INVALID_REQUEST);
            haltExecution.setTrue();
            return null;
        }

        try {
            boolean isValid = oauthDAO.isClientCredentialsValid(clientId.getValue(), clientSecret.getValue());
            if (!isValid) {
                context.setErrorObject(OAuth2Error.INVALID_CLIENT);
                haltExecution.setTrue();
                return null;
            }
            return clientId.getValue();
        } catch (OAuthDAOException e) {
            log.error("Error while validating client credentials", e);
            context.setErrorObject(OAuth2Error.SERVER_ERROR);
            haltExecution.setTrue();
        }

        return null;
    }
}
