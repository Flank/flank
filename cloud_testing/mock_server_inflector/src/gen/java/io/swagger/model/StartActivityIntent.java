package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A starting intent specified by an action, uri, and categories.
 **/@ApiModel(description = "A starting intent specified by an action, uri, and categories.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class StartActivityIntent   {
  
  @JsonProperty("action")
  private String action = null;
  
  @JsonProperty("uri")
  private String uri = null;
  
  @JsonProperty("categories")
  private List<String> categories = null;
  
  /**
   * Action name. Required for START_ACTIVITY.
   **/
  public StartActivityIntent action(String action) {
    this.action = action;
    return this;
  }

  @ApiModelProperty(value = "Action name. Required for START_ACTIVITY.")
  @JsonProperty("action")
  public String getAction() {
    return action;
  }
  public void setAction(String action) {
    this.action = action;
  }

  /**
   * URI for the action. Optional.
   **/
  public StartActivityIntent uri(String uri) {
    this.uri = uri;
    return this;
  }

  @ApiModelProperty(value = "URI for the action. Optional.")
  @JsonProperty("uri")
  public String getUri() {
    return uri;
  }
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Intent categories to set on the intent. Optional.
   **/
  public StartActivityIntent categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  @ApiModelProperty(value = "Intent categories to set on the intent. Optional.")
  @JsonProperty("categories")
  public List<String> getCategories() {
    return categories;
  }
  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StartActivityIntent startActivityIntent = (StartActivityIntent) o;
    return Objects.equals(action, startActivityIntent.action) &&
        Objects.equals(uri, startActivityIntent.uri) &&
        Objects.equals(categories, startActivityIntent.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(action, uri, categories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StartActivityIntent {\n");
    
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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



