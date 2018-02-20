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

package org.wso2.carbon.auth.core.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract template class for defining parametrised exception codes
 */
public class AbstractTemplateExceptionCode implements ExceptionCodeHandler {

    private long errorCode;
    private String errorMessage;
    private int httpStatusCode;
    private String errorDescription;
    private String[] params;
    private Pattern pattern = Pattern.compile("\\{\\}");

    AbstractTemplateExceptionCode(long errorCode, String errorMessage, int httpStatusCode, String errorDescription,
            String... params) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
        this.params = params;
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public long getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getErrorDescription() {
        StringBuilder stringBuilder = new StringBuilder(errorDescription);
        Matcher matcher = pattern.matcher(stringBuilder);
        for (int i = 0; (i < params.length) && matcher.find(); i++) {
            stringBuilder.replace(matcher.start(), matcher.end(), params[i]);
        }
        return stringBuilder.toString();
    }
}
