package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Locations where the results of running the test are stored.
 **/@ApiModel(description = "Locations where the results of running the test are stored.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class ResultStorage   {
  
  @JsonProperty("googleCloudStorage")
  private Object googleCloudStorage = null;
  
  @JsonProperty("toolResultsExecution")
  private Object toolResultsExecution = null;
  
  @JsonProperty("toolResultsHistory")
  private Object toolResultsHistory = null;
  
  /**
   * Required.
   **/
  public ResultStorage googleCloudStorage(Object googleCloudStorage) {
    this.googleCloudStorage = googleCloudStorage;
    return this;
  }

  @ApiModelProperty(value = "Required.")
  @JsonProperty("googleCloudStorage")
  public Object getGoogleCloudStorage() {
    return googleCloudStorage;
  }
  public void setGoogleCloudStorage(Object googleCloudStorage) {
    this.googleCloudStorage = googleCloudStorage;
  }

  /**
   * The tool results execution that results are written to. @OutputOnly
   **/
  public ResultStorage toolResultsExecution(Object toolResultsExecution) {
    this.toolResultsExecution = toolResultsExecution;
    return this;
  }

  @ApiModelProperty(value = "The tool results execution that results are written to. @OutputOnly")
  @JsonProperty("toolResultsExecution")
  public Object getToolResultsExecution() {
    return toolResultsExecution;
  }
  public void setToolResultsExecution(Object toolResultsExecution) {
    this.toolResultsExecution = toolResultsExecution;
  }

  /**
   * The tool results history that contains the tool results execution that results are written to.  Optional, if not provided the service will choose an appropriate value.
   **/
  public ResultStorage toolResultsHistory(Object toolResultsHistory) {
    this.toolResultsHistory = toolResultsHistory;
    return this;
  }

  @ApiModelProperty(value = "The tool results history that contains the tool results execution that results are written to.  Optional, if not provided the service will choose an appropriate value.")
  @JsonProperty("toolResultsHistory")
  public Object getToolResultsHistory() {
    return toolResultsHistory;
  }
  public void setToolResultsHistory(Object toolResultsHistory) {
    this.toolResultsHistory = toolResultsHistory;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResultStorage resultStorage = (ResultStorage) o;
    return Objects.equals(googleCloudStorage, resultStorage.googleCloudStorage) &&
        Objects.equals(toolResultsExecution, resultStorage.toolResultsExecution) &&
        Objects.equals(toolResultsHistory, resultStorage.toolResultsHistory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(googleCloudStorage, toolResultsExecution, toolResultsHistory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResultStorage {\n");
    
    sb.append("    googleCloudStorage: ").append(toIndentedString(googleCloudStorage)).append("\n");
    sb.append("    toolResultsExecution: ").append(toIndentedString(toolResultsExecution)).append("\n");
    sb.append("    toolResultsHistory: ").append(toIndentedString(toolResultsHistory)).append("\n");
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



