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

import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.oauth.ClientLookup;
import org.wso2.carbon.auth.oauth.OAuthConstants;
import org.wso2.carbon.auth.oauth.dao.OAuthDAO;
import org.wso2.carbon.auth.oauth.dto.AccessTokenContext;
import org.wso2.carbon.auth.oauth.exception.OAuthDAOException;

import java.util.HashMap;
import java.util.Map;

public class ClientLookupImplTest {

    private OAuthDAO oAuthDAO;

    @BeforeMethod
    public void setup() {

        oAuthDAO = Mockito.mock(OAuthDAO.class);
    }

    @Test
    public void testGetClientId() throws OAuthDAOException {

        ClientLookup clientLookup = new ClientLookupImpl(oAuthDAO);
        String authorization = "Basic YWRtaW46YWRtaW4=";
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        Map<String, String> queryParameters = new HashMap<>();
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        Mockito.when(oAuthDAO.isClientCredentialsValid("admin", "admin")).thenReturn(true);
        String clientId = clientLookup.getClientId(authorization, accessTokenContext, queryParameters, mutableBoolean);
        Assert.assertEquals(clientId, "admin");
    }

    @Test
    public void testGetClientIdSecretAsQueryParams() throws OAuthDAOException {

        ClientLookup clientLookup = new ClientLookupImpl(oAuthDAO);
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OAuthConstants.CLIENT_ID_QUERY_PARAM, "admin");
        queryParameters.put(OAuthConstants.CLIENT_SECRET_QUERY_PARAM, "admin");
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        Mockito.when(oAuthDAO.isClientCredentialsValid("admin", "admin")).thenReturn(true);
        String clientId = clientLookup.getClientId("", accessTokenContext, queryParameters, mutableBoolean);
        Assert.assertEquals(clientId, "admin");
    }
    @Test
    public void testInvalidRequest() throws OAuthDAOException {

        ClientLookup clientLookup = new ClientLookupImpl(oAuthDAO);
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        Map<String, String> queryParameters = new HashMap<>();
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        String clientId = clientLookup.getClientId("", accessTokenContext, queryParameters, mutableBoolean);
        Assert.assertNull(clientId);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.INVALID_REQUEST);
        Assert.assertEquals(mutableBoolean, new MutableBoolean(true));
    }

    @Test
    public void testGetClientIdInvalidClient() throws OAuthDAOException {

        ClientLookup clientLookup = new ClientLookupImpl(oAuthDAO);
        String authorization = "Basic YWRtaW46YWRtaW4=";
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        Map<String, String> queryParameters = new HashMap<>();
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        Mockito.when(oAuthDAO.isClientCredentialsValid("admin", "admin")).thenReturn(false);
        String clientId = clientLookup.getClientId(authorization, accessTokenContext, queryParameters, mutableBoolean);
        Assert.assertNull(clientId);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.INVALID_CLIENT);
        Assert.assertEquals(mutableBoolean, new MutableBoolean(true));
    }

    @Test
    public void testGetClientIdThrowsException() throws OAuthDAOException {

        ClientLookup clientLookup = new ClientLookupImpl(oAuthDAO);
        String authorization = "Basic YWRtaW46YWRtaW4=";
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        Map<String, String> queryParameters = new HashMap<>();
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        Mockito.when(oAuthDAO.isClientCredentialsValid("admin", "admin")).thenThrow(new OAuthDAOException(""));
        String clientId = clientLookup.getClientId(authorization, accessTokenContext, queryParameters, mutableBoolean);
        Assert.assertNull(clientId);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.SERVER_ERROR);
        Assert.assertEquals(mutableBoolean, new MutableBoolean(true));
    }

    @Test
    public void testGetClientIdThrowsParsingException() throws OAuthDAOException {

        ClientLookup clientLookup = new ClientLookupImpl(oAuthDAO);
        String authorization = "Basic YWRaW6WRtaW4=";
        AccessTokenContext accessTokenContext = new AccessTokenContext();
        Map<String, String> queryParameters = new HashMap<>();
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        String clientId = clientLookup.getClientId(authorization, accessTokenContext, queryParameters, mutableBoolean);
        Assert.assertNull(clientId);
        Assert.assertEquals(accessTokenContext.getErrorObject(), OAuth2Error.INVALID_REQUEST);
        Assert.assertEquals(mutableBoolean, new MutableBoolean(true));
    }
}
