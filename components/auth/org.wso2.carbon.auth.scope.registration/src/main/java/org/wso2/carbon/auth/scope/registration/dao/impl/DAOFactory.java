package org.wso2.carbon.auth.scope.registration.dao.impl;

import org.wso2.carbon.auth.scope.registration.exceptions.ScopeDAOException;

/**
 * Scope DAO Factory which returns DAO object.
 */
public class DAOFactory {
    public static ScopeDAOImpl getTokenDAO() throws ScopeDAOException {
        return new ScopeDAOImpl();
    }
}
