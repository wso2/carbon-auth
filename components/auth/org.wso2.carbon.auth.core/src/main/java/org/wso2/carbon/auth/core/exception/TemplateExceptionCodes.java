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

package org.wso2.carbon.auth.core.exception;

/**
 * A collection of defined parametrised exception codes
 */
public class TemplateExceptionCodes {

    private static final String ATTR_VAL_ALREADY_EXIST_MSG = "Attribute value already exists";
    private static final String ATTR_VAL_ALREADY_EXIST_DESC = "Attribute {} : {} already exists in a resource";

    /**
     * Unique attribute violation while adding a new resource
     */
    public static final class UniqueAttributeViolationAddingResource extends AbstractTemplateExceptionCode {
        public UniqueAttributeViolationAddingResource(String... params) {
            super(900600, ATTR_VAL_ALREADY_EXIST_MSG, 409, ATTR_VAL_ALREADY_EXIST_DESC,
                    params);
        }
    }

    /**
     * Unique attribute violation while updating a resource
     */
    public static final class UniqueAttributeViolationUpdatingResource extends AbstractTemplateExceptionCode {
        public UniqueAttributeViolationUpdatingResource(String... params) {
            super(900601, ATTR_VAL_ALREADY_EXIST_MSG, 400, ATTR_VAL_ALREADY_EXIST_DESC,
                    params);
        }
    }
}
