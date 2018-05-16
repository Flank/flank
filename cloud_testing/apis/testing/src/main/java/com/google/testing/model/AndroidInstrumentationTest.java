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
 * A test of an Android application that can control an Android component independently of its
 * normal lifecycle. Android instrumentation tests run an application APK and test APK inside the
 * same process on a virtual or physical AndroidDevice.  They also specify a test runner class, such
 * as com.google.GoogleTestRunner, which can vary on the specific instrumentation framework chosen.
 *
 * See  for more information on types of Android tests.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Cloud Testing API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class AndroidInstrumentationTest extends com.google.api.client.json.GenericJson {

  /**
   * The APK for the application under test. Required
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private FileReference appApk;

  /**
   * The java package for the application under test. Optional, default is determined by examining
   * the application's manifest.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String appPackageId;

  /**
   * The option of whether running each test within its own invocation of instrumentation with
   * Android Test Orchestrator or not. ** Orchestrator is only compatible with AndroidJUnitRunner
   * version 1.0 or higher! ** Orchestrator offers the following benefits:  - No shared state  -
   * Crashes are isolated  - Logs are scoped per test
   *
   * See
   *
   * for more information about Android Test Orchestrator.
   *
   * Optional, if empty, test will be run without orchestrator.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String orchestratorOption;

  /**
   * The APK containing the test code to be executed. Required
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private FileReference testApk;

  /**
   * The java package for the test to be executed. Optional, default is determined by examining the
   * application's manifest.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String testPackageId;

  /**
   * The InstrumentationTestRunner class. Optional, default is determined by examining the
   * application's manifest.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String testRunnerClass;

  /**
   * Each target must be fully qualified with the package name or class name, in one of these
   * formats:  - "package package_name"  - "class package_name.class_name"  - "class
   * package_name.class_name#method_name"
   *
   * Optional, if empty, all targets in the module will be run.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> testTargets;

  /**
   * The APK for the application under test. Required
   * @return value or {@code null} for none
   */
  public FileReference getAppApk() {
    return appApk;
  }

  /**
   * The APK for the application under test. Required
   * @param appApk appApk or {@code null} for none
   */
  public AndroidInstrumentationTest setAppApk(FileReference appApk) {
    this.appApk = appApk;
    return this;
  }

  /**
   * The java package for the application under test. Optional, default is determined by examining
   * the application's manifest.
   * @return value or {@code null} for none
   */
  public java.lang.String getAppPackageId() {
    return appPackageId;
  }

  /**
   * The java package for the application under test. Optional, default is determined by examining
   * the application's manifest.
   * @param appPackageId appPackageId or {@code null} for none
   */
  public AndroidInstrumentationTest setAppPackageId(java.lang.String appPackageId) {
    this.appPackageId = appPackageId;
    return this;
  }

  /**
   * The option of whether running each test within its own invocation of instrumentation with
   * Android Test Orchestrator or not. ** Orchestrator is only compatible with AndroidJUnitRunner
   * version 1.0 or higher! ** Orchestrator offers the following benefits:  - No shared state  -
   * Crashes are isolated  - Logs are scoped per test
   *
   * See
   *
   * for more information about Android Test Orchestrator.
   *
   * Optional, if empty, test will be run without orchestrator.
   * @return value or {@code null} for none
   */
  public java.lang.String getOrchestratorOption() {
    return orchestratorOption;
  }

  /**
   * The option of whether running each test within its own invocation of instrumentation with
   * Android Test Orchestrator or not. ** Orchestrator is only compatible with AndroidJUnitRunner
   * version 1.0 or higher! ** Orchestrator offers the following benefits:  - No shared state  -
   * Crashes are isolated  - Logs are scoped per test
   *
   * See
   *
   * for more information about Android Test Orchestrator.
   *
   * Optional, if empty, test will be run without orchestrator.
   * @param orchestratorOption orchestratorOption or {@code null} for none
   */
  public AndroidInstrumentationTest setOrchestratorOption(java.lang.String orchestratorOption) {
    this.orchestratorOption = orchestratorOption;
    return this;
  }

  /**
   * The APK containing the test code to be executed. Required
   * @return value or {@code null} for none
   */
  public FileReference getTestApk() {
    return testApk;
  }

  /**
   * The APK containing the test code to be executed. Required
   * @param testApk testApk or {@code null} for none
   */
  public AndroidInstrumentationTest setTestApk(FileReference testApk) {
    this.testApk = testApk;
    return this;
  }

  /**
   * The java package for the test to be executed. Optional, default is determined by examining the
   * application's manifest.
   * @return value or {@code null} for none
   */
  public java.lang.String getTestPackageId() {
    return testPackageId;
  }

  /**
   * The java package for the test to be executed. Optional, default is determined by examining the
   * application's manifest.
   * @param testPackageId testPackageId or {@code null} for none
   */
  public AndroidInstrumentationTest setTestPackageId(java.lang.String testPackageId) {
    this.testPackageId = testPackageId;
    return this;
  }

  /**
   * The InstrumentationTestRunner class. Optional, default is determined by examining the
   * application's manifest.
   * @return value or {@code null} for none
   */
  public java.lang.String getTestRunnerClass() {
    return testRunnerClass;
  }

  /**
   * The InstrumentationTestRunner class. Optional, default is determined by examining the
   * application's manifest.
   * @param testRunnerClass testRunnerClass or {@code null} for none
   */
  public AndroidInstrumentationTest setTestRunnerClass(java.lang.String testRunnerClass) {
    this.testRunnerClass = testRunnerClass;
    return this;
  }

  /**
   * Each target must be fully qualified with the package name or class name, in one of these
   * formats:  - "package package_name"  - "class package_name.class_name"  - "class
   * package_name.class_name#method_name"
   *
   * Optional, if empty, all targets in the module will be run.
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getTestTargets() {
    return testTargets;
  }

  /**
   * Each target must be fully qualified with the package name or class name, in one of these
   * formats:  - "package package_name"  - "class package_name.class_name"  - "class
   * package_name.class_name#method_name"
   *
   * Optional, if empty, all targets in the module will be run.
   * @param testTargets testTargets or {@code null} for none
   */
  public AndroidInstrumentationTest setTestTargets(java.util.List<java.lang.String> testTargets) {
    this.testTargets = testTargets;
    return this;
  }

  @Override
  public AndroidInstrumentationTest set(String fieldName, Object value) {
    return (AndroidInstrumentationTest) super.set(fieldName, value);
  }

  @Override
  public AndroidInstrumentationTest clone() {
    return (AndroidInstrumentationTest) super.clone();
  }

}
