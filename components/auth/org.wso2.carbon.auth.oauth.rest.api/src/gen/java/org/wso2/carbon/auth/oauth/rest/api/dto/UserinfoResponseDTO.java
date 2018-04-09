package org.wso2.carbon.auth.oauth.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * UserinfoResponseDTO
 */
public class UserinfoResponseDTO   {
  @SerializedName("sub")
  private String sub = null;

  public UserinfoResponseDTO sub(String sub) {
    this.sub = sub;
    return this;
  }

   /**
   * The subject value. 
   * @return sub
  **/
  @ApiModelProperty(required = true, value = "The subject value. ")
  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserinfoResponseDTO userinfoResponse = (UserinfoResponseDTO) o;
    return Objects.equals(this.sub, userinfoResponse.sub);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sub);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserinfoResponseDTO {\n");
    
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
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

