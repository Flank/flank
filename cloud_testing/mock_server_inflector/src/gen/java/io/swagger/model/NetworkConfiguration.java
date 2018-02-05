package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class NetworkConfiguration   {
  
  @JsonProperty("id")
  private String id = null;
  
  @JsonProperty("upRule")
  private Object upRule = null;
  
  @JsonProperty("downRule")
  private Object downRule = null;
  
  /**
   * The unique opaque id for this network traffic configuration @OutputOnly
   **/
  public NetworkConfiguration id(String id) {
    this.id = id;
    return this;
  }

  @ApiModelProperty(value = "The unique opaque id for this network traffic configuration @OutputOnly")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The emulation rule applying to the upload traffic
   **/
  public NetworkConfiguration upRule(Object upRule) {
    this.upRule = upRule;
    return this;
  }

  @ApiModelProperty(value = "The emulation rule applying to the upload traffic")
  @JsonProperty("upRule")
  public Object getUpRule() {
    return upRule;
  }
  public void setUpRule(Object upRule) {
    this.upRule = upRule;
  }

  /**
   * The emulation rule applying to the download traffic
   **/
  public NetworkConfiguration downRule(Object downRule) {
    this.downRule = downRule;
    return this;
  }

  @ApiModelProperty(value = "The emulation rule applying to the download traffic")
  @JsonProperty("downRule")
  public Object getDownRule() {
    return downRule;
  }
  public void setDownRule(Object downRule) {
    this.downRule = downRule;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NetworkConfiguration networkConfiguration = (NetworkConfiguration) o;
    return Objects.equals(id, networkConfiguration.id) &&
        Objects.equals(upRule, networkConfiguration.upRule) &&
        Objects.equals(downRule, networkConfiguration.downRule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, upRule, downRule);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NetworkConfiguration {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    upRule: ").append(toIndentedString(upRule)).append("\n");
    sb.append("    downRule: ").append(toIndentedString(downRule)).append("\n");
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



