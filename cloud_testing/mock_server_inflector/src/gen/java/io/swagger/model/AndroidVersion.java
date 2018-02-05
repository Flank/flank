package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A version of the Android OS
 **/@ApiModel(description = "A version of the Android OS")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidVersion   {
  
  @JsonProperty("codeName")
  private String codeName = null;
  
  @JsonProperty("apiLevel")
  private Integer apiLevel = null;
  
  @JsonProperty("distribution")
  private Object distribution = null;
  
  @JsonProperty("tags")
  private List<String> tags = null;
  
  @JsonProperty("id")
  private String id = null;
  
  @JsonProperty("releaseDate")
  private Object releaseDate = null;
  
  @JsonProperty("versionString")
  private String versionString = null;
  
  /**
   * The code name for this Android version. Examples: \"JellyBean\", \"KitKat\" @OutputOnly
   **/
  public AndroidVersion codeName(String codeName) {
    this.codeName = codeName;
    return this;
  }

  @ApiModelProperty(value = "The code name for this Android version. Examples: \"JellyBean\", \"KitKat\" @OutputOnly")
  @JsonProperty("codeName")
  public String getCodeName() {
    return codeName;
  }
  public void setCodeName(String codeName) {
    this.codeName = codeName;
  }

  /**
   * The API level for this Android version. Examples: 18, 19 @OutputOnly
   **/
  public AndroidVersion apiLevel(Integer apiLevel) {
    this.apiLevel = apiLevel;
    return this;
  }

  @ApiModelProperty(value = "The API level for this Android version. Examples: 18, 19 @OutputOnly")
  @JsonProperty("apiLevel")
  public Integer getApiLevel() {
    return apiLevel;
  }
  public void setApiLevel(Integer apiLevel) {
    this.apiLevel = apiLevel;
  }

  /**
   * Market share for this version. @OutputOnly
   **/
  public AndroidVersion distribution(Object distribution) {
    this.distribution = distribution;
    return this;
  }

  @ApiModelProperty(value = "Market share for this version. @OutputOnly")
  @JsonProperty("distribution")
  public Object getDistribution() {
    return distribution;
  }
  public void setDistribution(Object distribution) {
    this.distribution = distribution;
  }

  /**
   * Tags for this dimension. Examples: \"default\", \"preview\", \"deprecated\"
   **/
  public AndroidVersion tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  @ApiModelProperty(value = "Tags for this dimension. Examples: \"default\", \"preview\", \"deprecated\"")
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * An opaque id for this Android version. Use this id to invoke the TestExecutionService. @OutputOnly
   **/
  public AndroidVersion id(String id) {
    this.id = id;
    return this;
  }

  @ApiModelProperty(value = "An opaque id for this Android version. Use this id to invoke the TestExecutionService. @OutputOnly")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The date this Android version became available in the market. @OutputOnly
   **/
  public AndroidVersion releaseDate(Object releaseDate) {
    this.releaseDate = releaseDate;
    return this;
  }

  @ApiModelProperty(value = "The date this Android version became available in the market. @OutputOnly")
  @JsonProperty("releaseDate")
  public Object getReleaseDate() {
    return releaseDate;
  }
  public void setReleaseDate(Object releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * A string representing this version of the Android OS. Examples: \"4.3\", \"4.4\" @OutputOnly
   **/
  public AndroidVersion versionString(String versionString) {
    this.versionString = versionString;
    return this;
  }

  @ApiModelProperty(value = "A string representing this version of the Android OS. Examples: \"4.3\", \"4.4\" @OutputOnly")
  @JsonProperty("versionString")
  public String getVersionString() {
    return versionString;
  }
  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidVersion androidVersion = (AndroidVersion) o;
    return Objects.equals(codeName, androidVersion.codeName) &&
        Objects.equals(apiLevel, androidVersion.apiLevel) &&
        Objects.equals(distribution, androidVersion.distribution) &&
        Objects.equals(tags, androidVersion.tags) &&
        Objects.equals(id, androidVersion.id) &&
        Objects.equals(releaseDate, androidVersion.releaseDate) &&
        Objects.equals(versionString, androidVersion.versionString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codeName, apiLevel, distribution, tags, id, releaseDate, versionString);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidVersion {\n");
    
    sb.append("    codeName: ").append(toIndentedString(codeName)).append("\n");
    sb.append("    apiLevel: ").append(toIndentedString(apiLevel)).append("\n");
    sb.append("    distribution: ").append(toIndentedString(distribution)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    releaseDate: ").append(toIndentedString(releaseDate)).append("\n");
    sb.append("    versionString: ").append(toIndentedString(versionString)).append("\n");
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



