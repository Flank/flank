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

package com.google.api.services.testing.model;

/**
 * The matrix of environments in which the test is to be executed.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Cloud Testing API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class EnvironmentMatrix extends com.google.api.client.json.GenericJson {

  /**
   * A list of Android devices; the test will be run only on the specified devices.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private AndroidDeviceList androidDeviceList;

  /**
   * A matrix of Android devices.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private AndroidMatrix androidMatrix;

  /**
   * A list of iOS devices.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private IosDeviceList iosDeviceList;

  /**
   * A list of Android devices; the test will be run only on the specified devices.
   * @return value or {@code null} for none
   */
  public AndroidDeviceList getAndroidDeviceList() {
    return androidDeviceList;
  }

  /**
   * A list of Android devices; the test will be run only on the specified devices.
   * @param androidDeviceList androidDeviceList or {@code null} for none
   */
  public EnvironmentMatrix setAndroidDeviceList(AndroidDeviceList androidDeviceList) {
    this.androidDeviceList = androidDeviceList;
    return this;
  }

  /**
   * A matrix of Android devices.
   * @return value or {@code null} for none
   */
  public AndroidMatrix getAndroidMatrix() {
    return androidMatrix;
  }

  /**
   * A matrix of Android devices.
   * @param androidMatrix androidMatrix or {@code null} for none
   */
  public EnvironmentMatrix setAndroidMatrix(AndroidMatrix androidMatrix) {
    this.androidMatrix = androidMatrix;
    return this;
  }

  /**
   * A list of iOS devices.
   * @return value or {@code null} for none
   */
  public IosDeviceList getIosDeviceList() {
    return iosDeviceList;
  }

  /**
   * A list of iOS devices.
   * @param iosDeviceList iosDeviceList or {@code null} for none
   */
  public EnvironmentMatrix setIosDeviceList(IosDeviceList iosDeviceList) {
    this.iosDeviceList = iosDeviceList;
    return this;
  }

  @Override
  public EnvironmentMatrix set(String fieldName, Object value) {
    return (EnvironmentMatrix) super.set(fieldName, value);
  }

  @Override
  public EnvironmentMatrix clone() {
    return (EnvironmentMatrix) super.clone();
  }

}
