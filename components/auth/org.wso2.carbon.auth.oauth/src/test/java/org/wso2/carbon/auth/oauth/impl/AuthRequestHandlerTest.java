package org.wso2.carbon.auth.oauth.impl;
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

import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.test.common.AuthDAOIntegrationTestBase;
//import org.wso2.carbon.auth.oauth.IntegrationTestBase;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dao.impl.DAOFactory;
import org.wso2.carbon.auth.oauth.dto.AuthResponseContext;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class AuthRequestHandlerTest extends AuthDAOIntegrationTestBase {
    private static final Logger log = LoggerFactory.getLogger(AuthRequestHandlerTest.class);

    @Before
    public void setup() throws Exception {
        super.init();
        super.setup();
        log.info("setup AuthRequestHandlerTest");
    }

    @Test
    public void testGenerateCode() throws Exception {
        String ck = "a54404ea-d588-476f-87a0-ecd963a6b0d7";
        String cs = "90f7ce1a-3363-4cd4-b6f2-abdcff0f32f2";
        String authCode = "456846s5-3123-cer4-bio2-abdcff0f98f2";
        String hashedPassword = "SSFSSFDSD5456";
        String redirectUri = "https://localhost/redirect";
        String scopes = "default";
        Map info = new HashMap();
        info.put(UserStoreConstants.PASSWORD, hashedPassword);

        OAuthDAO clientDAO = DAOFactory.getClientDAO();
        AuthRequestHandlerImpl authRequestHandler = new AuthRequestHandlerImpl(clientDAO);

        String query = "INSERT INTO AUTH_OAUTH2_AUTHORIZATION_CODE"
                + "(CLIENT_ID, AUTHORIZATION_CODE, REDIRECT_URI, SCOPE) VALUES('" + ck + "','" + authCode + "','"
                + redirectUri + "','" + scopes + "')";
        super.executeOnAuthDb(query);

        //test without client id
        AuthResponseContext responseContext = authRequestHandler.generateCode(info);
        Assert.assertEquals(OAuth2Error.INVALID_REQUEST.toString(),
                responseContext.getQueryParams().get(OAuthConstants.ERROR_QUERY_PARAM));

        //test without client information
        info.put(OAuthConstants.RESPONSE_TYPE_QUERY_PARAM, "code");
        info.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, ck + "1234");
        responseContext = authRequestHandler.generateCode(info);
        Assert.assertEquals(OAuth2Error.UNAUTHORIZED_CLIENT.toString(),
                responseContext.getQueryParams().get(OAuthConstants.ERROR_QUERY_PARAM));

        //test without redirect url
        String tmCK = ck + "12345";
        query = "INSERT INTO AUTH_OAUTH2_APPLICATION " + "(CLIENT_ID, CLIENT_SECRET, APP_NAME, OAUTH_VERSION,"
                + " REDIRECT_URI, GRANT_TYPES) VALUES ('" + tmCK + "'," + "'" + cs
                + "','sampleApp','2.0',' ','password') ";
        super.executeOnAuthDb(query);
        info.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, tmCK);
        responseContext = authRequestHandler.generateCode(info);
        Assert.assertEquals(OAuth2Error.SERVER_ERROR.toString(),
                responseContext.getQueryParams().get(OAuthConstants.ERROR_QUERY_PARAM));

        //test with invalid
        String tmCkUrl = ck + "9876";
        query = "INSERT INTO AUTH_OAUTH2_APPLICATION " + "(CLIENT_ID, CLIENT_SECRET, APP_NAME, OAUTH_VERSION,"
                + " REDIRECT_URI, GRANT_TYPES) VALUES ('" + tmCkUrl + "'," + "'" + cs
                + "','sampleApp','2.0','my url','password') ";
        super.executeOnAuthDb(query);
        info.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, tmCkUrl);
        responseContext = authRequestHandler.generateCode(info);
        Assert.assertEquals(OAuth2Error.SERVER_ERROR.toString(),
                responseContext.getQueryParams().get(OAuthConstants.ERROR_QUERY_PARAM));

        info.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, ck);
        // adding auth app details to the DB
        query = "INSERT INTO AUTH_OAUTH2_APPLICATION " + "(CLIENT_ID, CLIENT_SECRET, APP_NAME, OAUTH_VERSION,"
                + " REDIRECT_URI, GRANT_TYPES) VALUES ('" + ck + "'," + "'" + cs + "','sampleApp','2.0','" + redirectUri
                + "','password') ";
        super.executeOnAuthDb(query);
        //test without valid response type
        info.put(OAuthConstants.RESPONSE_TYPE_QUERY_PARAM, "no_code");
        responseContext = authRequestHandler.generateCode(info);
        Assert.assertEquals(OAuth2Error.INVALID_REQUEST.toString(),
                responseContext.getQueryParams().get(OAuthConstants.ERROR_QUERY_PARAM));

        info.put(OAuthConstants.RESPONSE_TYPE_QUERY_PARAM, "code");
        //test auth code
        responseContext = authRequestHandler.generateCode(info);
        Map params = responseContext.getQueryParams();
        Assert.assertNotNull(params.get(OAuthConstants.CODE_QUERY_PARAM));

        // check with scope
        info.put(OAuthConstants.SCOPE_QUERY_PARAM, scopes);
        responseContext = authRequestHandler.generateCode(info);
        params = responseContext.getQueryParams();
        Assert.assertNotNull(params.get(OAuthConstants.CODE_QUERY_PARAM));

        info.put(OAuthConstants.SCOPE_QUERY_PARAM, null);

        //test Implicit grant
        info.put(OAuthConstants.RESPONSE_TYPE_QUERY_PARAM, "token");
        responseContext = authRequestHandler.generateCode(info);
        params = responseContext.getQueryParams();
        Assert.assertNotNull(params.get(OAuthConstants.ACCESS_TOKEN_QUERY_PARAM));

        //check location header
        String locationHeader = responseContext.getLocationHeaderValue();
        String accessToken = responseContext.getQueryParams().get(OAuthConstants.ACCESS_TOKEN_QUERY_PARAM);
        String tokenType = responseContext.getQueryParams().get(OAuthConstants.TOKEN_TYPE_QUERY_PARAM);
        String exp = responseContext.getQueryParams().get(OAuthConstants.EXPERIES_IN_QUERY_PARAM);
        String expected =
                redirectUri + "?access_token=" + accessToken + "&scope=default&token_type=" + tokenType + "&expires_in="
                        + exp;
        Assert.assertEquals(expected, URLDecoder.decode(locationHeader, "UTF-8"));

        // check with redirect url
        info.put(OAuthConstants.REDIRECT_URI_QUERY_PARAM, redirectUri);
        responseContext = authRequestHandler.generateCode(info);
        params = responseContext.getQueryParams();
        Assert.assertNotNull(params.get(OAuthConstants.ACCESS_TOKEN_QUERY_PARAM));

        // check with scope
        info.put(OAuthConstants.SCOPE_QUERY_PARAM, scopes);
        responseContext = authRequestHandler.generateCode(info);
        params = responseContext.getQueryParams();
        Assert.assertNotNull(params.get(OAuthConstants.ACCESS_TOKEN_QUERY_PARAM));

    }

    @After
    public void cleanup() throws Exception {
        super.cleanup();
        log.info("Cleaned databases");
    }
}
