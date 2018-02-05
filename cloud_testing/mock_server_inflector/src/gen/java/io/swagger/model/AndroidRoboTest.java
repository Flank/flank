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
 * A test of an android application that explores the application on a virtual or physical Android Device, finding culprits and crashes as it goes.
 **/@ApiModel(description = "A test of an android application that explores the application on a virtual or physical Android Device, finding culprits and crashes as it goes.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidRoboTest   {
  
  @JsonProperty("maxDepth")
  private Integer maxDepth = null;
  
  @JsonProperty("appApk")
  private FileReference appApk = null;
  
  @JsonProperty("appPackageId")
  private String appPackageId = null;
  
  @JsonProperty("startingIntents")
  private List<Object> startingIntents = null;
  
  @JsonProperty("maxSteps")
  private Integer maxSteps = null;
  
  @JsonProperty("appInitialActivity")
  private String appInitialActivity = null;
  
  @JsonProperty("roboDirectives")
  private List<Object> roboDirectives = null;
  
  /**
   * The max depth of the traversal stack Robo can explore. Needs to be at least 2 to make Robo explore the app beyond the first activity. Default is 50. Optional
   **/
  public AndroidRoboTest maxDepth(Integer maxDepth) {
    this.maxDepth = maxDepth;
    return this;
  }

  @ApiModelProperty(value = "The max depth of the traversal stack Robo can explore. Needs to be at least 2 to make Robo explore the app beyond the first activity. Default is 50. Optional")
  @JsonProperty("maxDepth")
  public Integer getMaxDepth() {
    return maxDepth;
  }
  public void setMaxDepth(Integer maxDepth) {
    this.maxDepth = maxDepth;
  }

  /**
   **/
  public AndroidRoboTest appApk(FileReference appApk) {
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
  public AndroidRoboTest appPackageId(String appPackageId) {
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
   * The intents used to launch the app for the crawl. If none are provided, then the main launcher activity is launched. If some are provided, then only those provided are launched (the main launcher activity must be provided explicitly).
   **/
  public AndroidRoboTest startingIntents(List<Object> startingIntents) {
    this.startingIntents = startingIntents;
    return this;
  }

  @ApiModelProperty(value = "The intents used to launch the app for the crawl. If none are provided, then the main launcher activity is launched. If some are provided, then only those provided are launched (the main launcher activity must be provided explicitly).")
  @JsonProperty("startingIntents")
  public List<Object> getStartingIntents() {
    return startingIntents;
  }
  public void setStartingIntents(List<Object> startingIntents) {
    this.startingIntents = startingIntents;
  }

  /**
   * The max number of steps Robo can execute. Default is no limit. Optional
   **/
  public AndroidRoboTest maxSteps(Integer maxSteps) {
    this.maxSteps = maxSteps;
    return this;
  }

  @ApiModelProperty(value = "The max number of steps Robo can execute. Default is no limit. Optional")
  @JsonProperty("maxSteps")
  public Integer getMaxSteps() {
    return maxSteps;
  }
  public void setMaxSteps(Integer maxSteps) {
    this.maxSteps = maxSteps;
  }

  /**
   * The initial activity that should be used to start the app. Optional
   **/
  public AndroidRoboTest appInitialActivity(String appInitialActivity) {
    this.appInitialActivity = appInitialActivity;
    return this;
  }

  @ApiModelProperty(value = "The initial activity that should be used to start the app. Optional")
  @JsonProperty("appInitialActivity")
  public String getAppInitialActivity() {
    return appInitialActivity;
  }
  public void setAppInitialActivity(String appInitialActivity) {
    this.appInitialActivity = appInitialActivity;
  }

  /**
   * A set of directives Robo should apply during the crawl. This allows users to customize the crawl. For example, the username and password for a test account can be provided. Optional
   **/
  public AndroidRoboTest roboDirectives(List<Object> roboDirectives) {
    this.roboDirectives = roboDirectives;
    return this;
  }

  @ApiModelProperty(value = "A set of directives Robo should apply during the crawl. This allows users to customize the crawl. For example, the username and password for a test account can be provided. Optional")
  @JsonProperty("roboDirectives")
  public List<Object> getRoboDirectives() {
    return roboDirectives;
  }
  public void setRoboDirectives(List<Object> roboDirectives) {
    this.roboDirectives = roboDirectives;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidRoboTest androidRoboTest = (AndroidRoboTest) o;
    return Objects.equals(maxDepth, androidRoboTest.maxDepth) &&
        Objects.equals(appApk, androidRoboTest.appApk) &&
        Objects.equals(appPackageId, androidRoboTest.appPackageId) &&
        Objects.equals(startingIntents, androidRoboTest.startingIntents) &&
        Objects.equals(maxSteps, androidRoboTest.maxSteps) &&
        Objects.equals(appInitialActivity, androidRoboTest.appInitialActivity) &&
        Objects.equals(roboDirectives, androidRoboTest.roboDirectives);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxDepth, appApk, appPackageId, startingIntents, maxSteps, appInitialActivity, roboDirectives);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidRoboTest {\n");
    
    sb.append("    maxDepth: ").append(toIndentedString(maxDepth)).append("\n");
    sb.append("    appApk: ").append(toIndentedString(appApk)).append("\n");
    sb.append("    appPackageId: ").append(toIndentedString(appPackageId)).append("\n");
    sb.append("    startingIntents: ").append(toIndentedString(startingIntents)).append("\n");
    sb.append("    maxSteps: ").append(toIndentedString(maxSteps)).append("\n");
    sb.append("    appInitialActivity: ").append(toIndentedString(appInitialActivity)).append("\n");
    sb.append("    roboDirectives: ").append(toIndentedString(roboDirectives)).append("\n");
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



