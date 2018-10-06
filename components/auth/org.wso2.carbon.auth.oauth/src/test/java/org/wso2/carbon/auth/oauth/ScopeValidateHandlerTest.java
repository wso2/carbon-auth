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

package org.wso2.carbon.auth.oauth;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.oauth.callback.ScopeValidatorCallback;
import org.wso2.carbon.auth.oauth.exception.OAuthScopeException;
import org.wso2.carbon.auth.oauth.internal.ServiceReferenceHolder;

import java.lang.reflect.Field;

public class ScopeValidateHandlerTest {

    ScopeValidator scopeValidator = Mockito.mock(ScopeValidator.class);

    @BeforeMethod
    public void setup() throws NoSuchFieldException, IllegalAccessException {

        ServiceReferenceHolder instance = ServiceReferenceHolder.getInstance();
        Field field = ServiceReferenceHolder.class.getDeclaredField("scopeValidator");
        field.setAccessible(true);
        field.set(instance, scopeValidator);
    }

    @Test
    public void testScopeValidator() throws OAuthScopeException {

        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        Mockito.doNothing().when(scopeValidator).process(scopeValidatorCallback);
        ScopeValidateHandler.validate(scopeValidatorCallback);
        Assert.assertTrue(scopeValidatorCallback.isSuccessful());
    }

    @Test
    public void testScopeValidatorExceptionScenario() throws OAuthScopeException {

        ScopeValidatorCallback scopeValidatorCallback = new ScopeValidatorCallback();
        Mockito.doThrow(new OAuthScopeException("abd")).when(scopeValidator).process(scopeValidatorCallback);
        ScopeValidateHandler.validate(scopeValidatorCallback);
        Assert.assertFalse(scopeValidatorCallback.isSuccessful());
        Assert.assertEquals(scopeValidatorCallback.getErrorObject(), OAuth2Error.SERVER_ERROR);
    }
}
