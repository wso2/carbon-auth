package org.wso2.carbon.auth.scim.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.dto.MetaDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.User_nameDTO;
import java.util.Objects;

/**
 * UserDTO
 */
public class UserDTO   {
  @SerializedName("id")
  private String id = null;

  @SerializedName("externalId")
  private String externalId = null;

  @SerializedName("meta")
  private MetaDTO meta = null;

  @SerializedName("userName")
  private String userName = null;

  @SerializedName("password")
  private String password = null;

  @SerializedName("schemas")
  private List<String> schemas = new ArrayList<String>();

  @SerializedName("name")
  private User_nameDTO name = null;

  @SerializedName("emails")
  private List<Object> emails = new ArrayList<Object>();

  public UserDTO id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserDTO externalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

   /**
   * Get externalId
   * @return externalId
  **/
  @ApiModelProperty(value = "")
  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public UserDTO meta(MetaDTO meta) {
    this.meta = meta;
    return this;
  }

   /**
   * Get meta
   * @return meta
  **/
  @ApiModelProperty(value = "")
  public MetaDTO getMeta() {
    return meta;
  }

  public void setMeta(MetaDTO meta) {
    this.meta = meta;
  }

  public UserDTO userName(String userName) {
    this.userName = userName;
    return this;
  }

   /**
   * Get userName
   * @return userName
  **/
  @ApiModelProperty(value = "")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public UserDTO password(String password) {
    this.password = password;
    return this;
  }

   /**
   * Get password
   * @return password
  **/
  @ApiModelProperty(value = "")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserDTO schemas(List<String> schemas) {
    this.schemas = schemas;
    return this;
  }

  public UserDTO addSchemasItem(String schemasItem) {
    this.schemas.add(schemasItem);
    return this;
  }

   /**
   * Get schemas
   * @return schemas
  **/
  @ApiModelProperty(value = "")
  public List<String> getSchemas() {
    return schemas;
  }

  public void setSchemas(List<String> schemas) {
    this.schemas = schemas;
  }

  public UserDTO name(User_nameDTO name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public User_nameDTO getName() {
    return name;
  }

  public void setName(User_nameDTO name) {
    this.name = name;
  }

  public UserDTO emails(List<Object> emails) {
    this.emails = emails;
    return this;
  }

  public UserDTO addEmailsItem(Object emailsItem) {
    this.emails.add(emailsItem);
    return this;
  }

   /**
   * Get emails
   * @return emails
  **/
  @ApiModelProperty(value = "")
  public List<Object> getEmails() {
    return emails;
  }

  public void setEmails(List<Object> emails) {
    this.emails = emails;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDTO user = (UserDTO) o;
    return Objects.equals(this.id, user.id) &&
        Objects.equals(this.externalId, user.externalId) &&
        Objects.equals(this.meta, user.meta) &&
        Objects.equals(this.userName, user.userName) &&
        Objects.equals(this.password, user.password) &&
        Objects.equals(this.schemas, user.schemas) &&
        Objects.equals(this.name, user.name) &&
        Objects.equals(this.emails, user.emails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, externalId, meta, userName, password, schemas, name, emails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    externalId: ").append(toIndentedString(externalId)).append("\n");
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    emails: ").append(toIndentedString(emails)).append("\n");
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

