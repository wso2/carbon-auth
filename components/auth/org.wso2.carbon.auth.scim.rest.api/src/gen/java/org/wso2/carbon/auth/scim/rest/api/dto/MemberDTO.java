package org.wso2.carbon.auth.scim.rest.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * MemberDTO
 */
public class MemberDTO   {
  @JsonProperty("display")
  private String display = null;

  @JsonProperty("value")
  private String value = null;

  public MemberDTO display(String display) {
    this.display = display;
    return this;
  }

   /**
   * Display name of the member
   * @return display
  **/
  @ApiModelProperty(value = "Display name of the member")
  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public MemberDTO value(String value) {
    this.value = value;
    return this;
  }

   /**
   * User Id
   * @return value
  **/
  @ApiModelProperty(value = "User Id")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MemberDTO member = (MemberDTO) o;
    return Objects.equals(this.display, member.display) &&
        Objects.equals(this.value, member.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(display, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MemberDTO {\n");
    
    sb.append("    display: ").append(toIndentedString(display)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

