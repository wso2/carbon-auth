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

package org.wso2.carbon.auth.scim.rest.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scim.SCIMManager;
import org.wso2.carbon.auth.scim.exception.AuthUserManagementException;
import org.wso2.carbon.auth.scim.rest.api.MeApiService;
import org.wso2.carbon.auth.scim.rest.api.NotFoundException;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMCharonInitializer;
import org.wso2.carbon.auth.scim.rest.api.util.SCIMRESTAPIUtils;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.UnauthorizedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.protocol.endpoints.MeResourceManager;
import org.wso2.msf4j.Request;

import javax.ws.rs.core.Response;

/**
 * REST API implementation class for logged in user
 */
public class MeApiServiceImpl extends MeApiService {
    private static final Logger log = LoggerFactory.getLogger(GroupsApiServiceImpl.class);

    public MeApiServiceImpl() {
        SCIMCharonInitializer.initializeOnceSCIMConfigs();
    }

    @Override
    public Response meGet(Request request) throws NotFoundException {
        String userName;
        UserManager userManager;
        //authenticate the user
        try {
            userName = SCIMRESTAPIUtils.getAuthenticatedUserName(request);
        } catch (CharonException e) {
            log.error(e.getMessage(), e);
            return SCIMRESTAPIUtils.getResponseFromCharonException(e);
        } catch (UnauthorizedException e) {
            log.error("User not authenticated", e);
            return SCIMRESTAPIUtils.getResponseFromCharonException(e);
        }

        //retrieve the user
        try {
            userManager = SCIMManager.getInstance().getCarbonAuthSCIMUserManager();
            MeResourceManager meResourceManager = new MeResourceManager();
            SCIMResponse scimResponse = meResourceManager.get(userName, userManager, null, null);
            return SCIMRESTAPIUtils.buildResponse(scimResponse);
        } catch (AuthUserManagementException e) {
            log.error("Error in initializing the CarbonAuthSCIMUserManager", e);
            return SCIMRESTAPIUtils.getSCIMInternalErrorResponse();
        }
    }
}
