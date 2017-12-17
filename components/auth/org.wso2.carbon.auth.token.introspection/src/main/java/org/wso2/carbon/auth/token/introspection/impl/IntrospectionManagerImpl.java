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
package org.wso2.carbon.auth.token.introspection.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.token.introspection.IntrospectionException;
import org.wso2.carbon.auth.token.introspection.IntrospectionManager;
import org.wso2.carbon.auth.token.introspection.TokenValidatorHandler;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionContext;
import org.wso2.carbon.auth.token.introspection.dto.IntrospectionResponse;

/**
 * Implementation of the IntrospectionManager
 */
public class IntrospectionManagerImpl implements IntrospectionManager {
    private static final Logger log = LoggerFactory.getLogger(IntrospectionManagerImpl.class);

    @Override
    public IntrospectionResponse introspect(String token) {
        IntrospectionContext context = new IntrospectionContext();
        IntrospectionResponse introspectionResponse = new IntrospectionResponse();
        if (StringUtils.isBlank(token)) {
            introspectionResponse.setActive(false);
            introspectionResponse.setError("Invalid input");
            return introspectionResponse;
        }
        context.setAccessToken(token);
        TokenValidatorHandler tokenValidatorHandler = new TokenValidatorHandlerImpl();
        try {
            tokenValidatorHandler.validate(context);
            return context.getIntrospectionResponse();
        } catch (IntrospectionException e) {
            log.error(e.getMessage(), e);
        }
        return introspectionResponse;
    }
}
