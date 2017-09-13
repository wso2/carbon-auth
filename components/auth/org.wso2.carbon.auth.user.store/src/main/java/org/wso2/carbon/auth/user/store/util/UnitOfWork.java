/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.auth.user.store.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Support class to implement Unit of work pattern.
 */
public class UnitOfWork implements AutoCloseable {

    private static Logger log = LoggerFactory.getLogger(UnitOfWork.class);

    private Connection connection = null;
    private List<AutoCloseable> listToClose = new ArrayList<>();

    private UnitOfWork() throws SQLException {
        super();
    }

    /**
     * Begin the transaction process.
     *
     * @param connection Database connection.
     * @param autoCommit Set auto commit status of this transaction.
     * @return Instance of @see UnitOfWork.
     * @throws SQLException SQL Exception.
     */
    public static UnitOfWork beginTransaction(Connection connection, boolean autoCommit) throws SQLException {

        connection.setAutoCommit(autoCommit);
        return beginTransaction(connection);
    }

    /**
     * Begin the transaction process.
     *
     * @param connection Database connection
     * @return Instance of UnitOfWork
     * @throws SQLException SQL Exception.
     */
    public static UnitOfWork beginTransaction(Connection connection) throws SQLException {

        UnitOfWork unitOfWork = new UnitOfWork();
        unitOfWork.connection = connection;

        return unitOfWork;
    }

    /**
     * Queue any auto closable to close at the end.
     *
     * @param closeable Auto closable to be closed.
     */
    public void queueToClose(AutoCloseable closeable) {
        listToClose.add(closeable);
    }

    /**
     * End the transaction by committing to the database.
     *
     * @throws SQLException SQL Exception.
     */
    public void endTransaction() throws SQLException {
        connection.commit();
    }

    /**
     * Get the underlying connection object.
     *
     * @return instance of Connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Commit and close connection.
     *
     * @throws SQLException SQL Exception.
     */
    @Override
    public void close() throws SQLException {

        SQLException exception = null;

        for (AutoCloseable closeable : listToClose) {
            try {
                closeable.close();
            } catch (Exception e) {
                if (exception == null) {
                    exception = new SQLException(e);
                    log.debug("Exception occurred while closing the closable.", e);
                } else {
                    exception.addSuppressed(e);
                    log.debug("Exception occurred and suppressed while closing the closable.", e);
                }
            }
        }

        connection.close();

        if (exception != null) {
            throw exception;
        }
    }
}
