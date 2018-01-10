/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.client.registration.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.client.registration.dao.ApplicationDAO;
import org.wso2.carbon.auth.client.registration.exception.ClientRegistrationDAOException;

/**
 * DAO Object creation factory
 */
public class DAOFactory {
    private static final Logger log = LoggerFactory.getLogger(DAOFactory.class);

    private static final String MYSQL = "MySQL";
    private static final String H2 = "H2";
    private static final String DB2 = "DB2";
    private static final String MICROSOFT = "Microsoft";
    private static final String MS_SQL = "MS SQL";
    private static final String POSTGRE = "PostgreSQL";
    private static final String ORACLE = "Oracle";

    public static ApplicationDAO getApplicationDAO() throws ClientRegistrationDAOException {
        return new ApplicationDAOImpl();
        // commented since core refactoring is required to fix activate order
        /*
        try (Connection connection = DAOUtil.getAuthConnection()) {
            String driverName = connection.getMetaData().getDriverName();

            if (!(driverName.contains(MYSQL) || driverName.contains(H2) || driverName.contains(DB2) ||
                    driverName.contains(MS_SQL) || driverName.contains(MICROSOFT) || driverName.contains(POSTGRE) ||
                    driverName.contains(ORACLE))) {
                throw new ClientRegistrationDAOException("Unhandled DB driver: " + driverName + " detected",
                        ExceptionCodes.DAO_EXCEPTION);
            }

            return new ApplicationDAOImpl();
        } catch (SQLException e) {
            throw new ClientRegistrationDAOException("Error while getting applicationDAO", e);
        }
        */
    }
    
}
