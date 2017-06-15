package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class AndroidDevice extends GenericJson {
  @Key private String androidModelId;
  @Key private String androidVersionId;
  @Key private String locale;
  @Key private String orientation;

  public String getAndroidModelId() {
    return this.androidModelId;
  }

  public AndroidDevice setAndroidModelId(String androidModelId) {
    this.androidModelId = androidModelId;
    return this;
  }

  public String getAndroidVersionId() {
    return this.androidVersionId;
  }

  public AndroidDevice setAndroidVersionId(String androidVersionId) {
    this.androidVersionId = androidVersionId;
    return this;
  }

  public String getLocale() {
    return this.locale;
  }

  public AndroidDevice setLocale(String locale) {
    this.locale = locale;
    return this;
  }

  public String getOrientation() {
    return this.orientation;
  }

  public AndroidDevice setOrientation(String orientation) {
    this.orientation = orientation;
    return this;
  }

  public AndroidDevice set(String fieldName, Object value) {
    return (AndroidDevice) super.set(fieldName, value);
  }

  public AndroidDevice clone() {
    return (AndroidDevice) super.clone();
  }
}
