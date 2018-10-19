package org.wso2.carbon.auth.core;
/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

/**
 * Configurations related to auth core
 */
public class Constants {
    public static final String DATASOURCE = "dataSource";
    public static final String OBJECT_CLASS_NAME = "objectClass";
    public static final String ATTR_NAME_CN = "cn";
    public static final String ATTR_NAME_SN = "sn";
    public static final String ATTR_NAME_SCIMID = "scimid";

    public static final String LDAP_CONNECTOR_CLASS = "ldap.connectorClass";
    public static final String LDAP_CONNECTION_URL = "ldap.ConnectionURL";
    public static final String LDAP_CONNECTION_NAME = "ldap.ConnectionName";
    public static final String LDAP_CONNECTION_PASSWORD = "ldap.ConnectionPassword";
    public static final String LDAP_INITIAL_CONTEXT_FACTORY = "ldap.InitialContextFactory";
    public static final String LDAP_SECURITY_AUTHENTICATION = "ldap.SecurityAuthentication";

    public static final String LDAP_USER_SEARCH_BASE = "ldap.UserSearchBase";
    public static final String LDAP_USER_ENTRY_OBJECT_CLASS = "ldap.UserEntryObjectClass";
    public static final String LDAP_USERNAME_ATTRIBUTE = "ldap.UserNameAttribute";
    public static final String LDAP_USERNAME_SEARCH_FILTER = "ldap.UserNameSearchFilter";
    public static final String LDAP_USERNAME_LIST_FILTER = "ldap.UserNameListFilter";

    public static final String LDAP_GROUP_SEARCH_BASE = "ldap.GroupSearchBase";
    public static final String LDAP_GROUP_ENTRY_OBJECT_CLASS = "ldap.GroupEntryObjectClass";
    public static final String LDAP_GROUP_ATTRIBUTE = "ldap.GroupAttribute";
    public static final String LDAP_GROUP_SEARCH_FILTER = "ldap.GroupSearchFilter";
    public static final String LDAP_GROUP_LIST_FILTER = "ldap.GroupListFilter";
}
