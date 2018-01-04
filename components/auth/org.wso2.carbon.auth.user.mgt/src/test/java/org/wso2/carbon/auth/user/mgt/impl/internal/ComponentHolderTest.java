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
package org.wso2.carbon.auth.user.mgt.impl.internal;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.wso2.carbon.auth.user.mgt.internal.ComponentHolder;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;

public class ComponentHolderTest {
    @Mock
    UserStoreConnector userStoreConnector;

    @Test
    public void testComponentHolder() throws Exception {
        ComponentHolder componentHolder = new ComponentHolder();
        Assert.assertNotNull(componentHolder);

        ComponentHolder.setUserStoreConnector(userStoreConnector);
        UserStoreConnector connector = ComponentHolder.getUserStoreConnector();
        Assert.assertEquals(userStoreConnector, connector);
    }
}
