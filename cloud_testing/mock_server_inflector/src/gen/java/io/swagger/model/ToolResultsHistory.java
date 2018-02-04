package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a tool results history resource.
 **/@ApiModel(description = "Represents a tool results history resource.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class ToolResultsHistory   {
  
  @JsonProperty("projectId")
  private String projectId = null;
  
  @JsonProperty("historyId")
  private String historyId = null;
  
  /**
   * The cloud project that owns the tool results history. Required
   **/
  public ToolResultsHistory projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  @ApiModelProperty(value = "The cloud project that owns the tool results history. Required")
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * A tool results history ID. Required
   **/
  public ToolResultsHistory historyId(String historyId) {
    this.historyId = historyId;
    return this;
  }

  @ApiModelProperty(value = "A tool results history ID. Required")
  @JsonProperty("historyId")
  public String getHistoryId() {
    return historyId;
  }
  public void setHistoryId(String historyId) {
    this.historyId = historyId;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ToolResultsHistory toolResultsHistory = (ToolResultsHistory) o;
    return Objects.equals(projectId, toolResultsHistory.projectId) &&
        Objects.equals(historyId, toolResultsHistory.historyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, historyId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ToolResultsHistory {\n");
    
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    historyId: ").append(toIndentedString(historyId)).append("\n");
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



