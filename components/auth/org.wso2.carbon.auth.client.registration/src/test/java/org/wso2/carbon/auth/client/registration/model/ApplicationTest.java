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

package org.wso2.carbon.auth.client.registration.model;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.auth.client.registration.SampleTestObjectCreator;

public class ApplicationTest {
    @Test
    public void testEquals() throws Exception {
        Application application = SampleTestObjectCreator.createDefaultApplication();
        Application applicationClone = SampleTestObjectCreator.createDefaultApplication();
        applicationClone.setClientId(application.getClientId());
        applicationClone.setClientSecret(application.getClientSecret());
        Assert.assertEquals(application, applicationClone, "Two apps are not equal");
        Assert.assertEquals(application.hashCode(), applicationClone.hashCode());

        applicationClone.setClientName("changed");
        Assert.assertNotEquals(application, applicationClone, "Two apps was equal but not expected");
    }

    @Test
    public void testToString() throws Exception {
        Application application = SampleTestObjectCreator.createDefaultApplication();
        String appString = application.toString();
        Assert.assertTrue(appString.contains(application.getClientId()));
        Assert.assertTrue(appString.contains(application.getClientName()));
    }
}
