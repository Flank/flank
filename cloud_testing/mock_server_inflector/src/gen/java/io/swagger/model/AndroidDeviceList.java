package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of Android device configurations in which the test is to be executed.
 **/@ApiModel(description = "A list of Android device configurations in which the test is to be executed.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidDeviceList   {
  
  @JsonProperty("androidDevices")
  private List<Object> androidDevices = null;
  
  /**
   * A list of Android devices Required
   **/
  public AndroidDeviceList androidDevices(List<Object> androidDevices) {
    this.androidDevices = androidDevices;
    return this;
  }

  @ApiModelProperty(value = "A list of Android devices Required")
  @JsonProperty("androidDevices")
  public List<Object> getAndroidDevices() {
    return androidDevices;
  }
  public void setAndroidDevices(List<Object> androidDevices) {
    this.androidDevices = androidDevices;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidDeviceList androidDeviceList = (AndroidDeviceList) o;
    return Objects.equals(androidDevices, androidDeviceList.androidDevices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(androidDevices);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidDeviceList {\n");
    
    sb.append("    androidDevices: ").append(toIndentedString(androidDevices)).append("\n");
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



