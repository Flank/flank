package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class EnvironmentMatrix extends GenericJson {
  @Key private AndroidMatrix androidMatrix;

  public AndroidMatrix getAndroidMatrix() {
    return this.androidMatrix;
  }

  public EnvironmentMatrix setAndroidMatrix(AndroidMatrix androidMatrix) {
    this.androidMatrix = androidMatrix;
    return this;
  }

  public EnvironmentMatrix set(String fieldName, Object value) {
    return (EnvironmentMatrix) super.set(fieldName, value);
  }

  public EnvironmentMatrix clone() {
    return (EnvironmentMatrix) super.clone();
  }
}
