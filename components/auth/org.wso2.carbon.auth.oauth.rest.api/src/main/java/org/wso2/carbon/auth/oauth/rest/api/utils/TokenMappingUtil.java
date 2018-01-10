/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.oauth.rest.api.utils;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.wso2.carbon.auth.oauth.rest.api.dto.TokenResponseDTO;

/**
 * Utility class for mapping token API implementation core models to REST API DTOs and vise versa.
 */
public class TokenMappingUtil {

    /**
     * This method convert the token response model object into DTO
     *
     * @param tokenResponse model object
     * @return TokenResponseDTO DTO object representing the model object
     */
    public static TokenResponseDTO tokenResponseToDTO(AccessTokenResponse tokenResponse) {
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        AccessToken accessToken = tokenResponse.getTokens().getAccessToken();
        RefreshToken refreshToken = tokenResponse.getTokens().getRefreshToken();
        tokenResponseDTO.accessToken(accessToken.getValue());
        tokenResponseDTO.tokenType(accessToken.getType().toString());

        if (accessToken.getLifetime() > 0L) {
            tokenResponseDTO.expiresIn(accessToken.getLifetime());
        }

        if (accessToken.getScope() != null) {
            tokenResponseDTO.scope(accessToken.getScope().toString());
        }

        if (refreshToken != null) {
            tokenResponseDTO.setRefreshToken(refreshToken.getValue());
        }

        return tokenResponseDTO;
    }

}
