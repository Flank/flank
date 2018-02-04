package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a tool results step resource.  This has the results of a TestExecution.
 **/@ApiModel(description = "Represents a tool results step resource.  This has the results of a TestExecution.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class ToolResultsStep   {
  
  @JsonProperty("executionId")
  private String executionId = null;
  
  @JsonProperty("stepId")
  private String stepId = null;
  
  @JsonProperty("projectId")
  private String projectId = null;
  
  @JsonProperty("historyId")
  private String historyId = null;
  
  /**
   * A tool results execution ID. @OutputOnly
   **/
  public ToolResultsStep executionId(String executionId) {
    this.executionId = executionId;
    return this;
  }

  @ApiModelProperty(value = "A tool results execution ID. @OutputOnly")
  @JsonProperty("executionId")
  public String getExecutionId() {
    return executionId;
  }
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  /**
   * A tool results step ID. @OutputOnly
   **/
  public ToolResultsStep stepId(String stepId) {
    this.stepId = stepId;
    return this;
  }

  @ApiModelProperty(value = "A tool results step ID. @OutputOnly")
  @JsonProperty("stepId")
  public String getStepId() {
    return stepId;
  }
  public void setStepId(String stepId) {
    this.stepId = stepId;
  }

  /**
   * The cloud project that owns the tool results step. @OutputOnly
   **/
  public ToolResultsStep projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  @ApiModelProperty(value = "The cloud project that owns the tool results step. @OutputOnly")
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * A tool results history ID. @OutputOnly
   **/
  public ToolResultsStep historyId(String historyId) {
    this.historyId = historyId;
    return this;
  }

  @ApiModelProperty(value = "A tool results history ID. @OutputOnly")
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
    ToolResultsStep toolResultsStep = (ToolResultsStep) o;
    return Objects.equals(executionId, toolResultsStep.executionId) &&
        Objects.equals(stepId, toolResultsStep.stepId) &&
        Objects.equals(projectId, toolResultsStep.projectId) &&
        Objects.equals(historyId, toolResultsStep.historyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionId, stepId, projectId, historyId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ToolResultsStep {\n");
    
    sb.append("    executionId: ").append(toIndentedString(executionId)).append("\n");
    sb.append("    stepId: ").append(toIndentedString(stepId)).append("\n");
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



