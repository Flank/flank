package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class TestSpecification extends GenericJson {
  @Key private AndroidInstrumentationTest androidInstrumentationTest;
  @Key private AndroidMonkeyTest androidMonkeyTest;
  @Key private AndroidRoboTest androidRoboTest;
  @Key private String testTimeout;

  public AndroidInstrumentationTest getAndroidInstrumentationTest() {
    return this.androidInstrumentationTest;
  }

  public TestSpecification setAndroidInstrumentationTest(
      AndroidInstrumentationTest androidInstrumentationTest) {
    this.androidInstrumentationTest = androidInstrumentationTest;
    return this;
  }

  public AndroidMonkeyTest getAndroidMonkeyTest() {
    return this.androidMonkeyTest;
  }

  public TestSpecification setAndroidMonkeyTest(AndroidMonkeyTest androidMonkeyTest) {
    this.androidMonkeyTest = androidMonkeyTest;
    return this;
  }

  public AndroidRoboTest getAndroidRoboTest() {
    return this.androidRoboTest;
  }

  public TestSpecification setAndroidRoboTest(AndroidRoboTest androidRoboTest) {
    this.androidRoboTest = androidRoboTest;
    return this;
  }

  public String getTestTimeout() {
    return this.testTimeout;
  }

  public TestSpecification setTestTimeout(String testTimeout) {
    this.testTimeout = testTimeout;
    return this;
  }

  public TestSpecification set(String fieldName, Object value) {
    return (TestSpecification) super.set(fieldName, value);
  }

  public TestSpecification clone() {
    return (TestSpecification) super.clone();
  }
}
