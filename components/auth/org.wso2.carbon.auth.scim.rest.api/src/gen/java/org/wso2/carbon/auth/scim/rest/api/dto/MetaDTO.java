package org.wso2.carbon.auth.scim.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * MetaDTO
 */
public class MetaDTO   {
  @SerializedName("created")
  private String created = null;

  @SerializedName("lastModified")
  private String lastModified = null;

  @SerializedName("location")
  private String location = null;

  @SerializedName("resourceType")
  private String resourceType = null;

  public MetaDTO created(String created) {
    this.created = created;
    return this;
  }

   /**
   * Date and time the resource has been created.
   * @return created
  **/
  @ApiModelProperty(value = "Date and time the resource has been created.")
  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public MetaDTO lastModified(String lastModified) {
    this.lastModified = lastModified;
    return this;
  }

   /**
   * Date and time the resource has been last modified.
   * @return lastModified
  **/
  @ApiModelProperty(value = "Date and time the resource has been last modified.")
  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public MetaDTO location(String location) {
    this.location = location;
    return this;
  }

   /**
   * Location URL to the resource.
   * @return location
  **/
  @ApiModelProperty(value = "Location URL to the resource.")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public MetaDTO resourceType(String resourceType) {
    this.resourceType = resourceType;
    return this;
  }

   /**
   * Resource type (Group or User)
   * @return resourceType
  **/
  @ApiModelProperty(value = "Resource type (Group or User)")
  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetaDTO meta = (MetaDTO) o;
    return Objects.equals(this.created, meta.created) &&
        Objects.equals(this.lastModified, meta.lastModified) &&
        Objects.equals(this.location, meta.location) &&
        Objects.equals(this.resourceType, meta.resourceType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, lastModified, location, resourceType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetaDTO {\n");
    
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    resourceType: ").append(toIndentedString(resourceType)).append("\n");
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

