package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.FileReference;
import io.swagger.model.OrchestratorOption;
import java.util.ArrayList;
import java.util.List;

/**
 * A test of an Android application that can control an Android component independently of its normal lifecycle. Android instrumentation tests run an application APK and test APK inside the same process on a virtual or physical AndroidDevice.  They also specify a test runner class, such as com.google.GoogleTestRunner, which can vary on the specific instrumentation framework chosen.  See &lt;http://developer.android.com/tools/testing/testing_android.html&gt; for more information on types of Android tests.
 **/@ApiModel(description = "A test of an Android application that can control an Android component independently of its normal lifecycle. Android instrumentation tests run an application APK and test APK inside the same process on a virtual or physical AndroidDevice.  They also specify a test runner class, such as com.google.GoogleTestRunner, which can vary on the specific instrumentation framework chosen.  See <http://developer.android.com/tools/testing/testing_android.html> for more information on types of Android tests.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidInstrumentationTest   {
  
  @JsonProperty("testApk")
  private FileReference testApk = null;
  
  @JsonProperty("testRunnerClass")
  private String testRunnerClass = null;
  
  @JsonProperty("testPackageId")
  private String testPackageId = null;
  
  @JsonProperty("appPackageId")
  private String appPackageId = null;
  
  @JsonProperty("appApk")
  private FileReference appApk = null;
  
  @JsonProperty("orchestratorOption")
  private OrchestratorOption orchestratorOption = null;
  
  @JsonProperty("testTargets")
  private List<String> testTargets = null;
  
  /**
   **/
  public AndroidInstrumentationTest testApk(FileReference testApk) {
    this.testApk = testApk;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("testApk")
  public FileReference getTestApk() {
    return testApk;
  }
  public void setTestApk(FileReference testApk) {
    this.testApk = testApk;
  }

  /**
   * The InstrumentationTestRunner class. Optional, default is determined by examining the application's manifest.
   **/
  public AndroidInstrumentationTest testRunnerClass(String testRunnerClass) {
    this.testRunnerClass = testRunnerClass;
    return this;
  }

  @ApiModelProperty(value = "The InstrumentationTestRunner class. Optional, default is determined by examining the application's manifest.")
  @JsonProperty("testRunnerClass")
  public String getTestRunnerClass() {
    return testRunnerClass;
  }
  public void setTestRunnerClass(String testRunnerClass) {
    this.testRunnerClass = testRunnerClass;
  }

  /**
   * The java package for the test to be executed. Optional, default is determined by examining the application's manifest.
   **/
  public AndroidInstrumentationTest testPackageId(String testPackageId) {
    this.testPackageId = testPackageId;
    return this;
  }

  @ApiModelProperty(value = "The java package for the test to be executed. Optional, default is determined by examining the application's manifest.")
  @JsonProperty("testPackageId")
  public String getTestPackageId() {
    return testPackageId;
  }
  public void setTestPackageId(String testPackageId) {
    this.testPackageId = testPackageId;
  }

  /**
   * The java package for the application under test. Optional, default is determined by examining the application's manifest.
   **/
  public AndroidInstrumentationTest appPackageId(String appPackageId) {
    this.appPackageId = appPackageId;
    return this;
  }

  @ApiModelProperty(value = "The java package for the application under test. Optional, default is determined by examining the application's manifest.")
  @JsonProperty("appPackageId")
  public String getAppPackageId() {
    return appPackageId;
  }
  public void setAppPackageId(String appPackageId) {
    this.appPackageId = appPackageId;
  }

  /**
   **/
  public AndroidInstrumentationTest appApk(FileReference appApk) {
    this.appApk = appApk;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("appApk")
  public FileReference getAppApk() {
    return appApk;
  }
  public void setAppApk(FileReference appApk) {
    this.appApk = appApk;
  }

  /**
   **/
  public AndroidInstrumentationTest orchestratorOption(OrchestratorOption orchestratorOption) {
    this.orchestratorOption = orchestratorOption;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("orchestratorOption")
  public OrchestratorOption getOrchestratorOption() {
    return orchestratorOption;
  }
  public void setOrchestratorOption(OrchestratorOption orchestratorOption) {
    this.orchestratorOption = orchestratorOption;
  }

  /**
   * Each target must be fully qualified with the package name or class name, in one of these formats:  - \"package package_name\"  - \"class package_name.class_name\"  - \"class package_name.class_name#method_name\"  Optional, if empty, all targets in the module will be run.
   **/
  public AndroidInstrumentationTest testTargets(List<String> testTargets) {
    this.testTargets = testTargets;
    return this;
  }

  @ApiModelProperty(value = "Each target must be fully qualified with the package name or class name, in one of these formats:  - \"package package_name\"  - \"class package_name.class_name\"  - \"class package_name.class_name#method_name\"  Optional, if empty, all targets in the module will be run.")
  @JsonProperty("testTargets")
  public List<String> getTestTargets() {
    return testTargets;
  }
  public void setTestTargets(List<String> testTargets) {
    this.testTargets = testTargets;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidInstrumentationTest androidInstrumentationTest = (AndroidInstrumentationTest) o;
    return Objects.equals(testApk, androidInstrumentationTest.testApk) &&
        Objects.equals(testRunnerClass, androidInstrumentationTest.testRunnerClass) &&
        Objects.equals(testPackageId, androidInstrumentationTest.testPackageId) &&
        Objects.equals(appPackageId, androidInstrumentationTest.appPackageId) &&
        Objects.equals(appApk, androidInstrumentationTest.appApk) &&
        Objects.equals(orchestratorOption, androidInstrumentationTest.orchestratorOption) &&
        Objects.equals(testTargets, androidInstrumentationTest.testTargets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(testApk, testRunnerClass, testPackageId, appPackageId, appApk, orchestratorOption, testTargets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidInstrumentationTest {\n");
    
    sb.append("    testApk: ").append(toIndentedString(testApk)).append("\n");
    sb.append("    testRunnerClass: ").append(toIndentedString(testRunnerClass)).append("\n");
    sb.append("    testPackageId: ").append(toIndentedString(testPackageId)).append("\n");
    sb.append("    appPackageId: ").append(toIndentedString(appPackageId)).append("\n");
    sb.append("    appApk: ").append(toIndentedString(appApk)).append("\n");
    sb.append("    orchestratorOption: ").append(toIndentedString(orchestratorOption)).append("\n");
    sb.append("    testTargets: ").append(toIndentedString(testTargets)).append("\n");
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



