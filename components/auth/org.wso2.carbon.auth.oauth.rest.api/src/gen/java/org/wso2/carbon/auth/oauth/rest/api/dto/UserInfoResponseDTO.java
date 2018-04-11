package org.wso2.carbon.auth.oauth.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * UserInfoResponseDTO
 */
public class UserInfoResponseDTO   {
  @SerializedName("userInfo")
  private String userInfo = null;

  public UserInfoResponseDTO userInfo(String userInfo) {
    this.userInfo = userInfo;
    return this;
  }

   /**
   * User info response value. 
   * @return userInfo
  **/
  @ApiModelProperty(required = true, value = "User info response value. ")
  public String getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(String userInfo) {
    this.userInfo = userInfo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfoResponseDTO userInfoResponse = (UserInfoResponseDTO) o;
    return Objects.equals(this.userInfo, userInfoResponse.userInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoResponseDTO {\n");
    
    sb.append("    userInfo: ").append(toIndentedString(userInfo)).append("\n");
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

