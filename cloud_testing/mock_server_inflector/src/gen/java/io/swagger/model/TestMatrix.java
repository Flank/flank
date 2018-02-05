package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.InvalidMatrixDetails;
import io.swagger.model.ResultStorage;
import io.swagger.model.State;
import io.swagger.model.TestSpecification;
import java.util.ArrayList;
import java.util.List;

/**
 * A group of one or more TestExecutions, built by taking a product of values over a pre-defined set of axes.
 **/@ApiModel(description = "A group of one or more TestExecutions, built by taking a product of values over a pre-defined set of axes.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TestMatrix   {
  
  @JsonProperty("timestamp")
  private String timestamp = null;
  
  @JsonProperty("environmentMatrix")
  private Object environmentMatrix = null;
  
  @JsonProperty("testMatrixId")
  private String testMatrixId = null;
  
  @JsonProperty("resultStorage")
  private ResultStorage resultStorage = null;
  
  @JsonProperty("invalidMatrixDetails")
  private InvalidMatrixDetails invalidMatrixDetails = null;
  
  @JsonProperty("state")
  private State state = null;
  
  @JsonProperty("projectId")
  private String projectId = null;
  
  @JsonProperty("testSpecification")
  private TestSpecification testSpecification = null;
  
  @JsonProperty("clientInfo")
  private Object clientInfo = null;
  
  @JsonProperty("testExecutions")
  private List<Object> testExecutions = null;
  
  /**
   * The time this test matrix was initially created. @OutputOnly
   **/
  public TestMatrix timestamp(String timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  @ApiModelProperty(value = "The time this test matrix was initially created. @OutputOnly")
  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * How the host machine(s) are configured. Required
   **/
  public TestMatrix environmentMatrix(Object environmentMatrix) {
    this.environmentMatrix = environmentMatrix;
    return this;
  }

  @ApiModelProperty(value = "How the host machine(s) are configured. Required")
  @JsonProperty("environmentMatrix")
  public Object getEnvironmentMatrix() {
    return environmentMatrix;
  }
  public void setEnvironmentMatrix(Object environmentMatrix) {
    this.environmentMatrix = environmentMatrix;
  }

  /**
   * Unique id set by the service. @OutputOnly
   **/
  public TestMatrix testMatrixId(String testMatrixId) {
    this.testMatrixId = testMatrixId;
    return this;
  }

  @ApiModelProperty(value = "Unique id set by the service. @OutputOnly")
  @JsonProperty("testMatrixId")
  public String getTestMatrixId() {
    return testMatrixId;
  }
  public void setTestMatrixId(String testMatrixId) {
    this.testMatrixId = testMatrixId;
  }

  /**
   **/
  public TestMatrix resultStorage(ResultStorage resultStorage) {
    this.resultStorage = resultStorage;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("resultStorage")
  public ResultStorage getResultStorage() {
    return resultStorage;
  }
  public void setResultStorage(ResultStorage resultStorage) {
    this.resultStorage = resultStorage;
  }

  /**
   **/
  public TestMatrix invalidMatrixDetails(InvalidMatrixDetails invalidMatrixDetails) {
    this.invalidMatrixDetails = invalidMatrixDetails;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("invalidMatrixDetails")
  public InvalidMatrixDetails getInvalidMatrixDetails() {
    return invalidMatrixDetails;
  }
  public void setInvalidMatrixDetails(InvalidMatrixDetails invalidMatrixDetails) {
    this.invalidMatrixDetails = invalidMatrixDetails;
  }

  /**
   **/
  public TestMatrix state(State state) {
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
   * The cloud project that owns the test matrix. @OutputOnly
   **/
  public TestMatrix projectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  @ApiModelProperty(value = "The cloud project that owns the test matrix. @OutputOnly")
  @JsonProperty("projectId")
  public String getProjectId() {
    return projectId;
  }
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   **/
  public TestMatrix testSpecification(TestSpecification testSpecification) {
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
   * Information about the client which invoked the test. Optional
   **/
  public TestMatrix clientInfo(Object clientInfo) {
    this.clientInfo = clientInfo;
    return this;
  }

  @ApiModelProperty(value = "Information about the client which invoked the test. Optional")
  @JsonProperty("clientInfo")
  public Object getClientInfo() {
    return clientInfo;
  }
  public void setClientInfo(Object clientInfo) {
    this.clientInfo = clientInfo;
  }

  /**
   * The list of test executions that the service creates for this matrix. @OutputOnly
   **/
  public TestMatrix testExecutions(List<Object> testExecutions) {
    this.testExecutions = testExecutions;
    return this;
  }

  @ApiModelProperty(value = "The list of test executions that the service creates for this matrix. @OutputOnly")
  @JsonProperty("testExecutions")
  public List<Object> getTestExecutions() {
    return testExecutions;
  }
  public void setTestExecutions(List<Object> testExecutions) {
    this.testExecutions = testExecutions;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestMatrix testMatrix = (TestMatrix) o;
    return Objects.equals(timestamp, testMatrix.timestamp) &&
        Objects.equals(environmentMatrix, testMatrix.environmentMatrix) &&
        Objects.equals(testMatrixId, testMatrix.testMatrixId) &&
        Objects.equals(resultStorage, testMatrix.resultStorage) &&
        Objects.equals(invalidMatrixDetails, testMatrix.invalidMatrixDetails) &&
        Objects.equals(state, testMatrix.state) &&
        Objects.equals(projectId, testMatrix.projectId) &&
        Objects.equals(testSpecification, testMatrix.testSpecification) &&
        Objects.equals(clientInfo, testMatrix.clientInfo) &&
        Objects.equals(testExecutions, testMatrix.testExecutions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, environmentMatrix, testMatrixId, resultStorage, invalidMatrixDetails, state, projectId, testSpecification, clientInfo, testExecutions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestMatrix {\n");
    
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    environmentMatrix: ").append(toIndentedString(environmentMatrix)).append("\n");
    sb.append("    testMatrixId: ").append(toIndentedString(testMatrixId)).append("\n");
    sb.append("    resultStorage: ").append(toIndentedString(resultStorage)).append("\n");
    sb.append("    invalidMatrixDetails: ").append(toIndentedString(invalidMatrixDetails)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
    sb.append("    testSpecification: ").append(toIndentedString(testSpecification)).append("\n");
    sb.append("    clientInfo: ").append(toIndentedString(clientInfo)).append("\n");
    sb.append("    testExecutions: ").append(toIndentedString(testExecutions)).append("\n");
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



