package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.AndroidDeviceCatalog;
import io.swagger.model.NetworkConfigurationCatalog;

/**
 * A description of a test environment.
 **/@ApiModel(description = "A description of a test environment.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TestEnvironmentCatalog   {
  
  @JsonProperty("androidDeviceCatalog")
  private AndroidDeviceCatalog androidDeviceCatalog = null;
  
  @JsonProperty("networkConfigurationCatalog")
  private NetworkConfigurationCatalog networkConfigurationCatalog = null;
  
  /**
   **/
  public TestEnvironmentCatalog androidDeviceCatalog(AndroidDeviceCatalog androidDeviceCatalog) {
    this.androidDeviceCatalog = androidDeviceCatalog;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("androidDeviceCatalog")
  public AndroidDeviceCatalog getAndroidDeviceCatalog() {
    return androidDeviceCatalog;
  }
  public void setAndroidDeviceCatalog(AndroidDeviceCatalog androidDeviceCatalog) {
    this.androidDeviceCatalog = androidDeviceCatalog;
  }

  /**
   **/
  public TestEnvironmentCatalog networkConfigurationCatalog(NetworkConfigurationCatalog networkConfigurationCatalog) {
    this.networkConfigurationCatalog = networkConfigurationCatalog;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("networkConfigurationCatalog")
  public NetworkConfigurationCatalog getNetworkConfigurationCatalog() {
    return networkConfigurationCatalog;
  }
  public void setNetworkConfigurationCatalog(NetworkConfigurationCatalog networkConfigurationCatalog) {
    this.networkConfigurationCatalog = networkConfigurationCatalog;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestEnvironmentCatalog testEnvironmentCatalog = (TestEnvironmentCatalog) o;
    return Objects.equals(androidDeviceCatalog, testEnvironmentCatalog.androidDeviceCatalog) &&
        Objects.equals(networkConfigurationCatalog, testEnvironmentCatalog.networkConfigurationCatalog);
  }

  @Override
  public int hashCode() {
    return Objects.hash(androidDeviceCatalog, networkConfigurationCatalog);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestEnvironmentCatalog {\n");
    
    sb.append("    androidDeviceCatalog: ").append(toIndentedString(androidDeviceCatalog)).append("\n");
    sb.append("    networkConfigurationCatalog: ").append(toIndentedString(networkConfigurationCatalog)).append("\n");
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



