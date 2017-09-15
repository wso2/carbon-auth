package org.wso2.carbon.auth.client.registration.rest.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ApplicationDTO
 */
public class ApplicationDTO   {
  @JsonProperty("client_id")
  private String clientId = null;

  @JsonProperty("client_secret")
  private String clientSecret = null;

  @JsonProperty("client_secret_expires_at")
  private String clientSecretExpiresAt = null;

  @JsonProperty("redirect_uris")
  private List<String> redirectUris = new ArrayList<String>();

  @JsonProperty("client_name")
  private String clientName = null;

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
        Objects.equals(this.clientName, application.clientName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientId, clientSecret, clientSecretExpiresAt, redirectUris, clientName);
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

