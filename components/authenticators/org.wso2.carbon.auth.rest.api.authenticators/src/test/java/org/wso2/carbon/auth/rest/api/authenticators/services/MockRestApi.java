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

package org.wso2.carbon.auth.rest.api.authenticators.services;

import org.osgi.service.component.annotations.Component;
import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.Request;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component(
        name = "org.wso2.carbon.auth.scim.rest.api.MeApi",
        service = Microservice.class,
        immediate = true
)
@Path("/api/identity/scim2/v1.[\\d]+/Me")
@Consumes({"application/json"})
@Produces({"application/json"})
@ApplicationPath("/Me")
@io.swagger.annotations.Api(description = "the Me API")
public class MockRestApi {

    @OPTIONS
    @GET
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response meGet(@Context Request request) {

        return Response.noContent().build();
    }

    @POST
    @Path("/{apiId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response mePost(@Context Request request) {

        return Response.noContent().build();
    }

}

