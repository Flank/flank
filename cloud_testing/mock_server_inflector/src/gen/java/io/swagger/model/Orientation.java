package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen orientation of the device.
 **/@ApiModel(description = "Screen orientation of the device.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class Orientation   {
  
  @JsonProperty("id")
  private String id = null;
  
  @JsonProperty("tags")
  private List<String> tags = null;
  
  @JsonProperty("name")
  private String name = null;
  
  /**
   * The id for this orientation. Example: \"portrait\" @OutputOnly
   **/
  public Orientation id(String id) {
    this.id = id;
    return this;
  }

  @ApiModelProperty(value = "The id for this orientation. Example: \"portrait\" @OutputOnly")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Tags for this dimension. Examples: \"default\"
   **/
  public Orientation tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  @ApiModelProperty(value = "Tags for this dimension. Examples: \"default\"")
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * A human-friendly name for this orientation. Example: \"portrait\" @OutputOnly
   **/
  public Orientation name(String name) {
    this.name = name;
    return this;
  }

  @ApiModelProperty(value = "A human-friendly name for this orientation. Example: \"portrait\" @OutputOnly")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Orientation orientation = (Orientation) o;
    return Objects.equals(id, orientation.id) &&
        Objects.equals(tags, orientation.tags) &&
        Objects.equals(name, orientation.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tags, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Orientation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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



