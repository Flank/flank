package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.AndroidVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * The currently supported Android devices.
 **/@ApiModel(description = "The currently supported Android devices.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidDeviceCatalog   {
  
  @JsonProperty("models")
  private List<Object> models = null;
  
  @JsonProperty("versions")
  private List<AndroidVersion> versions = null;
  
  @JsonProperty("runtimeConfiguration")
  private Object runtimeConfiguration = null;
  
  /**
   * The set of supported Android device models. @OutputOnly
   **/
  public AndroidDeviceCatalog models(List<Object> models) {
    this.models = models;
    return this;
  }

  @ApiModelProperty(value = "The set of supported Android device models. @OutputOnly")
  @JsonProperty("models")
  public List<Object> getModels() {
    return models;
  }
  public void setModels(List<Object> models) {
    this.models = models;
  }

  /**
   * The set of supported Android OS versions. @OutputOnly
   **/
  public AndroidDeviceCatalog versions(List<AndroidVersion> versions) {
    this.versions = versions;
    return this;
  }

  @ApiModelProperty(value = "The set of supported Android OS versions. @OutputOnly")
  @JsonProperty("versions")
  public List<AndroidVersion> getVersions() {
    return versions;
  }
  public void setVersions(List<AndroidVersion> versions) {
    this.versions = versions;
  }

  /**
   * The set of supported runtime configurations. @OutputOnly
   **/
  public AndroidDeviceCatalog runtimeConfiguration(Object runtimeConfiguration) {
    this.runtimeConfiguration = runtimeConfiguration;
    return this;
  }

  @ApiModelProperty(value = "The set of supported runtime configurations. @OutputOnly")
  @JsonProperty("runtimeConfiguration")
  public Object getRuntimeConfiguration() {
    return runtimeConfiguration;
  }
  public void setRuntimeConfiguration(Object runtimeConfiguration) {
    this.runtimeConfiguration = runtimeConfiguration;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidDeviceCatalog androidDeviceCatalog = (AndroidDeviceCatalog) o;
    return Objects.equals(models, androidDeviceCatalog.models) &&
        Objects.equals(versions, androidDeviceCatalog.versions) &&
        Objects.equals(runtimeConfiguration, androidDeviceCatalog.runtimeConfiguration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(models, versions, runtimeConfiguration);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidDeviceCatalog {\n");
    
    sb.append("    models: ").append(toIndentedString(models)).append("\n");
    sb.append("    versions: ").append(toIndentedString(versions)).append("\n");
    sb.append("    runtimeConfiguration: ").append(toIndentedString(runtimeConfiguration)).append("\n");
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



