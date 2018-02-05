package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.TestState;

/**
 * Response containing the current state of the specified test matrix.
 **/@ApiModel(description = "Response containing the current state of the specified test matrix.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class CancelTestMatrixResponse   {
  
  @JsonProperty("testState")
  private TestState testState = null;
  
  /**
   **/
  public CancelTestMatrixResponse testState(TestState testState) {
    this.testState = testState;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("testState")
  public TestState getTestState() {
    return testState;
  }
  public void setTestState(TestState testState) {
    this.testState = testState;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelTestMatrixResponse cancelTestMatrixResponse = (CancelTestMatrixResponse) o;
    return Objects.equals(testState, cancelTestMatrixResponse.testState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(testState);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelTestMatrixResponse {\n");
    
    sb.append("    testState: ").append(toIndentedString(testState)).append("\n");
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



