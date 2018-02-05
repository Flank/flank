package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A location/region designation for language.
 **/@ApiModel(description = "A location/region designation for language.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class Locale   {
  
  @JsonProperty("name")
  private String name = null;
  
  @JsonProperty("id")
  private String id = null;
  
  @JsonProperty("tags")
  private List<String> tags = null;
  
  @JsonProperty("region")
  private String region = null;
  
  /**
   * A human-friendly name for this language/locale. Example: \"English\" @OutputOnly
   **/
  public Locale name(String name) {
    this.name = name;
    return this;
  }

  @ApiModelProperty(value = "A human-friendly name for this language/locale. Example: \"English\" @OutputOnly")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The id for this locale. Example: \"en_US\" @OutputOnly
   **/
  public Locale id(String id) {
    this.id = id;
    return this;
  }

  @ApiModelProperty(value = "The id for this locale. Example: \"en_US\" @OutputOnly")
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
  public Locale tags(List<String> tags) {
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
   * A human-friendly string representing the region for this locale. Example: \"United States\" Not present for every locale. @OutputOnly
   **/
  public Locale region(String region) {
    this.region = region;
    return this;
  }

  @ApiModelProperty(value = "A human-friendly string representing the region for this locale. Example: \"United States\" Not present for every locale. @OutputOnly")
  @JsonProperty("region")
  public String getRegion() {
    return region;
  }
  public void setRegion(String region) {
    this.region = region;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Locale locale = (Locale) o;
    return Objects.equals(name, locale.name) &&
        Objects.equals(id, locale.id) &&
        Objects.equals(tags, locale.tags) &&
        Objects.equals(region, locale.region);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id, tags, region);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Locale {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
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



