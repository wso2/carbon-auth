package org.wso2.carbon.auth.scim.rest.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.dto.AttributeDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.SchemaDTO;
import java.util.Objects;

/**
 * UserSearchDTO
 */
public class UserSearchDTO   {
  @JsonProperty("startIndex")
  private Integer startIndex = null;

  @JsonProperty("count")
  private Integer count = null;

  @JsonProperty("filter")
  private String filter = null;

  @JsonProperty("schemas")
  private List<SchemaDTO> schemas = new ArrayList<SchemaDTO>();

  @JsonProperty("attributes")
  private List<AttributeDTO> attributes = new ArrayList<AttributeDTO>();

  public UserSearchDTO startIndex(Integer startIndex) {
    this.startIndex = startIndex;
    return this;
  }

   /**
   * The index of the first element in the result.  
   * @return startIndex
  **/
  @ApiModelProperty(example = "1", value = "The index of the first element in the result.  ")
  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public UserSearchDTO count(Integer count) {
    this.count = count;
    return this;
  }

   /**
   * Number of elements returned in the paginated result. 
   * @return count
  **/
  @ApiModelProperty(value = "Number of elements returned in the paginated result. ")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public UserSearchDTO filter(String filter) {
    this.filter = filter;
    return this;
  }

   /**
   * A filter expression to request a subset of the result.
   * @return filter
  **/
  @ApiModelProperty(value = "A filter expression to request a subset of the result.")
  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public UserSearchDTO schemas(List<SchemaDTO> schemas) {
    this.schemas = schemas;
    return this;
  }

  public UserSearchDTO addSchemasItem(SchemaDTO schemasItem) {
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

  public UserSearchDTO attributes(List<AttributeDTO> attributes) {
    this.attributes = attributes;
    return this;
  }

  public UserSearchDTO addAttributesItem(AttributeDTO attributesItem) {
    this.attributes.add(attributesItem);
    return this;
  }

   /**
   * Get attributes
   * @return attributes
  **/
  @ApiModelProperty(value = "")
  public List<AttributeDTO> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<AttributeDTO> attributes) {
    this.attributes = attributes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserSearchDTO userSearch = (UserSearchDTO) o;
    return Objects.equals(this.startIndex, userSearch.startIndex) &&
        Objects.equals(this.count, userSearch.count) &&
        Objects.equals(this.filter, userSearch.filter) &&
        Objects.equals(this.schemas, userSearch.schemas) &&
        Objects.equals(this.attributes, userSearch.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startIndex, count, filter, schemas, attributes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserSearchDTO {\n");
    
    sb.append("    startIndex: ").append(toIndentedString(startIndex)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    filter: ").append(toIndentedString(filter)).append("\n");
    sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
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

