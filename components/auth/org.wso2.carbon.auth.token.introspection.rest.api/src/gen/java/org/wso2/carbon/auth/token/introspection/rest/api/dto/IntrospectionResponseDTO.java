package org.wso2.carbon.auth.token.introspection.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * IntrospectionResponseDTO
 */
public class IntrospectionResponseDTO   {
  @SerializedName("active")
  private Boolean active = null;

  @SerializedName("username")
  private String username = null;

  @SerializedName("scope")
  private String scope = null;

  @SerializedName("token_type")
  private String tokenType = null;

  @SerializedName("client_id")
  private String clientId = null;

  @SerializedName("exp")
  private Integer exp = null;

  @SerializedName("iat")
  private Integer iat = null;

  public IntrospectionResponseDTO active(Boolean active) {
    this.active = active;
    return this;
  }

   /**
   * Get active
   * @return active
  **/
  @ApiModelProperty(value = "")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public IntrospectionResponseDTO username(String username) {
    this.username = username;
    return this;
  }

   /**
   * Get username
   * @return username
  **/
  @ApiModelProperty(value = "")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public IntrospectionResponseDTO scope(String scope) {
    this.scope = scope;
    return this;
  }

   /**
   * Get scope
   * @return scope
  **/
  @ApiModelProperty(value = "")
  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public IntrospectionResponseDTO tokenType(String tokenType) {
    this.tokenType = tokenType;
    return this;
  }

   /**
   * Get tokenType
   * @return tokenType
  **/
  @ApiModelProperty(value = "")
  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public IntrospectionResponseDTO clientId(String clientId) {
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

  public IntrospectionResponseDTO exp(Integer exp) {
    this.exp = exp;
    return this;
  }

   /**
   * Get exp
   * @return exp
  **/
  @ApiModelProperty(value = "")
  public Integer getExp() {
    return exp;
  }

  public void setExp(Integer exp) {
    this.exp = exp;
  }

  public IntrospectionResponseDTO iat(Integer iat) {
    this.iat = iat;
    return this;
  }

   /**
   * Get iat
   * @return iat
  **/
  @ApiModelProperty(value = "")
  public Integer getIat() {
    return iat;
  }

  public void setIat(Integer iat) {
    this.iat = iat;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IntrospectionResponseDTO introspectionResponse = (IntrospectionResponseDTO) o;
    return Objects.equals(this.active, introspectionResponse.active) &&
        Objects.equals(this.username, introspectionResponse.username) &&
        Objects.equals(this.scope, introspectionResponse.scope) &&
        Objects.equals(this.tokenType, introspectionResponse.tokenType) &&
        Objects.equals(this.clientId, introspectionResponse.clientId) &&
        Objects.equals(this.exp, introspectionResponse.exp) &&
        Objects.equals(this.iat, introspectionResponse.iat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, username, scope, tokenType, clientId, exp, iat);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IntrospectionResponseDTO {\n");
    
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
    sb.append("    exp: ").append(toIndentedString(exp)).append("\n");
    sb.append("    iat: ").append(toIndentedString(iat)).append("\n");
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

