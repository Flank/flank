package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.TestSetup;

/**
 * A description of how to run the test.
 **/@ApiModel(description = "A description of how to run the test.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TestSpecification   {
  
  @JsonProperty("testTimeout")
  private String testTimeout = null;
  
  @JsonProperty("autoGoogleLogin")
  private Boolean autoGoogleLogin = null;
  
  @JsonProperty("testSetup")
  private TestSetup testSetup = null;
  
  @JsonProperty("androidRoboTest")
  private Object androidRoboTest = null;
  
  @JsonProperty("androidTestLoop")
  private Object androidTestLoop = null;
  
  @JsonProperty("androidInstrumentationTest")
  private Object androidInstrumentationTest = null;
  
  @JsonProperty("disablePerformanceMetrics")
  private Boolean disablePerformanceMetrics = null;
  
  @JsonProperty("disableVideoRecording")
  private Boolean disableVideoRecording = null;
  
  /**
   * Max time a test execution is allowed to run before it is automatically cancelled. Optional, default is 5 min.
   **/
  public TestSpecification testTimeout(String testTimeout) {
    this.testTimeout = testTimeout;
    return this;
  }

  @ApiModelProperty(value = "Max time a test execution is allowed to run before it is automatically cancelled. Optional, default is 5 min.")
  @JsonProperty("testTimeout")
  public String getTestTimeout() {
    return testTimeout;
  }
  public void setTestTimeout(String testTimeout) {
    this.testTimeout = testTimeout;
  }

  /**
   * Enables automatic Google account login. If set, the service will automatically generate a Google test account and add it to the device, before executing the test. Note that test accounts might be reused. Many applications show their full set of functionalities when an account is present on the device. Logging into the device with these generated accounts allows testing more functionalities. Default is false. Optional
   **/
  public TestSpecification autoGoogleLogin(Boolean autoGoogleLogin) {
    this.autoGoogleLogin = autoGoogleLogin;
    return this;
  }

  @ApiModelProperty(value = "Enables automatic Google account login. If set, the service will automatically generate a Google test account and add it to the device, before executing the test. Note that test accounts might be reused. Many applications show their full set of functionalities when an account is present on the device. Logging into the device with these generated accounts allows testing more functionalities. Default is false. Optional")
  @JsonProperty("autoGoogleLogin")
  public Boolean isAutoGoogleLogin() {
    return autoGoogleLogin;
  }
  public void setAutoGoogleLogin(Boolean autoGoogleLogin) {
    this.autoGoogleLogin = autoGoogleLogin;
  }

  /**
   **/
  public TestSpecification testSetup(TestSetup testSetup) {
    this.testSetup = testSetup;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("testSetup")
  public TestSetup getTestSetup() {
    return testSetup;
  }
  public void setTestSetup(TestSetup testSetup) {
    this.testSetup = testSetup;
  }

  /**
   * An Android robo test.
   **/
  public TestSpecification androidRoboTest(Object androidRoboTest) {
    this.androidRoboTest = androidRoboTest;
    return this;
  }

  @ApiModelProperty(value = "An Android robo test.")
  @JsonProperty("androidRoboTest")
  public Object getAndroidRoboTest() {
    return androidRoboTest;
  }
  public void setAndroidRoboTest(Object androidRoboTest) {
    this.androidRoboTest = androidRoboTest;
  }

  /**
   * An Android Application with a Test Loop
   **/
  public TestSpecification androidTestLoop(Object androidTestLoop) {
    this.androidTestLoop = androidTestLoop;
    return this;
  }

  @ApiModelProperty(value = "An Android Application with a Test Loop")
  @JsonProperty("androidTestLoop")
  public Object getAndroidTestLoop() {
    return androidTestLoop;
  }
  public void setAndroidTestLoop(Object androidTestLoop) {
    this.androidTestLoop = androidTestLoop;
  }

  /**
   * An Android instrumentation test.
   **/
  public TestSpecification androidInstrumentationTest(Object androidInstrumentationTest) {
    this.androidInstrumentationTest = androidInstrumentationTest;
    return this;
  }

  @ApiModelProperty(value = "An Android instrumentation test.")
  @JsonProperty("androidInstrumentationTest")
  public Object getAndroidInstrumentationTest() {
    return androidInstrumentationTest;
  }
  public void setAndroidInstrumentationTest(Object androidInstrumentationTest) {
    this.androidInstrumentationTest = androidInstrumentationTest;
  }

  /**
   * Disables performance metrics recording; may reduce test latency.
   **/
  public TestSpecification disablePerformanceMetrics(Boolean disablePerformanceMetrics) {
    this.disablePerformanceMetrics = disablePerformanceMetrics;
    return this;
  }

  @ApiModelProperty(value = "Disables performance metrics recording; may reduce test latency.")
  @JsonProperty("disablePerformanceMetrics")
  public Boolean isDisablePerformanceMetrics() {
    return disablePerformanceMetrics;
  }
  public void setDisablePerformanceMetrics(Boolean disablePerformanceMetrics) {
    this.disablePerformanceMetrics = disablePerformanceMetrics;
  }

  /**
   * Disables video recording; may reduce test latency.
   **/
  public TestSpecification disableVideoRecording(Boolean disableVideoRecording) {
    this.disableVideoRecording = disableVideoRecording;
    return this;
  }

  @ApiModelProperty(value = "Disables video recording; may reduce test latency.")
  @JsonProperty("disableVideoRecording")
  public Boolean isDisableVideoRecording() {
    return disableVideoRecording;
  }
  public void setDisableVideoRecording(Boolean disableVideoRecording) {
    this.disableVideoRecording = disableVideoRecording;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestSpecification testSpecification = (TestSpecification) o;
    return Objects.equals(testTimeout, testSpecification.testTimeout) &&
        Objects.equals(autoGoogleLogin, testSpecification.autoGoogleLogin) &&
        Objects.equals(testSetup, testSpecification.testSetup) &&
        Objects.equals(androidRoboTest, testSpecification.androidRoboTest) &&
        Objects.equals(androidTestLoop, testSpecification.androidTestLoop) &&
        Objects.equals(androidInstrumentationTest, testSpecification.androidInstrumentationTest) &&
        Objects.equals(disablePerformanceMetrics, testSpecification.disablePerformanceMetrics) &&
        Objects.equals(disableVideoRecording, testSpecification.disableVideoRecording);
  }

  @Override
  public int hashCode() {
    return Objects.hash(testTimeout, autoGoogleLogin, testSetup, androidRoboTest, androidTestLoop, androidInstrumentationTest, disablePerformanceMetrics, disableVideoRecording);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestSpecification {\n");
    
    sb.append("    testTimeout: ").append(toIndentedString(testTimeout)).append("\n");
    sb.append("    autoGoogleLogin: ").append(toIndentedString(autoGoogleLogin)).append("\n");
    sb.append("    testSetup: ").append(toIndentedString(testSetup)).append("\n");
    sb.append("    androidRoboTest: ").append(toIndentedString(androidRoboTest)).append("\n");
    sb.append("    androidTestLoop: ").append(toIndentedString(androidTestLoop)).append("\n");
    sb.append("    androidInstrumentationTest: ").append(toIndentedString(androidInstrumentationTest)).append("\n");
    sb.append("    disablePerformanceMetrics: ").append(toIndentedString(disablePerformanceMetrics)).append("\n");
    sb.append("    disableVideoRecording: ").append(toIndentedString(disableVideoRecording)).append("\n");
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



