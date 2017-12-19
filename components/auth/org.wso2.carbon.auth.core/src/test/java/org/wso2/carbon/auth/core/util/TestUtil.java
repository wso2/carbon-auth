/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.auth.core.util;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.wso2.carbon.auth.core.configuration.models.AttributeConfiguration;
import org.wso2.carbon.auth.core.configuration.models.UserStoreConfiguration;

public class TestUtil {

    /**
     * Checks whether the specified attribute exist in UserStoreConfiguration
     *
     * @param userStoreConfiguration UserStoreConfiguration object
     * @param attribute              Attribute string to check if exists
     * @return true if the specified attribute exist in UserStoreConfiguration
     */
    public static boolean isAttributeExists(UserStoreConfiguration userStoreConfiguration, String attribute) {
        Assert.assertTrue(userStoreConfiguration.isReadOnly());
        for (AttributeConfiguration attributeConfiguration : userStoreConfiguration.getAttributes()) {
            if (!StringUtils.isBlank(attribute) && attribute.equals(attributeConfiguration.getAttribute())) {
                return true;
            }
        }
        return false;
    }
}
