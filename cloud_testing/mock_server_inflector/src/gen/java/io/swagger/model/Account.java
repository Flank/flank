package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Identifies an account and how to log into it
 **/@ApiModel(description = "Identifies an account and how to log into it")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class Account   {
  
  @JsonProperty("googleAuto")
  private Object googleAuto = null;
  
  /**
   * An automatic google login account
   **/
  public Account googleAuto(Object googleAuto) {
    this.googleAuto = googleAuto;
    return this;
  }

  @ApiModelProperty(value = "An automatic google login account")
  @JsonProperty("googleAuto")
  public Object getGoogleAuto() {
    return googleAuto;
  }
  public void setGoogleAuto(Object googleAuto) {
    this.googleAuto = googleAuto;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account account = (Account) o;
    return Objects.equals(googleAuto, account.googleAuto);
  }

  @Override
  public int hashCode() {
    return Objects.hash(googleAuto);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Account {\n");
    
    sb.append("    googleAuto: ").append(toIndentedString(googleAuto)).append("\n");
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



