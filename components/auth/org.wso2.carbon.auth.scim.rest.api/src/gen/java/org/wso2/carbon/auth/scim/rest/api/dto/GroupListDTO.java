package org.wso2.carbon.auth.scim.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.dto.GroupDTO;
import java.util.Objects;

/**
 * GroupListDTO
 */
public class GroupListDTO   {
  @SerializedName("startIndex")
  private Integer startIndex = null;

  @SerializedName("itemsPerPage")
  private Integer itemsPerPage = null;

  @SerializedName("totalResults")
  private Integer totalResults = null;

  @SerializedName("schemas")
  private List<String> schemas = new ArrayList<String>();

  @SerializedName("Resources")
  private List<GroupDTO> resources = new ArrayList<GroupDTO>();

  public GroupListDTO startIndex(Integer startIndex) {
    this.startIndex = startIndex;
    return this;
  }

   /**
   * Number of Groups returned. 
   * @return startIndex
  **/
  @ApiModelProperty(example = "1", value = "Number of Groups returned. ")
  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public GroupListDTO itemsPerPage(Integer itemsPerPage) {
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

  public GroupListDTO totalResults(Integer totalResults) {
    this.totalResults = totalResults;
    return this;
  }

   /**
   * Total result count 
   * @return totalResults
  **/
  @ApiModelProperty(value = "Total result count ")
  public Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(Integer totalResults) {
    this.totalResults = totalResults;
  }

  public GroupListDTO schemas(List<String> schemas) {
    this.schemas = schemas;
    return this;
  }

  public GroupListDTO addSchemasItem(String schemasItem) {
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

  public GroupListDTO resources(List<GroupDTO> resources) {
    this.resources = resources;
    return this;
  }

  public GroupListDTO addResourcesItem(GroupDTO resourcesItem) {
    this.resources.add(resourcesItem);
    return this;
  }

   /**
   * Get resources
   * @return resources
  **/
  @ApiModelProperty(value = "")
  public List<GroupDTO> getResources() {
    return resources;
  }

  public void setResources(List<GroupDTO> resources) {
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
    GroupListDTO groupList = (GroupListDTO) o;
    return Objects.equals(this.startIndex, groupList.startIndex) &&
        Objects.equals(this.itemsPerPage, groupList.itemsPerPage) &&
        Objects.equals(this.totalResults, groupList.totalResults) &&
        Objects.equals(this.schemas, groupList.schemas) &&
        Objects.equals(this.resources, groupList.resources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startIndex, itemsPerPage, totalResults, schemas, resources);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupListDTO {\n");
    
    sb.append("    startIndex: ").append(toIndentedString(startIndex)).append("\n");
    sb.append("    itemsPerPage: ").append(toIndentedString(itemsPerPage)).append("\n");
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
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

