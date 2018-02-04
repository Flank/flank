package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A storage location within Google cloud storage (GCS).
 **/@ApiModel(description = "A storage location within Google cloud storage (GCS).")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class GoogleCloudStorage   {
  
  @JsonProperty("gcsPath")
  private String gcsPath = null;
  
  /**
   * The path to a directory in GCS that will eventually contain the results for this test. The requesting user must have write access on the bucket in the supplied path. Required
   **/
  public GoogleCloudStorage gcsPath(String gcsPath) {
    this.gcsPath = gcsPath;
    return this;
  }

  @ApiModelProperty(value = "The path to a directory in GCS that will eventually contain the results for this test. The requesting user must have write access on the bucket in the supplied path. Required")
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
    GoogleCloudStorage googleCloudStorage = (GoogleCloudStorage) o;
    return Objects.equals(gcsPath, googleCloudStorage.gcsPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gcsPath);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GoogleCloudStorage {\n");
    
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



