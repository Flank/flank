package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class AndroidRoboTest extends GenericJson {
  @Key private FileReference appApk;
  @Key private String appInitialActivity;
  @Key private String appPackageId;
  @Key private FileReference bootstrapApk;
  @Key private String bootstrapPackageId;
  @Key private String bootstrapRunnerClass;
  @Key private Integer maxDepth;
  @Key private Integer maxSteps;
  @Key private Boolean randomizeSteps;

  public FileReference getAppApk() {
    return this.appApk;
  }

  public AndroidRoboTest setAppApk(FileReference appApk) {
    this.appApk = appApk;
    return this;
  }

  public String getAppInitialActivity() {
    return this.appInitialActivity;
  }

  public AndroidRoboTest setAppInitialActivity(String appInitialActivity) {
    this.appInitialActivity = appInitialActivity;
    return this;
  }

  public String getAppPackageId() {
    return this.appPackageId;
  }

  public AndroidRoboTest setAppPackageId(String appPackageId) {
    this.appPackageId = appPackageId;
    return this;
  }

  public FileReference getBootstrapApk() {
    return this.bootstrapApk;
  }

  public AndroidRoboTest setBootstrapApk(FileReference bootstrapApk) {
    this.bootstrapApk = bootstrapApk;
    return this;
  }

  public String getBootstrapPackageId() {
    return this.bootstrapPackageId;
  }

  public AndroidRoboTest setBootstrapPackageId(String bootstrapPackageId) {
    this.bootstrapPackageId = bootstrapPackageId;
    return this;
  }

  public String getBootstrapRunnerClass() {
    return this.bootstrapRunnerClass;
  }

  public AndroidRoboTest setBootstrapRunnerClass(String bootstrapRunnerClass) {
    this.bootstrapRunnerClass = bootstrapRunnerClass;
    return this;
  }

  public Integer getMaxDepth() {
    return this.maxDepth;
  }

  public AndroidRoboTest setMaxDepth(Integer maxDepth) {
    this.maxDepth = maxDepth;
    return this;
  }

  public Integer getMaxSteps() {
    return this.maxSteps;
  }

  public AndroidRoboTest setMaxSteps(Integer maxSteps) {
    this.maxSteps = maxSteps;
    return this;
  }

  public Boolean getRandomizeSteps() {
    return this.randomizeSteps;
  }

  public AndroidRoboTest setRandomizeSteps(Boolean randomizeSteps) {
    this.randomizeSteps = randomizeSteps;
    return this;
  }

  public AndroidRoboTest set(String fieldName, Object value) {
    return (AndroidRoboTest) super.set(fieldName, value);
  }

  public AndroidRoboTest clone() {
    return (AndroidRoboTest) super.clone();
  }
}
