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

package org.wso2.carbon.auth.user.store.configuration.models;

/**
 * Representing uniqueness
 */
public enum Uniqueness {
    // There can be same attribute value possessing in multiple resource in the same server
    NONE(0),

    // There can't be same attribute value possessing in multiple resource in the same server, but can be in
    //  two different servers
    SERVER(1),

    // There can't be same attribute value possessing in multiple resources globally in any server
    GLOBAL(2);

    private final int value;

    Uniqueness(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Uniqueness from(int value) {
        switch (value) {
        case 0:
            return NONE;
        case 1:
            return SERVER;
        case 2:
            return GLOBAL;
        default:
            return null;
        }
    }
}
