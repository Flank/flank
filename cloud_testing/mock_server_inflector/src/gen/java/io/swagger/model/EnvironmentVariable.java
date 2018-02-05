package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A key-value pair passed as an environment variable to the test
 **/@ApiModel(description = "A key-value pair passed as an environment variable to the test")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class EnvironmentVariable   {
  
  @JsonProperty("value")
  private String value = null;
  
  @JsonProperty("key")
  private String key = null;
  
  /**
   * Value for the environment variable
   **/
  public EnvironmentVariable value(String value) {
    this.value = value;
    return this;
  }

  @ApiModelProperty(value = "Value for the environment variable")
  @JsonProperty("value")
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Key for the environment variable
   **/
  public EnvironmentVariable key(String key) {
    this.key = key;
    return this;
  }

  @ApiModelProperty(value = "Key for the environment variable")
  @JsonProperty("key")
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnvironmentVariable environmentVariable = (EnvironmentVariable) o;
    return Objects.equals(value, environmentVariable.value) &&
        Objects.equals(key, environmentVariable.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, key);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnvironmentVariable {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
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



