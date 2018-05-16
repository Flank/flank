/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * Modify at your own risk.
 */

package com.google.testing.model;

/**
 * Represents a tool results step resource.
 *
 * This has the results of a TestExecution.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Cloud Testing API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ToolResultsStep extends com.google.api.client.json.GenericJson {

  /**
   * A tool results execution ID. @OutputOnly
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String executionId;

  /**
   * A tool results history ID. @OutputOnly
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String historyId;

  /**
   * The cloud project that owns the tool results step. @OutputOnly
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String projectId;

  /**
   * A tool results step ID. @OutputOnly
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String stepId;

  /**
   * A tool results execution ID. @OutputOnly
   * @return value or {@code null} for none
   */
  public java.lang.String getExecutionId() {
    return executionId;
  }

  /**
   * A tool results execution ID. @OutputOnly
   * @param executionId executionId or {@code null} for none
   */
  public ToolResultsStep setExecutionId(java.lang.String executionId) {
    this.executionId = executionId;
    return this;
  }

  /**
   * A tool results history ID. @OutputOnly
   * @return value or {@code null} for none
   */
  public java.lang.String getHistoryId() {
    return historyId;
  }

  /**
   * A tool results history ID. @OutputOnly
   * @param historyId historyId or {@code null} for none
   */
  public ToolResultsStep setHistoryId(java.lang.String historyId) {
    this.historyId = historyId;
    return this;
  }

  /**
   * The cloud project that owns the tool results step. @OutputOnly
   * @return value or {@code null} for none
   */
  public java.lang.String getProjectId() {
    return projectId;
  }

  /**
   * The cloud project that owns the tool results step. @OutputOnly
   * @param projectId projectId or {@code null} for none
   */
  public ToolResultsStep setProjectId(java.lang.String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * A tool results step ID. @OutputOnly
   * @return value or {@code null} for none
   */
  public java.lang.String getStepId() {
    return stepId;
  }

  /**
   * A tool results step ID. @OutputOnly
   * @param stepId stepId or {@code null} for none
   */
  public ToolResultsStep setStepId(java.lang.String stepId) {
    this.stepId = stepId;
    return this;
  }

  @Override
  public ToolResultsStep set(String fieldName, Object value) {
    return (ToolResultsStep) super.set(fieldName, value);
  }

  @Override
  public ToolResultsStep clone() {
    return (ToolResultsStep) super.clone();
  }

}
