package org.wso2.carbon.auth.scope.registration.rest.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * scopeDTO
 */
public class scopeDTO   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("bindings")
  private List<String> bindings = new ArrayList<String>();

  public scopeDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(required = true, value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public scopeDTO description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(required = true, value = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public scopeDTO bindings(List<String> bindings) {
    this.bindings = bindings;
    return this;
  }

  public scopeDTO addBindingsItem(String bindingsItem) {
    this.bindings.add(bindingsItem);
    return this;
  }

   /**
   * Get bindings
   * @return bindings
  **/
  @ApiModelProperty(value = "")
  public List<String> getBindings() {
    return bindings;
  }

  public void setBindings(List<String> bindings) {
    this.bindings = bindings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    scopeDTO scope = (scopeDTO) o;
    return Objects.equals(this.name, scope.name) &&
        Objects.equals(this.description, scope.description) &&
        Objects.equals(this.bindings, scope.bindings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, bindings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class scopeDTO {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    bindings: ").append(toIndentedString(bindings)).append("\n");
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

