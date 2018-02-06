package org.wso2.carbon.auth.scim.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * name related attributes
 */
@ApiModel(description = "name related attributes")
public class User_nameDTO   {
  @SerializedName("formatted")
  private String formatted = null;

  @SerializedName("familyName")
  private String familyName = null;

  @SerializedName("givenName")
  private String givenName = null;

  @SerializedName("middleName")
  private String middleName = null;

  @SerializedName("honorificPrefix")
  private String honorificPrefix = null;

  @SerializedName("honorificSuffix")
  private String honorificSuffix = null;

  public User_nameDTO formatted(String formatted) {
    this.formatted = formatted;
    return this;
  }

   /**
   * The full name, including all middle names, titles, and suffixes as appropriate, formatted for display 
   * @return formatted
  **/
  @ApiModelProperty(example = "Ms. Barbara Jane Jensen, III", value = "The full name, including all middle names, titles, and suffixes as appropriate, formatted for display ")
  public String getFormatted() {
    return formatted;
  }

  public void setFormatted(String formatted) {
    this.formatted = formatted;
  }

  public User_nameDTO familyName(String familyName) {
    this.familyName = familyName;
    return this;
  }

   /**
   * The family name of the User, or last name in most Western languages 
   * @return familyName
  **/
  @ApiModelProperty(example = "Jensen", value = "The family name of the User, or last name in most Western languages ")
  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public User_nameDTO givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

   /**
   * The given name of the User, or first name in most Western languages 
   * @return givenName
  **/
  @ApiModelProperty(example = "Barbara", value = "The given name of the User, or first name in most Western languages ")
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public User_nameDTO middleName(String middleName) {
    this.middleName = middleName;
    return this;
  }

   /**
   * The middle name(s) of the User 
   * @return middleName
  **/
  @ApiModelProperty(example = "Jane", value = "The middle name(s) of the User ")
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public User_nameDTO honorificPrefix(String honorificPrefix) {
    this.honorificPrefix = honorificPrefix;
    return this;
  }

   /**
   * The honorific prefix(es) of the User, or title in most Western languages 
   * @return honorificPrefix
  **/
  @ApiModelProperty(example = "Ms.", value = "The honorific prefix(es) of the User, or title in most Western languages ")
  public String getHonorificPrefix() {
    return honorificPrefix;
  }

  public void setHonorificPrefix(String honorificPrefix) {
    this.honorificPrefix = honorificPrefix;
  }

  public User_nameDTO honorificSuffix(String honorificSuffix) {
    this.honorificSuffix = honorificSuffix;
    return this;
  }

   /**
   * The honorific suffix(es) of the User, or suffix in most Western languages 
   * @return honorificSuffix
  **/
  @ApiModelProperty(example = "III", value = "The honorific suffix(es) of the User, or suffix in most Western languages ")
  public String getHonorificSuffix() {
    return honorificSuffix;
  }

  public void setHonorificSuffix(String honorificSuffix) {
    this.honorificSuffix = honorificSuffix;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User_nameDTO userName = (User_nameDTO) o;
    return Objects.equals(this.formatted, userName.formatted) &&
        Objects.equals(this.familyName, userName.familyName) &&
        Objects.equals(this.givenName, userName.givenName) &&
        Objects.equals(this.middleName, userName.middleName) &&
        Objects.equals(this.honorificPrefix, userName.honorificPrefix) &&
        Objects.equals(this.honorificSuffix, userName.honorificSuffix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formatted, familyName, givenName, middleName, honorificPrefix, honorificSuffix);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User_nameDTO {\n");
    
    sb.append("    formatted: ").append(toIndentedString(formatted)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    honorificPrefix: ").append(toIndentedString(honorificPrefix)).append("\n");
    sb.append("    honorificSuffix: ").append(toIndentedString(honorificSuffix)).append("\n");
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

