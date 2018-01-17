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


package org.wso2.carbon.auth.scope.registration.dao.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.core.datasource.DAOUtil;
import org.wso2.carbon.auth.scope.registration.constants.ScopeConstants;
import org.wso2.carbon.auth.scope.registration.dao.ScopeDAO;
import org.wso2.carbon.auth.scope.registration.dto.Scope;
import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Scope Data Access Object which handles scope data access.
 */
public class ScopeDAOImpl implements ScopeDAO {
    private static final Logger log = LoggerFactory.getLogger(ScopeDAOImpl.class);

    /**
     * Add a scope
     *
     * @param scope Scope
     * @throws ScopeDAOException IdentityOAuth2ScopeException
     */
    public void addScope(Scope scope) throws ScopeDAOException {
        if (scope == null) {
            if (log.isDebugEnabled()) {
                log.debug("Scope is not defined");
            }
            String msg = "Scope cannot be null";
            throw new ScopeDAOException(msg);
            //TODO Send Error
        }
        if (log.isDebugEnabled()) {
            log.debug("Adding scope :" + scope.getName());
        }
        try (Connection conn = DAOUtil.getAuthConnection()) {
            addScope(scope, conn);
            conn.commit();
        } catch (SQLException e) {
            String msg = "Error occurred while creating scope :" + scope.getName();
            throw new ScopeDAOException(msg);
        }
    }

    /**
     * Get Scopes with pagination
     *
     * @param offset start index of the result set
     * @param limit  number of elements of the result set
     * @return available scope list
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    public List<Scope> getScopesWithPagination(Integer offset, Integer limit) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get scopes with pagination; offset=" + offset + ", limit=" + limit);
        }

        List<Scope> scopes = new ArrayList<>();
        Map<Integer, Scope> scopeMap = new LinkedHashMap<>();
        try (Connection conn = DAOUtil.getAuthConnection()) {
            final String query;
            if (conn.getMetaData().getDriverName().contains("MySQL")
                    || conn.getMetaData().getDriverName().contains("H2")) {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_MYSQL;
                //TODO when other database support added we need to fix this
            /*else if (conn.getMetaData().getDatabaseProductName().contains("DB2")) {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_DB2SQL;
            } else if (conn.getMetaData().getDriverName().contains("MS SQL")) {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_MSSQL;
            } else if (conn.getMetaData().getDriverName().contains("Microsoft") || conn.getMetaData()
                    .getDriverName().contains("microsoft")) {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_MSSQL;
            } else if (conn.getMetaData().getDriverName().contains("PostgreSQL")) {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_POSTGRESQL;
            } else if (conn.getMetaData().getDriverName().contains("Informix")) {
                // Driver name = "IBM Informix JDBC Driver for IBM Informix Dynamic Server"
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_INFORMIX;
            } */
            } else {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_ORACLE;
            }
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, offset);
                preparedStatement.setInt(2, limit);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int scopeID = rs.getInt(1);
                        String name = rs.getString(2);
                        String displayName = rs.getString(3);
                        String description = rs.getString(4);
                        final String binding = rs.getString(5);
                        if (scopeMap.containsKey(scopeID) && scopeMap.get(scopeID) != null) {
                            scopeMap.get(scopeID).setName(name);
                            scopeMap.get(scopeID).setDescription(description);
                            scopeMap.get(scopeID).setDisplayName(displayName);
                            if (binding != null) {
                                if (scopeMap.get(scopeID).getBindings() != null) {
                                    scopeMap.get(scopeID).addBinding(binding);
                                } else {
                                    //TODO add bindings here
                                    scopeMap.get(scopeID).setBindings(new ArrayList<String>());
                                }
                            }
                        } else {
                            scopeMap.put(scopeID, new Scope(name, displayName, description, new ArrayList<String>()));
                            if (binding != null) {
                                scopeMap.get(scopeID).addBinding(binding);

                            }
                        }
                    }
                }
            }

            for (Map.Entry<Integer, Scope> entry : scopeMap.entrySet()) {
                scopes.add(entry.getValue());
            }
            return scopes;
        } catch (SQLException e) {
            String msg = "Error occurred while getting all scopes with pagination ";
            throw new ScopeDAOException(msg, e);
        }
    }

    /**
     * Get a scope by name
     *
     * @param name name of the scope
     * @return Scope for the provided ID
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public Scope getScopeByName(String name) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get scope by name called for scope name:" + name);
        }

        Scope scope = null;

        try (Connection conn = DAOUtil.getAuthConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.RETRIEVE_SCOPE_BY_NAME)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {

                    String description = null;
                    String displayName = null;
                    List<String> bindings = new ArrayList<>();

                    while (rs.next()) {
                        if (StringUtils.isBlank(displayName)) {
                            displayName = rs.getString(2);
                        }
                        if (StringUtils.isBlank(description)) {
                            description = rs.getString(3);
                        }
                        if (StringUtils.isNotBlank(rs.getString(4))) {
                            bindings.add(rs.getString(4));
                        }
                    }

                    if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(description)) {
                        scope = new Scope(name, displayName, description, bindings);
                    }
                }
            }
            return scope;
        } catch (SQLException e) {
            String msg = "Error occurred while getting scope by ID ";
            throw new ScopeDAOException(msg, e);
        }
    }

    /**
     * Get existence of scope for the provided scope name
     *
     * @param scopeName name of the scope
     * @return true if scope is exists
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public boolean isScopeExists(String scopeName) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Is scope exists called for scope:" + scopeName);
        }

        boolean isScopeExists = false;
        int scopeID = getScopeIDByName(scopeName);
        if (scopeID != ScopeConstants.INVALID_SCOPE_ID) {
            isScopeExists = true;
        }
        return isScopeExists;
    }

    /**
     * Get scope ID for the provided scope name
     *
     * @param scopeName name of the scope
     * @return scope ID for the provided scope name
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    private int getScopeIDByName(String scopeName) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get scope ID by name called for scope name:" + scopeName);
        }

        int scopeID = ScopeConstants.INVALID_SCOPE_ID;
        try (Connection conn = DAOUtil.getAuthConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.RETRIEVE_SCOPE_ID_BY_NAME)) {
                ps.setString(1, scopeName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        scopeID = rs.getInt(1);
                    }
                }
            }
            return scopeID;
        } catch (SQLException e) {
            String msg = "Error occurred while getting scope ID by name ";
            throw new ScopeDAOException(msg, e);
        }
    }

    /**
     * Delete a scope of the provided scope ID
     *
     * @param name name of the scope
     * @throws ScopeDAOException When error occured while deleting scope
     */
    public void deleteScopeByName(String name) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Delete scope by name for scope name:" + name);
        }

        try (Connection conn = DAOUtil.getAuthConnection()) {
            deleteScope(name, conn);
            conn.commit();
        } catch (SQLException e) {
            String msg = "Error occurred while deleting scopes ";
            throw new ScopeDAOException(msg, e);
        }
    }

    /**
     * Update a scope of the provided scope name
     *
     * @param updatedScope details of the updated scope
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public void updateScopeByName(Scope updatedScope) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Updae scope by name for scope name:" + updatedScope.getName());
        }

        try (Connection conn = DAOUtil.getAuthConnection()) {
            deleteScope(updatedScope.getName(), conn);
            addScope(updatedScope, conn);
            conn.commit();
        } catch (SQLException e) {
            String msg = "Error occurred while updating scope by ID ";
            throw new ScopeDAOException(msg, e);
        }
    }

    private void addScope(Scope scope, Connection conn) throws SQLException {
        //Adding the scope
        if (scope != null) {
            int scopeID = 0;
            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.ADD_SCOPE)) {
                ps.setString(1, scope.getName());
                ps.setString(2, scope.getDisplayName());
                ps.setString(3, scope.getDescription());
                ps.execute();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        scopeID = rs.getInt(1);
                    }
                }
            }

            //TODO when other database support added we need to fix this
            // some JDBC Drivers returns this in the result, some don't
            /*if (scopeID == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("JDBC Driver did not return the scope id, executing Select operation");
                }
                try (PreparedStatement ps = conn.prepareStatement(SQLQueries.RETRIEVE_SCOPE_ID_BY_NAME)) {
                    ps.setString(1, scope.getName());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            scopeID = rs.getInt(1);
                        }
                    }
                }
            }*/

            //Adding scope bindings
            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.ADD_SCOPE_BINDING)) {
                for (String binding : scope.getBindings()) {
                    if (binding != null) {
                        ps.setInt(1, scopeID);
                        ps.setString(2, binding);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }
        }
    }

    private void deleteScope(String name, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQLQueries.DELETE_SCOPE_BY_NAME)) {
            ps.setString(1, name);
            ps.execute();
        }
    }
}
