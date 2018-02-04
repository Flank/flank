package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.ActionType;

/**
 * Directs Robo to interact with a specific UI element if it is encountered during the crawl. Currently, Robo can perform text entry or element click.
 **/@ApiModel(description = "Directs Robo to interact with a specific UI element if it is encountered during the crawl. Currently, Robo can perform text entry or element click.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class RoboDirective   {
  
  @JsonProperty("resourceName")
  private String resourceName = null;
  
  @JsonProperty("inputText")
  private String inputText = null;
  
  @JsonProperty("actionType")
  private ActionType actionType = null;
  
  /**
   * The android resource name of the target UI element For example,    in Java: R.string.foo    in xml: @string/foo Only the “foo” part is needed. Reference doc: https://developer.android.com/guide/topics/resources/accessing-resources.html Required
   **/
  public RoboDirective resourceName(String resourceName) {
    this.resourceName = resourceName;
    return this;
  }

  @ApiModelProperty(value = "The android resource name of the target UI element For example,    in Java: R.string.foo    in xml: @string/foo Only the “foo” part is needed. Reference doc: https://developer.android.com/guide/topics/resources/accessing-resources.html Required")
  @JsonProperty("resourceName")
  public String getResourceName() {
    return resourceName;
  }
  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  /**
   * The text that Robo is directed to set. If left empty, the directive will be treated as a CLICK on the element matching the resource_name. Optional
   **/
  public RoboDirective inputText(String inputText) {
    this.inputText = inputText;
    return this;
  }

  @ApiModelProperty(value = "The text that Robo is directed to set. If left empty, the directive will be treated as a CLICK on the element matching the resource_name. Optional")
  @JsonProperty("inputText")
  public String getInputText() {
    return inputText;
  }
  public void setInputText(String inputText) {
    this.inputText = inputText;
  }

  /**
   **/
  public RoboDirective actionType(ActionType actionType) {
    this.actionType = actionType;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("actionType")
  public ActionType getActionType() {
    return actionType;
  }
  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoboDirective roboDirective = (RoboDirective) o;
    return Objects.equals(resourceName, roboDirective.resourceName) &&
        Objects.equals(inputText, roboDirective.inputText) &&
        Objects.equals(actionType, roboDirective.actionType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceName, inputText, actionType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoboDirective {\n");
    
    sb.append("    resourceName: ").append(toIndentedString(resourceName)).append("\n");
    sb.append("    inputText: ").append(toIndentedString(inputText)).append("\n");
    sb.append("    actionType: ").append(toIndentedString(actionType)).append("\n");
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



