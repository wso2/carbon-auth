/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.auth.oauth.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.oauth.TokenManager;
import org.wso2.carbon.auth.oauth.dao.TokenDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.dto.AccessTokenDTO;
import org.wso2.carbon.auth.oauth.exception.ClientDAOException;

import java.sql.SQLException;

/**
 *
 */
public class TokenManagerImpl implements TokenManager {
    private static final Logger log = LoggerFactory.getLogger(TokenManagerImpl.class);
    private TokenDAO tokenDAO;

    public TokenManagerImpl() {

        try {
            tokenDAO = DAOFactory.getTokenDAO();
        } catch (ClientDAOException e) {
            throw new IllegalStateException("Could not create TokenManagerImpl", e);
        }
    }

    @Override
    public void storeToken(String accessToken, String refreshToken, String clientID, String authUser, String userDomain,
            long timeCreated, long refreshTokenCreatedTime, int validityPeriod, int refreshTokenValidityPeriod,
            String tokenScopeHash, String tokenState, String userType, String grantType) {
        try {
            tokenDAO.persistToken(accessToken, refreshToken, clientID, authUser, userDomain, timeCreated,
                    refreshTokenCreatedTime, validityPeriod, refreshTokenValidityPeriod, tokenScopeHash, tokenState,
                    userType, grantType);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public AccessTokenDTO getTokenInfo(String accessToken) {
        try {
            return tokenDAO.getTokenInfo(accessToken);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
