package org.wso2.carbon.auth.scim.rest.api.dto;


import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.auth.scim.rest.api.dto.MemberDTO;
import org.wso2.carbon.auth.scim.rest.api.dto.MetaDTO;
import java.util.Objects;

/**
 * GroupDTO
 */
public class GroupDTO   {
  @SerializedName("meta")
  private MetaDTO meta = null;

  @SerializedName("displayName")
  private String displayName = null;

  @SerializedName("schemas")
  private List<String> schemas = new ArrayList<String>();

  @SerializedName("members")
  private List<MemberDTO> members = new ArrayList<MemberDTO>();

  @SerializedName("id")
  private String id = null;

  public GroupDTO meta(MetaDTO meta) {
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

  public GroupDTO displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

   /**
   * Display name of the group
   * @return displayName
  **/
  @ApiModelProperty(value = "Display name of the group")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public GroupDTO schemas(List<String> schemas) {
    this.schemas = schemas;
    return this;
  }

  public GroupDTO addSchemasItem(String schemasItem) {
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

  public GroupDTO members(List<MemberDTO> members) {
    this.members = members;
    return this;
  }

  public GroupDTO addMembersItem(MemberDTO membersItem) {
    this.members.add(membersItem);
    return this;
  }

   /**
   * Get members
   * @return members
  **/
  @ApiModelProperty(value = "")
  public List<MemberDTO> getMembers() {
    return members;
  }

  public void setMembers(List<MemberDTO> members) {
    this.members = members;
  }

  public GroupDTO id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Group Id
   * @return id
  **/
  @ApiModelProperty(value = "Group Id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupDTO group = (GroupDTO) o;
    return Objects.equals(this.meta, group.meta) &&
        Objects.equals(this.displayName, group.displayName) &&
        Objects.equals(this.schemas, group.schemas) &&
        Objects.equals(this.members, group.members) &&
        Objects.equals(this.id, group.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(meta, displayName, schemas, members, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupDTO {\n");
    
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
    sb.append("    members: ").append(toIndentedString(members)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

