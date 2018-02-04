package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A reference to a file, used for user inputs.
 **/@ApiModel(description = "A reference to a file, used for user inputs.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class FileReference   {
  
  @JsonProperty("gcsPath")
  private String gcsPath = null;
  
  /**
   * A path to a file in Google Cloud Storage. Example: gs://build-app-1414623860166/app-debug-unaligned.apk
   **/
  public FileReference gcsPath(String gcsPath) {
    this.gcsPath = gcsPath;
    return this;
  }

  @ApiModelProperty(value = "A path to a file in Google Cloud Storage. Example: gs://build-app-1414623860166/app-debug-unaligned.apk")
  @JsonProperty("gcsPath")
  public String getGcsPath() {
    return gcsPath;
  }
  public void setGcsPath(String gcsPath) {
    this.gcsPath = gcsPath;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileReference fileReference = (FileReference) o;
    return Objects.equals(gcsPath, fileReference.gcsPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gcsPath);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileReference {\n");
    
    sb.append("    gcsPath: ").append(toIndentedString(gcsPath)).append("\n");
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



