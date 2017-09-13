package org.wso2.carbon.auth.scim.rest.api.dto;


import java.util.Objects;

/**
 * UserDTO
 */
public class UserDTO   {

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDTO {\n");
    
    sb.append("}");
    return sb.toString();
  }

}

