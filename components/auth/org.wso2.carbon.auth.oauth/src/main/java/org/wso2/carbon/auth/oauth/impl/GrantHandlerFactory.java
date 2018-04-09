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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.core.api.UserNameMapper;
import org.wso2.carbon.auth.core.impl.UserNameMapperFactory;
import org.wso2.carbon.auth.oauth.GrantHandler;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;
import org.wso2.carbon.auth.user.mgt.UserStoreException;
import org.wso2.carbon.auth.user.mgt.UserStoreManager;
import org.wso2.carbon.auth.user.mgt.UserStoreManagerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Factory for creating relevant Grant handler based on grant type being served
 */
public class GrantHandlerFactory {
    private static final Logger log = LoggerFactory.getLogger(GrantHandlerFactory.class);
    private static UserNameMapper userNameMapper = UserNameMapperFactory.getInstance().getUserNameMapper();

    /**
     * Create relevant Grant Handler
     *
     * @param grantTypeValue grant type being served
     * @return Grant handler implementation
     */
    static Optional<GrantHandler> createGrantHandler(String grantTypeValue, AccessTokenContext context,
            OAuthDAO oauthDAO, ApplicationDAO applicationDAO, TokenDAO tokenDAO, MutableBoolean haltExecution)
            throws UserStoreException {
        log.debug("Calling createGrantHandler");
        if (!StringUtils.isEmpty(grantTypeValue)) {
            Map<String, String> grantTypes = ServiceReferenceHolder.getInstance().getAuthConfigurations()
                    .getGrantTypes();
            String grantTypeImplClassName = grantTypes.get(grantTypeValue);
            if (grantTypeImplClassName == null) {
                log.debug("Requested grant type not found");
                context.setErrorObject(OAuth2Error.UNSUPPORTED_GRANT_TYPE);
                haltExecution.setTrue();
                return Optional.empty();
            }
            Class<?> grantTypeImplClass = null;
            GrantHandler grantHandlerImpl = null;
            try {
                grantTypeImplClass = Class.forName(grantTypeImplClassName);
                grantHandlerImpl = (GrantHandler) grantTypeImplClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Error instantiation class " + grantTypeImplClass, e);
                context.setErrorObject(OAuth2Error.UNSUPPORTED_GRANT_TYPE);
                haltExecution.setTrue();
                return Optional.empty();
            } catch (ClassNotFoundException e) {
                log.error("Requested grant type implementation not found", e);
                context.setErrorObject(OAuth2Error.UNSUPPORTED_GRANT_TYPE);
                haltExecution.setTrue();
                return Optional.empty();
            }
            UserStoreManager userStoreManager = UserStoreManagerFactory.getUserStoreManager();
            grantHandlerImpl.init(userNameMapper, oauthDAO, userStoreManager, applicationDAO, tokenDAO);
            return Optional.of(grantHandlerImpl);
        } else {
            context.setErrorObject(OAuth2Error.INVALID_REQUEST);
            haltExecution.setTrue();
            log.info(OAuthConstants.GRANT_TYPE_QUERY_PARAM + " has not been sent in request");
        }

        return Optional.empty();
    }
}
