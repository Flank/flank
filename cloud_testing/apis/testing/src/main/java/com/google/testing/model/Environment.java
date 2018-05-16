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
 * The environment in which the test is run.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Cloud Testing API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Environment extends com.google.api.client.json.GenericJson {

  /**
   * An Android device which must be used with an Android test.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private AndroidDevice androidDevice;

  /**
   * An iOS device which must be used with an iOS test.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private IosDevice iosDevice;

  /**
   * An Android device which must be used with an Android test.
   * @return value or {@code null} for none
   */
  public AndroidDevice getAndroidDevice() {
    return androidDevice;
  }

  /**
   * An Android device which must be used with an Android test.
   * @param androidDevice androidDevice or {@code null} for none
   */
  public Environment setAndroidDevice(AndroidDevice androidDevice) {
    this.androidDevice = androidDevice;
    return this;
  }

  /**
   * An iOS device which must be used with an iOS test.
   * @return value or {@code null} for none
   */
  public IosDevice getIosDevice() {
    return iosDevice;
  }

  /**
   * An iOS device which must be used with an iOS test.
   * @param iosDevice iosDevice or {@code null} for none
   */
  public Environment setIosDevice(IosDevice iosDevice) {
    this.iosDevice = iosDevice;
    return this;
  }

  @Override
  public Environment set(String fieldName, Object value) {
    return (Environment) super.set(fieldName, value);
  }

  @Override
  public Environment clone() {
    return (Environment) super.clone();
  }

}
