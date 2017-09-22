package org.wso2.carbon.auth.client.registration.exception;

import org.wso2.carbon.auth.core.exception.AuthException;

/**
 *  This is the Exception class for symmetric encryption related exceptions.
 */
public class PersistenceProcessorException extends AuthException {

    public PersistenceProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
