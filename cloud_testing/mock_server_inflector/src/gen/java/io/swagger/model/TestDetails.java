package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Additional details about the progress of the running test.
 **/@ApiModel(description = "Additional details about the progress of the running test.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TestDetails   {
  
  @JsonProperty("errorMessage")
  private String errorMessage = null;
  
  @JsonProperty("progressMessages")
  private List<String> progressMessages = null;
  
  /**
   * If the TestState is ERROR, then this string will contain human-readable details about the error. @OutputOnly
   **/
  public TestDetails errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  @ApiModelProperty(value = "If the TestState is ERROR, then this string will contain human-readable details about the error. @OutputOnly")
  @JsonProperty("errorMessage")
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Human-readable, detailed descriptions of the test's progress. For example: \"Provisioning a device\", \"Starting Test\".  During the course of execution new data may be appended to the end of progress_messages. @OutputOnly
   **/
  public TestDetails progressMessages(List<String> progressMessages) {
    this.progressMessages = progressMessages;
    return this;
  }

  @ApiModelProperty(value = "Human-readable, detailed descriptions of the test's progress. For example: \"Provisioning a device\", \"Starting Test\".  During the course of execution new data may be appended to the end of progress_messages. @OutputOnly")
  @JsonProperty("progressMessages")
  public List<String> getProgressMessages() {
    return progressMessages;
  }
  public void setProgressMessages(List<String> progressMessages) {
    this.progressMessages = progressMessages;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestDetails testDetails = (TestDetails) o;
    return Objects.equals(errorMessage, testDetails.errorMessage) &&
        Objects.equals(progressMessages, testDetails.progressMessages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorMessage, progressMessages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestDetails {\n");
    
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    progressMessages: ").append(toIndentedString(progressMessages)).append("\n");
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



