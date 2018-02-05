package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Locale;
import io.swagger.model.Orientation;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration that can be selected at the time a test is run.
 **/@ApiModel(description = "Configuration that can be selected at the time a test is run.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidRuntimeConfiguration   {
  
  @JsonProperty("orientations")
  private List<Orientation> orientations = null;
  
  @JsonProperty("locales")
  private List<Locale> locales = null;
  
  /**
   * The set of available orientations. @OutputOnly
   **/
  public AndroidRuntimeConfiguration orientations(List<Orientation> orientations) {
    this.orientations = orientations;
    return this;
  }

  @ApiModelProperty(value = "The set of available orientations. @OutputOnly")
  @JsonProperty("orientations")
  public List<Orientation> getOrientations() {
    return orientations;
  }
  public void setOrientations(List<Orientation> orientations) {
    this.orientations = orientations;
  }

  /**
   * The set of available locales. @OutputOnly
   **/
  public AndroidRuntimeConfiguration locales(List<Locale> locales) {
    this.locales = locales;
    return this;
  }

  @ApiModelProperty(value = "The set of available locales. @OutputOnly")
  @JsonProperty("locales")
  public List<Locale> getLocales() {
    return locales;
  }
  public void setLocales(List<Locale> locales) {
    this.locales = locales;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidRuntimeConfiguration androidRuntimeConfiguration = (AndroidRuntimeConfiguration) o;
    return Objects.equals(orientations, androidRuntimeConfiguration.orientations) &&
        Objects.equals(locales, androidRuntimeConfiguration.locales);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orientations, locales);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidRuntimeConfiguration {\n");
    
    sb.append("    orientations: ").append(toIndentedString(orientations)).append("\n");
    sb.append("    locales: ").append(toIndentedString(locales)).append("\n");
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



