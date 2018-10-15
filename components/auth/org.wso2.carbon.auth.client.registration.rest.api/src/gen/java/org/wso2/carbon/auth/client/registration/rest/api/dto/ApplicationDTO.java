package org.wso2.carbon.auth.client.registration.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ApplicationDTO
 */
public class ApplicationDTO   {
  @SerializedName("client_id")
  private String clientId = null;

  @SerializedName("client_secret")
  private String clientSecret = null;

  @SerializedName("client_secret_expires_at")
  private String clientSecretExpiresAt = null;

  @SerializedName("redirect_uris")
  private List<String> redirectUris = new ArrayList<String>();

  @SerializedName("client_name")
  private String clientName = null;

  @SerializedName("grant_types")
  private List<String> grantTypes = new ArrayList<String>();

  @SerializedName("token_expire_time")
  private Long tokenExpireTime = null;

  @SerializedName("token_type_extension")
  private String tokenTypeExtension = null;

  public ApplicationDTO clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

   /**
   * Get clientId
   * @return clientId
  **/
  @ApiModelProperty(value = "")
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public ApplicationDTO clientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
    return this;
  }

   /**
   * Get clientSecret
   * @return clientSecret
  **/
  @ApiModelProperty(value = "")
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public ApplicationDTO clientSecretExpiresAt(String clientSecretExpiresAt) {
    this.clientSecretExpiresAt = clientSecretExpiresAt;
    return this;
  }

   /**
   * Get clientSecretExpiresAt
   * @return clientSecretExpiresAt
  **/
  @ApiModelProperty(value = "")
  public String getClientSecretExpiresAt() {
    return clientSecretExpiresAt;
  }

  public void setClientSecretExpiresAt(String clientSecretExpiresAt) {
    this.clientSecretExpiresAt = clientSecretExpiresAt;
  }

  public ApplicationDTO redirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
    return this;
  }

  public ApplicationDTO addRedirectUrisItem(String redirectUrisItem) {
    this.redirectUris.add(redirectUrisItem);
    return this;
  }

   /**
   * Get redirectUris
   * @return redirectUris
  **/
  @ApiModelProperty(value = "")
  public List<String> getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  public ApplicationDTO clientName(String clientName) {
    this.clientName = clientName;
    return this;
  }

   /**
   * Get clientName
   * @return clientName
  **/
  @ApiModelProperty(value = "")
  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public ApplicationDTO grantTypes(List<String> grantTypes) {
    this.grantTypes = grantTypes;
    return this;
  }

  public ApplicationDTO addGrantTypesItem(String grantTypesItem) {
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

  public ApplicationDTO tokenExpireTime(Long tokenExpireTime) {
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

  public ApplicationDTO tokenTypeExtension(String tokenTypeExtension) {
    this.tokenTypeExtension = tokenTypeExtension;
    return this;
  }

  /**
   * Get tokenTypeExtension
   *
   * @return tokenTypeExtension
   **/
  @ApiModelProperty(value = "")
  public String getTokenTypeExtension() {
    return tokenTypeExtension;
  }

  public void setTokenTypeExtension(String tokenTypeExtension) {
    this.tokenTypeExtension = tokenTypeExtension;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationDTO application = (ApplicationDTO) o;
    return Objects.equals(this.clientId, application.clientId) &&
        Objects.equals(this.clientSecret, application.clientSecret) &&
        Objects.equals(this.clientSecretExpiresAt, application.clientSecretExpiresAt) &&
        Objects.equals(this.redirectUris, application.redirectUris) &&
        Objects.equals(this.clientName, application.clientName) &&
        Objects.equals(this.grantTypes, application.grantTypes) &&
        Objects.equals(this.tokenExpireTime, application.tokenExpireTime) &&
        Objects.equals(this.tokenTypeExtension, application.tokenTypeExtension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientId, clientSecret, clientSecretExpiresAt, redirectUris, clientName, grantTypes, tokenExpireTime, tokenTypeExtension);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationDTO {\n");
    
    sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
    sb.append("    clientSecret: ").append(toIndentedString(clientSecret)).append("\n");
    sb.append("    clientSecretExpiresAt: ").append(toIndentedString(clientSecretExpiresAt)).append("\n");
    sb.append("    redirectUris: ").append(toIndentedString(redirectUris)).append("\n");
    sb.append("    clientName: ").append(toIndentedString(clientName)).append("\n");
    sb.append("    grantTypes: ").append(toIndentedString(grantTypes)).append("\n");
    sb.append("    tokenExpireTime: ").append(toIndentedString(tokenExpireTime)).append("\n");
    sb.append("    tokenTypeExtension: ").append(toIndentedString(tokenTypeExtension)).append("\n");
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

