package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A single Android device.
 **/@ApiModel(description = "A single Android device.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidDevice   {
  
  @JsonProperty("orientation")
  private String orientation = null;
  
  @JsonProperty("locale")
  private String locale = null;
  
  @JsonProperty("androidModelId")
  private String androidModelId = null;
  
  @JsonProperty("androidVersionId")
  private String androidVersionId = null;
  
  /**
   * How the device is oriented during the test. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidDevice orientation(String orientation) {
    this.orientation = orientation;
    return this;
  }

  @ApiModelProperty(value = "How the device is oriented during the test. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("orientation")
  public String getOrientation() {
    return orientation;
  }
  public void setOrientation(String orientation) {
    this.orientation = orientation;
  }

  /**
   * The locale the test device used for testing. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidDevice locale(String locale) {
    this.locale = locale;
    return this;
  }

  @ApiModelProperty(value = "The locale the test device used for testing. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("locale")
  public String getLocale() {
    return locale;
  }
  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * The id of the Android device to be used. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidDevice androidModelId(String androidModelId) {
    this.androidModelId = androidModelId;
    return this;
  }

  @ApiModelProperty(value = "The id of the Android device to be used. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("androidModelId")
  public String getAndroidModelId() {
    return androidModelId;
  }
  public void setAndroidModelId(String androidModelId) {
    this.androidModelId = androidModelId;
  }

  /**
   * The id of the Android OS version to be used. Use the EnvironmentDiscoveryService to get supported options. Required
   **/
  public AndroidDevice androidVersionId(String androidVersionId) {
    this.androidVersionId = androidVersionId;
    return this;
  }

  @ApiModelProperty(value = "The id of the Android OS version to be used. Use the EnvironmentDiscoveryService to get supported options. Required")
  @JsonProperty("androidVersionId")
  public String getAndroidVersionId() {
    return androidVersionId;
  }
  public void setAndroidVersionId(String androidVersionId) {
    this.androidVersionId = androidVersionId;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidDevice androidDevice = (AndroidDevice) o;
    return Objects.equals(orientation, androidDevice.orientation) &&
        Objects.equals(locale, androidDevice.locale) &&
        Objects.equals(androidModelId, androidDevice.androidModelId) &&
        Objects.equals(androidVersionId, androidDevice.androidVersionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orientation, locale, androidModelId, androidVersionId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidDevice {\n");
    
    sb.append("    orientation: ").append(toIndentedString(orientation)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
    sb.append("    androidModelId: ").append(toIndentedString(androidModelId)).append("\n");
    sb.append("    androidVersionId: ").append(toIndentedString(androidVersionId)).append("\n");
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



