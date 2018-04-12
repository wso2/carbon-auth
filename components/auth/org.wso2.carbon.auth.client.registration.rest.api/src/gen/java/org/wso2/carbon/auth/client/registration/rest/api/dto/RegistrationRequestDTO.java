package org.wso2.carbon.auth.client.registration.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RegistrationRequestDTO
 */
public class RegistrationRequestDTO   {
  @SerializedName("redirect_uris")
  private List<String> redirectUris = new ArrayList<String>();

  @SerializedName("client_name")
  private String clientName = null;

  @SerializedName("grant_types")
  private List<String> grantTypes = new ArrayList<String>();

  @SerializedName("token_expire_time")
  private Long tokenExpireTime = null;

  public RegistrationRequestDTO redirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
    return this;
  }

  public RegistrationRequestDTO addRedirectUrisItem(String redirectUrisItem) {
    this.redirectUris.add(redirectUrisItem);
    return this;
  }

   /**
   * Get redirectUris
   * @return redirectUris
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  public RegistrationRequestDTO clientName(String clientName) {
    this.clientName = clientName;
    return this;
  }

   /**
   * Get clientName
   * @return clientName
  **/
  @ApiModelProperty(required = true, value = "")
  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public RegistrationRequestDTO grantTypes(List<String> grantTypes) {
    this.grantTypes = grantTypes;
    return this;
  }

  public RegistrationRequestDTO addGrantTypesItem(String grantTypesItem) {
    this.grantTypes.add(grantTypesItem);
    return this;
  }

   /**
   * Get grantTypes
   * @return grantTypes
  **/
  @ApiModelProperty(value = "")
  public List<String> getGrantTypes() {
    return grantTypes;
  }

  public void setGrantTypes(List<String> grantTypes) {
    this.grantTypes = grantTypes;
  }

  public RegistrationRequestDTO tokenExpireTime(Long tokenExpireTime) {
    this.tokenExpireTime = tokenExpireTime;
    return this;
  }

   /**
   * Get tokenExpireTime
   * @return tokenExpireTime
  **/
  @ApiModelProperty(value = "")
  public Long getTokenExpireTime() {
    return tokenExpireTime;
  }

  public void setTokenExpireTime(Long tokenExpireTime) {
    this.tokenExpireTime = tokenExpireTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegistrationRequestDTO registrationRequest = (RegistrationRequestDTO) o;
    return Objects.equals(this.redirectUris, registrationRequest.redirectUris) &&
        Objects.equals(this.clientName, registrationRequest.clientName) &&
        Objects.equals(this.grantTypes, registrationRequest.grantTypes) &&
        Objects.equals(this.tokenExpireTime, registrationRequest.tokenExpireTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(redirectUris, clientName, grantTypes, tokenExpireTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrationRequestDTO {\n");
    
    sb.append("    redirectUris: ").append(toIndentedString(redirectUris)).append("\n");
    sb.append("    clientName: ").append(toIndentedString(clientName)).append("\n");
    sb.append("    grantTypes: ").append(toIndentedString(grantTypes)).append("\n");
    sb.append("    tokenExpireTime: ").append(toIndentedString(tokenExpireTime)).append("\n");
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

