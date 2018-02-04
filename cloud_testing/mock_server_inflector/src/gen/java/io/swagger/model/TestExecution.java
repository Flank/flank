package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Environment;
import io.swagger.model.State;
import io.swagger.model.TestDetails;
import io.swagger.model.TestSpecification;
import io.swagger.model.ToolResultsStep;

/**
 * Specifies a single test to be executed in a single environment.
 **/@ApiModel(description = "Specifies a single test to be executed in a single environment.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TestExecution   {
  
  @JsonProperty("matrixId")
  private String matrixId = null;
  
  @JsonProperty("testDetails")
  private TestDetails testDetails = null;
  
  @JsonProperty("environment")
  private Environment environment = null;
  
  @JsonProperty("state")
  private State state = null;
  
  @JsonProperty("toolResultsStep")
  private ToolResultsStep toolResultsStep = null;
  
  @JsonProperty("projectId")
  private String projectId = null;
  
  @JsonProperty("testSpecification")
  private TestSpecification testSpecification = null;
  
  @JsonProperty("id")
  private String id = null;
  
  @JsonProperty("timestamp")
  private String timestamp = null;
  
  /**
   * Id of the containing TestMatrix. @OutputOnly
   **/
  public TestExecution matrixId(String matrixId) {
    this.matrixId = matrixId;
    return this;
  }

  @ApiModelProperty(value = "Id of the containing TestMatrix. @OutputOnly")
  @JsonProperty("matrixId")
  public String getMatrixId() {
    return matrixId;
  }
  public void setMatrixId(String matrixId) {
    this.matrixId = matrixId;
  }

  /**
   **/
  public TestExecution testDetails(TestDetails testDetails) {
    this.testDetails = testDetails;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("testDetails")
  public TestDetails getTestDetails() {
    return testDetails;
  }
  public void setTestDetails(TestDetails testDetails) {
    this.testDetails = testDetails;
  }

  /**
   **/
  public TestExecution environment(Environment environment) {
    this.environment = environment;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("environment")
  public Environment getEnvironment() {
    return environment;
  }
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  /**
   **/
  public TestExecution state(State state) {
    this.state = state;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("state")
  public State getState() {
    return state;
  }
  public void setState(State state) {
    this.state = state;
  }

  /**
   **/
  public TestExecution toolResultsStep(ToolResultsStep toolResultsStep) {
    this.toolResultsStep = toolResultsStep;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("toolResultsStep")
  public ToolResultsStep getToolResultsStep() {
    return toolResultsStep;
  }
  public void setToolResultsStep(ToolResultsStep toolResultsStep) {
    this.toolResultsStep = toolResultsStep;
  }

  /**
   * The cloud project that owns the test execution. @OutputOnly
   **/
  public TestExecution projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  @ApiModelProperty(value = "The cloud project that owns the test execution. @OutputOnly")
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   **/
  public TestExecution testSpecification(TestSpecification testSpecification) {
    this.testSpecification = testSpecification;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("testSpecification")
  public TestSpecification getTestSpecification() {
    return testSpecification;
  }
  public void setTestSpecification(TestSpecification testSpecification) {
    this.testSpecification = testSpecification;
  }

  /**
   * Unique id set by the backend. @OutputOnly
   **/
  public TestExecution id(String id) {
    this.id = id;
    return this;
  }

  @ApiModelProperty(value = "Unique id set by the backend. @OutputOnly")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The time this test execution was initially created. @OutputOnly
   **/
  public TestExecution timestamp(String timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  @ApiModelProperty(value = "The time this test execution was initially created. @OutputOnly")
  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestExecution testExecution = (TestExecution) o;
    return Objects.equals(matrixId, testExecution.matrixId) &&
        Objects.equals(testDetails, testExecution.testDetails) &&
        Objects.equals(environment, testExecution.environment) &&
        Objects.equals(state, testExecution.state) &&
        Objects.equals(toolResultsStep, testExecution.toolResultsStep) &&
        Objects.equals(projectId, testExecution.projectId) &&
        Objects.equals(testSpecification, testExecution.testSpecification) &&
        Objects.equals(id, testExecution.id) &&
        Objects.equals(timestamp, testExecution.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matrixId, testDetails, environment, state, toolResultsStep, projectId, testSpecification, id, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestExecution {\n");
    
    sb.append("    matrixId: ").append(toIndentedString(matrixId)).append("\n");
    sb.append("    testDetails: ").append(toIndentedString(testDetails)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    toolResultsStep: ").append(toIndentedString(toolResultsStep)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    testSpecification: ").append(toIndentedString(testSpecification)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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



