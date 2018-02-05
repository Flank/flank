package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.ObbFile;

/**
 * A single device file description.
 **/@ApiModel(description = "A single device file description.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class DeviceFile   {
  
  @JsonProperty("obbFile")
  private ObbFile obbFile = null;
  
  /**
   **/
  public DeviceFile obbFile(ObbFile obbFile) {
    this.obbFile = obbFile;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("obbFile")
  public ObbFile getObbFile() {
    return obbFile;
  }
  public void setObbFile(ObbFile obbFile) {
    this.obbFile = obbFile;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeviceFile deviceFile = (DeviceFile) o;
    return Objects.equals(obbFile, deviceFile.obbFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(obbFile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeviceFile {\n");
    
    sb.append("    obbFile: ").append(toIndentedString(obbFile)).append("\n");
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



