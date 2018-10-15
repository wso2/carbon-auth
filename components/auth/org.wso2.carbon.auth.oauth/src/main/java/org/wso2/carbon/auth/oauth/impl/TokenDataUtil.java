package org.wso2.carbon.auth.oauth.impl;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.token.Tokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.Constants;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.OAuthUtils;
import org.wso2.carbon.auth.oauth.configuration.models.OAuthConfiguration;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.dto.AccessTokenData;
import org.wso2.carbon.auth.oauth.dto.TokenState;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.time.Instant;

class TokenDataUtil {
    private static final Logger log = LoggerFactory.getLogger(TokenDataUtil.class);

    static AccessTokenData generateTokenData(AccessTokenContext context) {
        OAuthConfiguration oAuthConfiguration = ServiceReferenceHolder.getInstance().getAuthConfigurations();
        String tokenType = (String) context.getParams().getOrDefault(OAuthConstants.TOKEN_TYPE, Constants
                .DEFAULT_TOKEN_TYPE);
        AccessTokenResponse accessTokenResponse = context.getAccessTokenResponse();
        Tokens tokens = accessTokenResponse.getTokens();

        AccessTokenData accessTokenData = new AccessTokenData();
        Object tokenAlias = context.getParams().get(OAuthConstants.TOKEN_ALIAS);
        if (!(Constants.DEFAULT_TOKEN_TYPE.equals(tokenType) || !oAuthConfiguration.isPersistAccessTokenAlias()
                || tokenAlias == null)) {
            accessTokenData.setAccessToken((String) tokenAlias);
        } else {
            accessTokenData.setAccessToken(tokens.getAccessToken().getValue());
        }

        //refresh token can be null in client credentials grant
        if (tokens.getRefreshToken() != null) {
            accessTokenData.setRefreshToken(tokens.getRefreshToken().getValue());
        }
        accessTokenData.setScopes(tokens.getAccessToken().getScope().toStringList());
        accessTokenData.setHashedScopes(OAuthUtils.hashScopes(tokens.getAccessToken().getScope()));

        Instant timestamp = Instant.now();
        long defaultRefreshTokenValidityPeriod = ServiceReferenceHolder.getInstance().getAuthConfigurations()
                .getDefaultRefreshTokenValidityPeriod();
        accessTokenData.setAccessTokenCreatedTime(timestamp);
        accessTokenData.setAccessTokenValidityPeriod(tokens.getAccessToken().getLifetime());
        String grantTypeValue = (String) context.getParams().get(OAuthConstants.GRANT_TYPE);
        if (!GrantType.CLIENT_CREDENTIALS.getValue().equals(grantTypeValue)) {
            accessTokenData.setRefreshTokenCreatedTime(timestamp);
            accessTokenData.setRefreshTokenValidityPeriod(defaultRefreshTokenValidityPeriod);
        }
        accessTokenData.setTokenState(TokenState.ACTIVE);
        accessTokenData.setGrantType(grantTypeValue);

        return accessTokenData;
    }
}
