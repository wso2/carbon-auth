package org.wso2.carbon.auth.rest.api.commons.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * ErrorDTO
 */
public class ErrorDTO   {
    @SerializedName("code")
    private Integer code = null;

    @SerializedName("message")
    private String message = null;

    @SerializedName("description")
    private String description = null;

    @SerializedName("moreInfo")
    private String moreInfo = null;

    public ErrorDTO code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     * @return code
     **/
    @ApiModelProperty(value = "")
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ErrorDTO message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get message
     * @return message
     **/
    @ApiModelProperty(value = "")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorDTO description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     * @return description
     **/
    @ApiModelProperty(value = "")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ErrorDTO moreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
        return this;
    }

    /**
     * Get moreInfo
     * @return moreInfo
     **/
    @ApiModelProperty(value = "")
    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorDTO error = (ErrorDTO) o;
        return Objects.equals(this.code, error.code) &&
                Objects.equals(this.message, error.message) &&
                Objects.equals(this.description, error.description) &&
                Objects.equals(this.moreInfo, error.moreInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, description, moreInfo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorDTO {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    moreInfo: ").append(toIndentedString(moreInfo)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

