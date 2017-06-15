package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class TestExecution extends GenericJson {
  @Key private Environment environment;
  @Key private String id;
  @Key private String matrixId;
  @Key private String projectId;
  @Key private String state;
  @Key private TestDetails testDetails;
  @Key private TestSpecification testSpecification;
  @Key private String timestamp;
  @Key private ToolResultsStep toolResultsStep;

  public Environment getEnvironment() {
    return this.environment;
  }

  public TestExecution setEnvironment(Environment environment) {
    this.environment = environment;
    return this;
  }

  public String getId() {
    return this.id;
  }

  public TestExecution setId(String id) {
    this.id = id;
    return this;
  }

  public String getMatrixId() {
    return this.matrixId;
  }

  public TestExecution setMatrixId(String matrixId) {
    this.matrixId = matrixId;
    return this;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public TestExecution setProjectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  public String getState() {
    return this.state;
  }

  public TestExecution setState(String state) {
    this.state = state;
    return this;
  }

  public TestDetails getTestDetails() {
    return this.testDetails;
  }

  public TestExecution setTestDetails(TestDetails testDetails) {
    this.testDetails = testDetails;
    return this;
  }

  public TestSpecification getTestSpecification() {
    return this.testSpecification;
  }

  public TestExecution setTestSpecification(TestSpecification testSpecification) {
    this.testSpecification = testSpecification;
    return this;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public TestExecution setTimestamp(String timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public ToolResultsStep getToolResultsStep() {
    return this.toolResultsStep;
  }

  public TestExecution setToolResultsStep(ToolResultsStep toolResultsStep) {
    this.toolResultsStep = toolResultsStep;
    return this;
  }

  public TestExecution set(String fieldName, Object value) {
    return (TestExecution) super.set(fieldName, value);
  }

  public TestExecution clone() {
    return (TestExecution) super.clone();
  }
}
