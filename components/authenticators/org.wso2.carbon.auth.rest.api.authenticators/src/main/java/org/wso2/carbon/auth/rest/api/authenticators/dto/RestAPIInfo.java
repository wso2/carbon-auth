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

package org.wso2.carbon.auth.rest.api.authenticators.dto;

import io.swagger.models.Swagger;

/**
 * Metadata for RestAPI.
 */
public class RestAPIInfo {

    private String basePath;
    private Swagger swagger;
    private String yaml;

    public RestAPIInfo(String basePath, Swagger swagger, String yaml) {

        this.basePath = basePath;
        this.swagger = swagger;
        this.yaml = yaml;
    }

    public String getBasePath() {

        return basePath;
    }

    public Swagger getSwagger() {

        return swagger;
    }

    public String getYaml() {

        return yaml;
    }
}
