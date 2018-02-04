package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Key-value pair of detailed information about the client which invoked the test. For example {&#x27;Version&#x27;, &#x27;1.0&#x27;}, {&#x27;Release Track&#x27;, &#x27;BETA&#x27;}
 **/@ApiModel(description = "Key-value pair of detailed information about the client which invoked the test. For example {'Version', '1.0'}, {'Release Track', 'BETA'}")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class ClientInfoDetail   {
  
  @JsonProperty("key")
  private String key = null;
  
  @JsonProperty("value")
  private String value = null;
  
  /**
   * The key of detailed client information. Required
   **/
  public ClientInfoDetail key(String key) {
    this.key = key;
    return this;
  }

  @ApiModelProperty(value = "The key of detailed client information. Required")
  @JsonProperty("key")
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * The value of detailed client information. Required
   **/
  public ClientInfoDetail value(String value) {
    this.value = value;
    return this;
  }

  @ApiModelProperty(value = "The value of detailed client information. Required")
  @JsonProperty("value")
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
    ClientInfoDetail clientInfoDetail = (ClientInfoDetail) o;
    return Objects.equals(key, clientInfoDetail.key) &&
        Objects.equals(value, clientInfoDetail.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientInfoDetail {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
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



