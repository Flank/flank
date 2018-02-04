package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.FileReference;

/**
 * An opaque binary blob file to install on the device before the test starts
 **/@ApiModel(description = "An opaque binary blob file to install on the device before the test starts")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class ObbFile   {
  
  @JsonProperty("obb")
  private FileReference obb = null;
  
  @JsonProperty("obbFileName")
  private String obbFileName = null;
  
  /**
   **/
  public ObbFile obb(FileReference obb) {
    this.obb = obb;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("obb")
  public FileReference getObb() {
    return obb;
  }
  public void setObb(FileReference obb) {
    this.obb = obb;
  }

  /**
   * OBB file name which must conform to the format as specified by Android e.g. [main|patch].0300110.com.example.android.obb which will be installed into   <shared-storage>/Android/obb/<package-name>/ on the device Required
   **/
  public ObbFile obbFileName(String obbFileName) {
    this.obbFileName = obbFileName;
    return this;
  }

  @ApiModelProperty(value = "OBB file name which must conform to the format as specified by Android e.g. [main|patch].0300110.com.example.android.obb which will be installed into   <shared-storage>/Android/obb/<package-name>/ on the device Required")
  @JsonProperty("obbFileName")
  public String getObbFileName() {
    return obbFileName;
  }
  public void setObbFileName(String obbFileName) {
    this.obbFileName = obbFileName;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObbFile obbFile = (ObbFile) o;
    return Objects.equals(obb, obbFile.obb) &&
        Objects.equals(obbFileName, obbFile.obbFileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(obb, obbFileName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObbFile {\n");
    
    sb.append("    obb: ").append(toIndentedString(obb)).append("\n");
    sb.append("    obbFileName: ").append(toIndentedString(obbFileName)).append("\n");
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



