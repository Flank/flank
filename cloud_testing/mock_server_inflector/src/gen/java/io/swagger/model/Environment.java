package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.AndroidDevice;

/**
 * The environment in which the test is run.
 **/@ApiModel(description = "The environment in which the test is run.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class Environment   {
  
  @JsonProperty("androidDevice")
  private AndroidDevice androidDevice = null;
  
  /**
   **/
  public Environment androidDevice(AndroidDevice androidDevice) {
    this.androidDevice = androidDevice;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("androidDevice")
  public AndroidDevice getAndroidDevice() {
    return androidDevice;
  }
  public void setAndroidDevice(AndroidDevice androidDevice) {
    this.androidDevice = androidDevice;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Environment environment = (Environment) o;
    return Objects.equals(androidDevice, environment.androidDevice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(androidDevice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Environment {\n");
    
    sb.append("    androidDevice: ").append(toIndentedString(androidDevice)).append("\n");
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



