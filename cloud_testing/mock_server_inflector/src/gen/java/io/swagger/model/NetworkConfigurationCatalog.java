package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class NetworkConfigurationCatalog   {
  
  @JsonProperty("configurations")
  private List<Object> configurations = null;
  
  /**
   **/
  public NetworkConfigurationCatalog configurations(List<Object> configurations) {
    this.configurations = configurations;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("configurations")
  public List<Object> getConfigurations() {
    return configurations;
  }
  public void setConfigurations(List<Object> configurations) {
    this.configurations = configurations;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NetworkConfigurationCatalog networkConfigurationCatalog = (NetworkConfigurationCatalog) o;
    return Objects.equals(configurations, networkConfigurationCatalog.configurations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(configurations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NetworkConfigurationCatalog {\n");
    
    sb.append("    configurations: ").append(toIndentedString(configurations)).append("\n");
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



