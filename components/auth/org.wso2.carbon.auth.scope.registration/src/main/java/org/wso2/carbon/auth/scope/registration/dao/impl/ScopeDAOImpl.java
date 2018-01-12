package org.wso2.carbon.auth.scope.registration.dao.impl;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Get all available scopes
     *
     * @param tenantID tenant ID
     * @return available scope list
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public Set<Scope> getAllScopes(int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get all scopes for tenantId  :" + tenantID);
        }

        Set<Scope> scopes = new HashSet<>();
        Map<Integer, Scope> scopeMap = new HashMap<>();

        try (Connection conn = DAOUtil.getAuthConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.RETRIEVE_ALL_SCOPES)) {
                ps.setInt(1, tenantID);
                try (ResultSet rs = ps.executeQuery()) {
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
            String msg = "Error occurred while getting all scopes ";
            throw new ScopeDAOException(msg, e);
        }
    }

    /**
     * Get Scopes with pagination
     *
     * @param offset   start index of the result set
     * @param limit    number of elements of the result set
     * @param tenantID tenant ID
     * @return available scope list
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public Set<Scope> getScopesWithPagination(Integer offset, Integer limit, int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get scopes with pagination for tenantId  :" + tenantID);
        }

        Set<Scope> scopes = new HashSet<>();
        Map<Integer, Scope> scopeMap = new HashMap<>();
        try (Connection conn = DAOUtil.getAuthConnection()) {
            String query;
            if (conn.getMetaData().getDriverName().contains("MySQL")
                    || conn.getMetaData().getDriverName().contains("H2")) {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_MYSQL;
            } else if (conn.getMetaData().getDatabaseProductName().contains("DB2")) {
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
            } else {
                query = SQLQueries.RETRIEVE_SCOPES_WITH_PAGINATION_ORACLE;
            }
            try (PreparedStatement preparedStatement = conn.prepareStatement(SQLQueries.RETRIEVE_SCOPE_BY_NAME)) {
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
     * @param name     name of the scope
     * @param tenantID tenant ID
     * @return Scope for the provided ID
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public Scope getScopeByName(String name, int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get scope by name called for scope name:" + name);
        }

        Scope scope = null;

        try (Connection conn = DAOUtil.getAuthConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.RETRIEVE_SCOPE_BY_NAME)) {
                ps.setString(1, name);
                ps.setInt(2, tenantID);
                try (ResultSet rs = ps.executeQuery()) {

                    String description = null;
                    String displayName = null;
                    List<String> bindings = new ArrayList<>();

                    while (rs.next()) {
                        if (StringUtils.isBlank(description)) {
                            description = rs.getString(2);
                        }
                        if (StringUtils.isBlank(displayName)) {
                            displayName = rs.getString(3);
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
     * @param tenantID  tenant ID
     * @return true if scope is exists
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public boolean isScopeExists(String scopeName, int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Is scope exists called for scope:" + scopeName);
        }

        boolean isScopeExists = false;
        int scopeID = getScopeIDByName(scopeName, tenantID);
        if (scopeID != ScopeConstants.INVALID_SCOPE_ID) {
            isScopeExists = true;
        }
        return isScopeExists;
    }

    /**
     * Get scope ID for the provided scope name
     *
     * @param scopeName name of the scope
     * @param tenantID  tenant ID
     * @return scope ID for the provided scope name
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public int getScopeIDByName(String scopeName, int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Get scope ID by name called for scope name:" + scopeName);
        }

        int scopeID = ScopeConstants.INVALID_SCOPE_ID;
        try (Connection conn = DAOUtil.getAuthConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(SQLQueries.RETRIEVE_SCOPE_ID_BY_NAME)) {
                ps.setString(1, scopeName);
                ps.setInt(2, tenantID);
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
     * @param name     name of the scope
     * @param tenantID tenant ID
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public void deleteScopeByName(String name, int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Delete scope by name for scope name:" + name);
        }

        try (Connection conn = DAOUtil.getAuthConnection()) {

            deleteScope(name, tenantID, conn);
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
     * @param tenantID     tenant ID
     * @throws ScopeDAOException IdentityOAuth2ScopeServerException
     */
    public void updateScopeByName(Scope updatedScope, int tenantID) throws ScopeDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Updae scope by name for scope name:" + updatedScope.getName());
        }

        try (Connection conn = DAOUtil.getAuthConnection()) {
            deleteScope(updatedScope.getName(), tenantID, conn);
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

            // some JDBC Drivers returns this in the result, some don't
            if (scopeID == 0) {
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
            }

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

    private void deleteScope(String name, int tenantID, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQLQueries.DELETE_SCOPE_BY_NAME)) {
            ps.setString(1, name);
            ps.setInt(2, tenantID);
            ps.execute();
        }
    }
}
