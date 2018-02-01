package org.wso2.carbon.auth.scim.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.dto.SchemaDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.UserDTO;
import java.util.Objects;

/**
 * UserListDTO
 */
public class UserListDTO   {
  @SerializedName("startIndex")
  private Integer startIndex = null;

  @SerializedName("itemsPerPage")
  private Integer itemsPerPage = null;

  @SerializedName("schemas")
  private List<SchemaDTO> schemas = new ArrayList<SchemaDTO>();

  @SerializedName("Resources")
  private List<UserDTO> resources = new ArrayList<UserDTO>();

  public UserListDTO startIndex(Integer startIndex) {
    this.startIndex = startIndex;
    return this;
  }

   /**
   * Number of Users returned. 
   * @return startIndex
  **/
  @ApiModelProperty(example = "1", value = "Number of Users returned. ")
  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public UserListDTO itemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
    return this;
  }

   /**
   * Items for page 
   * @return itemsPerPage
  **/
  @ApiModelProperty(value = "Items for page ")
  public Integer getItemsPerPage() {
    return itemsPerPage;
  }

  public void setItemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  public UserListDTO schemas(List<SchemaDTO> schemas) {
    this.schemas = schemas;
    return this;
  }

  public UserListDTO addSchemasItem(SchemaDTO schemasItem) {
    this.schemas.add(schemasItem);
    return this;
  }

   /**
   * Get schemas
   * @return schemas
  **/
  @ApiModelProperty(value = "")
  public List<SchemaDTO> getSchemas() {
    return schemas;
  }

  public void setSchemas(List<SchemaDTO> schemas) {
    this.schemas = schemas;
  }

  public UserListDTO resources(List<UserDTO> resources) {
    this.resources = resources;
    return this;
  }

  public UserListDTO addResourcesItem(UserDTO resourcesItem) {
    this.resources.add(resourcesItem);
    return this;
  }

   /**
   * Get resources
   * @return resources
  **/
  @ApiModelProperty(value = "")
  public List<UserDTO> getResources() {
    return resources;
  }

  public void setResources(List<UserDTO> resources) {
    this.resources = resources;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserListDTO userList = (UserListDTO) o;
    return Objects.equals(this.startIndex, userList.startIndex) &&
        Objects.equals(this.itemsPerPage, userList.itemsPerPage) &&
        Objects.equals(this.schemas, userList.schemas) &&
        Objects.equals(this.resources, userList.resources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startIndex, itemsPerPage, schemas, resources);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserListDTO {\n");
    
    sb.append("    startIndex: ").append(toIndentedString(startIndex)).append("\n");
    sb.append("    itemsPerPage: ").append(toIndentedString(itemsPerPage)).append("\n");
    sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
    sb.append("    resources: ").append(toIndentedString(resources)).append("\n");
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

