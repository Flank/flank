package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class Environment extends GenericJson {
  @Key private AndroidDevice androidDevice;

  public AndroidDevice getAndroidDevice() {
    return this.androidDevice;
  }

  public Environment setAndroidDevice(AndroidDevice androidDevice) {
    this.androidDevice = androidDevice;
    return this;
  }

  public Environment set(String fieldName, Object value) {
    return (Environment) super.set(fieldName, value);
  }

  public Environment clone() {
    return (Environment) super.clone();
  }
}
