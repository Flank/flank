package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of Android device configuration permutations is defined by the the cross-product of the given axes.  Internally, the given AndroidMatrix will be expanded into a set of AndroidDevices.  Only supported permutations will be instantiated.  Invalid permutations (e.g., incompatible models/versions) are ignored.
 **/@ApiModel(description = "A set of Android device configuration permutations is defined by the the cross-product of the given axes.  Internally, the given AndroidMatrix will be expanded into a set of AndroidDevices.  Only supported permutations will be instantiated.  Invalid permutations (e.g., incompatible models/versions) are ignored.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidMatrix   {
  
  @JsonProperty("orientations")
  private List<String> orientations = null;
  
  @JsonProperty("androidVersionIds")
  private List<String> androidVersionIds = null;
  
  @JsonProperty("locales")
  private List<String> locales = null;
  
  @JsonProperty("androidModelIds")
  private List<String> androidModelIds = null;
  
  /**
   * The set of orientations to test with. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidMatrix orientations(List<String> orientations) {
    this.orientations = orientations;
    return this;
  }

  @ApiModelProperty(value = "The set of orientations to test with. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("orientations")
  public List<String> getOrientations() {
    return orientations;
  }
  public void setOrientations(List<String> orientations) {
    this.orientations = orientations;
  }

  /**
   * The ids of the set of Android OS version to be used. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidMatrix androidVersionIds(List<String> androidVersionIds) {
    this.androidVersionIds = androidVersionIds;
    return this;
  }

  @ApiModelProperty(value = "The ids of the set of Android OS version to be used. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("androidVersionIds")
  public List<String> getAndroidVersionIds() {
    return androidVersionIds;
  }
  public void setAndroidVersionIds(List<String> androidVersionIds) {
    this.androidVersionIds = androidVersionIds;
  }

  /**
   * The set of locales the test device will enable for testing. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidMatrix locales(List<String> locales) {
    this.locales = locales;
    return this;
  }

  @ApiModelProperty(value = "The set of locales the test device will enable for testing. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("locales")
  public List<String> getLocales() {
    return locales;
  }
  public void setLocales(List<String> locales) {
    this.locales = locales;
  }

  /**
   * The ids of the set of Android device to be used. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidMatrix androidModelIds(List<String> androidModelIds) {
    this.androidModelIds = androidModelIds;
    return this;
  }

  @ApiModelProperty(value = "The ids of the set of Android device to be used. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("androidModelIds")
  public List<String> getAndroidModelIds() {
    return androidModelIds;
  }
  public void setAndroidModelIds(List<String> androidModelIds) {
    this.androidModelIds = androidModelIds;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidMatrix androidMatrix = (AndroidMatrix) o;
    return Objects.equals(orientations, androidMatrix.orientations) &&
        Objects.equals(androidVersionIds, androidMatrix.androidVersionIds) &&
        Objects.equals(locales, androidMatrix.locales) &&
        Objects.equals(androidModelIds, androidMatrix.androidModelIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orientations, androidVersionIds, locales, androidModelIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidMatrix {\n");
    
    sb.append("    orientations: ").append(toIndentedString(orientations)).append("\n");
    sb.append("    androidVersionIds: ").append(toIndentedString(androidVersionIds)).append("\n");
    sb.append("    locales: ").append(toIndentedString(locales)).append("\n");
    sb.append("    androidModelIds: ").append(toIndentedString(androidModelIds)).append("\n");
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



