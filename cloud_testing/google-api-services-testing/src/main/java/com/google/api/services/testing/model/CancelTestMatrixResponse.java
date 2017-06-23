package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class CancelTestMatrixResponse extends GenericJson {
  @Key private String testState;

  public String getTestState() {
    return this.testState;
  }

  public CancelTestMatrixResponse setTestState(String testState) {
    this.testState = testState;
    return this;
  }

  public CancelTestMatrixResponse set(String fieldName, Object value) {
    return (CancelTestMatrixResponse) super.set(fieldName, value);
  }

  public CancelTestMatrixResponse clone() {
    return (CancelTestMatrixResponse) super.clone();
  }
}
