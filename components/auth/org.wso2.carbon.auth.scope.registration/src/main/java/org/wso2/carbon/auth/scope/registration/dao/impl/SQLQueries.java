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
package org.wso2.carbon.auth.scope.registration.dao.impl;

import org.wso2.carbon.auth.scope.registration.constants.ScopeConstants;

/**
 * Scopes Related SQL queries
 */
public class SQLQueries {

    /**
     * Scope related queries
     **/
    public static final String ADD_SCOPE =
            "INSERT INTO AUTH_OAUTH2_SCOPE (NAME, DISPLAY_NAME, DESCRIPTION) VALUES(?,?,?)";

    public static final String ADD_SCOPE_BINDING =
            "INSERT INTO AUTH_OAUTH2_SCOPE_BINDING (SCOPE_ID, SCOPE_BINDING) VALUES(?,?)";

    public static final String RETRIEVE_ALL_SCOPES = "SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME, " +
            "Scopes.DESCRIPTION, " +
            "ScopeBindings.SCOPE_BINDING FROM AUTH_OAUTH2_SCOPE AS Scopes " +
            "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON Scopes.SCOPE_ID=ScopeBindings.SCOPE_ID ";

    public static final String RETRIEVE_SCOPES_WITH_PAGINATION_MYSQL =
            "SELECT filteredScopes.SCOPE_ID, filteredScopes.NAME, filteredScopes.DISPLAY_NAME, " +
                    "filteredScopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM " +
                    "(SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME, Scopes.DESCRIPTION FROM " +
                    "AUTH_OAUTH2_SCOPE AS Scopes " +
                    " OFFSET ? LIMIT ? ) AS filteredScopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON filteredScopes.SCOPE_ID=ScopeBindings" +
                    ".SCOPE_ID ORDER BY filteredScopes.NAME";

    //todo check offset is not added
    public static final String RETRIEVE_SCOPES_WITH_PAGINATION_ORACLE =
            "SELECT filteredScopes.SCOPE_ID, filteredScopes.NAME, filteredScopes.DISPLAY_NAME, " +
                    "filteredScopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM " +
                    "(SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME, Scopes.DESCRIPTION FROM " +
                    "AUTH_OAUTH2_SCOPE AS Scopes " +
                    "WHERE ROWNUM < :limit;) AS filteredScopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON " +
                    "filteredScopes.SCOPE_ID=ScopeBindings.SCOPE_ID ORDER BY filteredScopes.NAME";

    //todo check offset is not added
    public static final String RETRIEVE_SCOPES_WITH_PAGINATION_DB2SQL =
            "SELECT filteredScopes.SCOPE_ID, filteredScopes.NAME, filteredScopes.DISPLAY_NAME, " +
                    "filteredScopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM " +
                    "(SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME, Scopes.DESCRIPTION FROM " +
                    "AUTH_OAUTH2_SCOPE AS Scopes " +
                    "WHERE Scopes.TENANT_ID = :" + ScopeConstants.SQLPlaceholders.TENANT_ID +
                    "; FETCH FIRST :limit; ROWS ONLY) AS filteredScopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON filteredScopes.SCOPE_ID=ScopeBindings" +
                    ".SCOPE_ID ORDER BY filteredScopes.NAME";

    //todo check offset is not added
    public static final String RETRIEVE_SCOPES_WITH_PAGINATION_MSSQL =
            "SELECT filteredScopes.SCOPE_ID, filteredScopes.NAME, filteredScopes.DISPLAY_NAME, " +
                    "filteredScopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM " +
                    "(SELECT TOP :limit; SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME," +
                    " Scopes.DESCRIPTION FROM AUTH_OAUTH2_SCOPE AS Scopes " +
                    "WHERE Scopes.TENANT_ID = :" + ScopeConstants.SQLPlaceholders.TENANT_ID +
                    ";) AS filteredScopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON " +
                    "filteredScopes.SCOPE_ID=ScopeBindings.SCOPE_ID ORDER BY filteredScopes.NAME";

    //todo check offset is not added
    public static final String RETRIEVE_SCOPES_WITH_PAGINATION_POSTGRESQL =
            "SELECT filteredScopes.SCOPE_ID, filteredScopes.NAME, filteredScopes.DISPLAY_NAME, " +
                    "filteredScopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM " +
                    "(SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME, Scopes.DESCRIPTION FROM " +
                    "AUTH_OAUTH2_SCOPE AS Scopes " +
                    "WHERE Scopes.TENANT_ID = :" + ScopeConstants.SQLPlaceholders.TENANT_ID +
                    "; LIMIT :limit;) AS filteredScopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON " +
                    "filteredScopes.SCOPE_ID=ScopeBindings.SCOPE_ID ORDER BY filteredScopes.NAME";

    //todo check offset is not added
    public static final String RETRIEVE_SCOPES_WITH_PAGINATION_INFORMIX =
            "SELECT filteredScopes.SCOPE_ID, filteredScopes.NAME, filteredScopes.DISPLAY_NAME, " +
                    "filteredScopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM " +
                    "(SELECT FIRST :limit; * FROM  (SELECT Scopes.SCOPE_ID, Scopes.NAME, Scopes.DISPLAY_NAME, " +
                    "Scopes.DESCRIPTION FROM AUTH_OAUTH2_SCOPE AS Scopes " +
                    "WHERE Scopes.TENANT_ID = :" + ScopeConstants.SQLPlaceholders.TENANT_ID +
                    ";) RESULT) AS filteredScopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON " +
                    "filteredScopes.SCOPE_ID=ScopeBindings.SCOPE_ID ORDER BY filteredScopes.NAME";

    public static final String RETRIEVE_SCOPE_BY_NAME = "SELECT Scopes.NAME, Scopes.DISPLAY_NAME, " + 
            "Scopes.DESCRIPTION, ScopeBindings.SCOPE_BINDING FROM AUTH_OAUTH2_SCOPE AS Scopes " +
            "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON Scopes.SCOPE_ID=ScopeBindings.SCOPE_ID " +
            "WHERE Scopes.NAME=?";

    public static final String RETRIEVE_SCOPE_ID_BY_NAME = "SELECT SCOPE_ID FROM AUTH_OAUTH2_SCOPE " +
            "WHERE NAME=?";

    public static final String DELETE_SCOPE_BY_NAME = "DELETE FROM AUTH_OAUTH2_SCOPE WHERE NAME = ?";


    public static final String RETRIEVE_SCOPE_NAME_FOR_RESOURCE = "SELECT Scopes.NAME FROM AUTH_OAUTH2_SCOPE AS " +
            "Scopes, " +
            "AUTH_OAUTH2_RESOURCE_SCOPE AS ScopeResources WHERE RESOURCE_PATH = ? AND" +
            " ScopeResources.SCOPE_ID = Scopes.SCOPE_ID";

    public static final String RETRIEVE_SCOPE_WITH_TENANT_FOR_RESOURCE = "SELECT Scopes.NAME, Scopes.TENANT_ID FROM " +
            "AUTH_OAUTH2_SCOPE AS Scopes, AUTH_OAUTH2_RESOURCE_SCOPE AS ScopeResources WHERE RESOURCE_PATH = ? AND " +
            "ScopeResources.SCOPE_ID = Scopes.SCOPE_ID";

    public static final String RETRIEVE_BINDINGS_OF_SCOPE =
            "SELECT ScopeBindings.SCOPE_BINDING FROM AUTH_OAUTH2_SCOPE AS Scopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON Scopes.SCOPE_ID=ScopeBindings.SCOPE_ID " +
                    "WHERE Scopes.NAME = ?";

    public static final String RETRIEVE_BINDINGS_OF_SCOPE_FOR_TENANT =
            "SELECT ScopeBindings.SCOPE_BINDING FROM AUTH_OAUTH2_SCOPE AS Scopes " +
                    "LEFT JOIN AUTH_OAUTH2_SCOPE_BINDING AS ScopeBindings ON Scopes.SCOPE_ID=ScopeBindings.SCOPE_ID " +
                    "WHERE Scopes.NAME = ? AND TENANT_ID = ?";

    private SQLQueries() {
    }
}
