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

package org.wso2.carbon.auth.rest.api.commons.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO class for ErrorListItem
 */
@ApiModel(description = "")
public class ErrorListItemDTO {

    @SerializedName("code")
    private Long code = null;

    @SerializedName("message")
    private String message = null;

    public ErrorListItemDTO() {
    }

    public ErrorListItemDTO(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Gets the error code.
     *
     * @return Error code . for ex :900404
     **/
    @ApiModelProperty(required = true, value = "")
    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    /**
     * Description about individual errors occurred
     *
     * @return error message.
     **/
    @ApiModelProperty(required = true, value = "Description about individual errors occurred")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorListItemDTO {\n");

        sb.append("  code: ").append(code).append("\n");
        sb.append("  message: ").append(message).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
