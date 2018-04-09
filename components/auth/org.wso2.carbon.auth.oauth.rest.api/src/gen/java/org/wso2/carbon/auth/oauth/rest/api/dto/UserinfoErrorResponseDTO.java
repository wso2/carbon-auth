package org.wso2.carbon.auth.oauth.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * UserinfoErrorResponseDTO
 */
public class UserinfoErrorResponseDTO   {
  @SerializedName("preProcessingError")
  private String preProcessingError = null;

  public UserinfoErrorResponseDTO preProcessingError(String preProcessingError) {
    this.preProcessingError = preProcessingError;
    return this;
  }

   /**
   * Error code classifying the type of preProcessingError.
   * @return preProcessingError
  **/
  @ApiModelProperty(required = true, value = "Error code classifying the type of preProcessingError.")
  public String getPreProcessingError() {
    return preProcessingError;
  }

  public void setPreProcessingError(String preProcessingError) {
    this.preProcessingError = preProcessingError;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserinfoErrorResponseDTO userinfoErrorResponse = (UserinfoErrorResponseDTO) o;
    return Objects.equals(this.preProcessingError, userinfoErrorResponse.preProcessingError);
  }

  @Override
  public int hashCode() {
    return Objects.hash(preProcessingError);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserinfoErrorResponseDTO {\n");
    
    sb.append("    preProcessingError: ").append(toIndentedString(preProcessingError)).append("\n");
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

