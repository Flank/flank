package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.FileReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A test of an Android Application with a Test Loop. The intent &lt;intent-name&gt; will be implicitly added, since Games is the only user of this api, for the time being.
 **/@ApiModel(description = "A test of an Android Application with a Test Loop. The intent <intent-name> will be implicitly added, since Games is the only user of this api, for the time being.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidTestLoop   {
  
  @JsonProperty("scenarios")
  private List<Integer> scenarios = null;
  
  @JsonProperty("scenarioLabels")
  private List<String> scenarioLabels = null;
  
  @JsonProperty("appApk")
  private FileReference appApk = null;
  
  @JsonProperty("appPackageId")
  private String appPackageId = null;
  
  /**
   * The list of scenarios that should be run during the test. Optional, default is all test loops, derived from the application's manifest.
   **/
  public AndroidTestLoop scenarios(List<Integer> scenarios) {
    this.scenarios = scenarios;
    return this;
  }

  @ApiModelProperty(value = "The list of scenarios that should be run during the test. Optional, default is all test loops, derived from the application's manifest.")
  @JsonProperty("scenarios")
  public List<Integer> getScenarios() {
    return scenarios;
  }
  public void setScenarios(List<Integer> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * The list of scenario labels that should be run during the test. The scenario labels should map to labels defined in the application's manifest. For example, player_experience and com.google.test.loops.player_experience add all of the loops labeled in the manifest with the com.google.test.loops.player_experience name to the execution. Optional. Scenarios can also be specified in the scenarios field.
   **/
  public AndroidTestLoop scenarioLabels(List<String> scenarioLabels) {
    this.scenarioLabels = scenarioLabels;
    return this;
  }

  @ApiModelProperty(value = "The list of scenario labels that should be run during the test. The scenario labels should map to labels defined in the application's manifest. For example, player_experience and com.google.test.loops.player_experience add all of the loops labeled in the manifest with the com.google.test.loops.player_experience name to the execution. Optional. Scenarios can also be specified in the scenarios field.")
  @JsonProperty("scenarioLabels")
  public List<String> getScenarioLabels() {
    return scenarioLabels;
  }
  public void setScenarioLabels(List<String> scenarioLabels) {
    this.scenarioLabels = scenarioLabels;
  }

  /**
   **/
  public AndroidTestLoop appApk(FileReference appApk) {
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
   * The java package for the application under test. Optional, default is determined by examining the application's manifest.
   **/
  public AndroidTestLoop appPackageId(String appPackageId) {
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

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidTestLoop androidTestLoop = (AndroidTestLoop) o;
    return Objects.equals(scenarios, androidTestLoop.scenarios) &&
        Objects.equals(scenarioLabels, androidTestLoop.scenarioLabels) &&
        Objects.equals(appApk, androidTestLoop.appApk) &&
        Objects.equals(appPackageId, androidTestLoop.appPackageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scenarios, scenarioLabels, appApk, appPackageId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidTestLoop {\n");
    
    sb.append("    scenarios: ").append(toIndentedString(scenarios)).append("\n");
    sb.append("    scenarioLabels: ").append(toIndentedString(scenarioLabels)).append("\n");
    sb.append("    appApk: ").append(toIndentedString(appApk)).append("\n");
    sb.append("    appPackageId: ").append(toIndentedString(appPackageId)).append("\n");
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



