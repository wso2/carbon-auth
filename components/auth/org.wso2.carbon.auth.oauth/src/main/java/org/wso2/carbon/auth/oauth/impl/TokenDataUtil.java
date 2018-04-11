package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.dto.TokenState;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.time.Instant;

class TokenDataUtil {

    static AccessTokenData generateTokenData(AccessTokenContext context) {
        AccessTokenResponse accessTokenResponse = context.getAccessTokenResponse();
        Tokens tokens = accessTokenResponse.getTokens();

        AccessTokenData accessTokenData = new AccessTokenData();
        accessTokenData.setAccessToken(tokens.getAccessToken().getValue());
        //refresh token can be null in client credentials grant
        if (tokens.getRefreshToken() != null) {
            accessTokenData.setRefreshToken(tokens.getRefreshToken().getValue());
        }
        accessTokenData.setScopes(tokens.getAccessToken().getScope().toString());

        Instant timestamp = Instant.now();
        long defaultRefreshTokenValidityPeriod = ServiceReferenceHolder.getInstance().getAuthConfigurations()
                .getDefaultRefreshTokenValidityPeriod();

        accessTokenData.setAccessTokenCreatedTime(timestamp);
        accessTokenData.setRefreshTokenCreatedTime(timestamp);
        accessTokenData.setAccessTokenValidityPeriod(tokens.getAccessToken().getLifetime());
        accessTokenData.setRefreshTokenValidityPeriod(defaultRefreshTokenValidityPeriod);
        accessTokenData.setTokenState(TokenState.ACTIVE);
        accessTokenData.setGrantType((String) context.getParams().get(OAuthConstants.GRANT_TYPE));

        return accessTokenData;
    }
}
