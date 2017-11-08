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

import com.nimbusds.oauth2.sdk.ErrorObject;
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
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

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
    public String getClientId(String authorization, AccessTokenContext context, MutableBoolean haltExecution) {
        log.debug("Calling getClientId");
        if (!StringUtils.isEmpty(authorization)) {
            try {
                ClientSecretBasic clientCredentials = ClientSecretBasic.parse(authorization);

                ClientID clientId = clientCredentials.getClientID();
                Secret clientSecret = clientCredentials.getClientSecret();
                boolean isValid = oauthDAO.isClientCredentialsValid(clientId.getValue(), clientSecret.getValue());

                if (!isValid) {
                    ErrorObject error = new ErrorObject(OAuth2Error.INVALID_CLIENT.getCode());
                    context.setErrorObject(error);
                    haltExecution.setTrue();
                }

                return clientId.getValue();
            } catch (ParseException e) {
                log.info("Error while parsing client credentials: ", e.getMessage());
                context.setErrorObject(e.getErrorObject());
                haltExecution.setTrue();
            } catch (OAuthDAOException e) {
                log.error("Error while validating client credentials", e);
                ErrorObject error = new ErrorObject(OAuth2Error.SERVER_ERROR.getCode());
                context.setErrorObject(error);
                haltExecution.setTrue();
            }
        } else {
            log.info("Authorization header is missing");
            ErrorObject error = new ErrorObject(OAuth2Error.INVALID_REQUEST.getCode());
            context.setErrorObject(error);
            haltExecution.setTrue();
        }

        return "";
    }
}
