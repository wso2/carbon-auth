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

package org.wso2.carbon.auth.client.registration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class TestUtil {
    private static TestUtil instance = new TestUtil();

    private TestUtil() {
    }


    public static <T> String printDiff(T obj1, T obj2) throws IllegalAccessException {
        Field[] fields = FieldUtils.getAllFields(obj1.getClass());

        for (Field field : fields) {
            Object obj1Value = FieldUtils.readField(field, obj1, true);
            Object obj2Value = FieldUtils.readField(field, obj2, true);

            String obj1ValueString = "null";
            String obj2ValueString = "null";
            if (obj1Value != null) {
                obj1ValueString = obj1Value.toString();
            }
            if (obj2Value != null) {
                obj2ValueString = obj2Value.toString();
            }

            if (!Objects.equals(obj1Value, obj2Value)) {
                return "Diff detected for '" + field.getName() + "' " + System.lineSeparator() +
                        ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> LHS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                        System.lineSeparator() + obj1ValueString + System.lineSeparator() +
                        ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> LHS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                        System.lineSeparator() +
                        "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< RHS <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" +
                        System.lineSeparator() + obj2ValueString + System.lineSeparator() +
                        "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< RHS <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" +
                        System.lineSeparator();
            }
        }

        return null;
    }

    public static <T> String printListDiff(List<T> list1, List<T> list2) throws IllegalAccessException {
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException("The size of the list types are not the same");
        }

        for (int i = 0; i < list1.size(); ++i) {
            String diff = printDiff(list1.get(i), list2.get(i));

            if (diff != null) {
                return diff;
            }
        }

        return null;
    }

    public static TestUtil getInstance() {
        return instance;
    }

    /**
     * Utility for get Docker running host
     *
     * @return docker host
     * @throws URISyntaxException if docker Host url is malformed this will throw
     */
    public String getIpAddressOfContainer() throws URISyntaxException {
        String ip = "localhost";
        String dockerHost = System.getenv("DOCKER_HOST");
        if (!StringUtils.isEmpty(dockerHost)) {
            URI uri = new URI(dockerHost);
            ip = uri.getHost();
        }
        return ip;
    }
}
