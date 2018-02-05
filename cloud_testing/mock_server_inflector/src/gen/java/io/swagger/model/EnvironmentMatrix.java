package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.AndroidDeviceList;

/**
 * The matrix of environments in which the test is to be executed.
 **/@ApiModel(description = "The matrix of environments in which the test is to be executed.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class EnvironmentMatrix   {
  
  @JsonProperty("androidDeviceList")
  private AndroidDeviceList androidDeviceList = null;
  
  @JsonProperty("androidMatrix")
  private Object androidMatrix = null;
  
  /**
   **/
  public EnvironmentMatrix androidDeviceList(AndroidDeviceList androidDeviceList) {
    this.androidDeviceList = androidDeviceList;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("androidDeviceList")
  public AndroidDeviceList getAndroidDeviceList() {
    return androidDeviceList;
  }
  public void setAndroidDeviceList(AndroidDeviceList androidDeviceList) {
    this.androidDeviceList = androidDeviceList;
  }

  /**
   * A matrix of Android devices.
   **/
  public EnvironmentMatrix androidMatrix(Object androidMatrix) {
    this.androidMatrix = androidMatrix;
    return this;
  }

  @ApiModelProperty(value = "A matrix of Android devices.")
  @JsonProperty("androidMatrix")
  public Object getAndroidMatrix() {
    return androidMatrix;
  }
  public void setAndroidMatrix(Object androidMatrix) {
    this.androidMatrix = androidMatrix;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnvironmentMatrix environmentMatrix = (EnvironmentMatrix) o;
    return Objects.equals(androidDeviceList, environmentMatrix.androidDeviceList) &&
        Objects.equals(androidMatrix, environmentMatrix.androidMatrix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(androidDeviceList, androidMatrix);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnvironmentMatrix {\n");
    
    sb.append("    androidDeviceList: ").append(toIndentedString(androidDeviceList)).append("\n");
    sb.append("    androidMatrix: ").append(toIndentedString(androidMatrix)).append("\n");
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



