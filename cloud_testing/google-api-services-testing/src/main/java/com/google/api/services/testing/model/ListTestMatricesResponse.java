package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public final class ListTestMatricesResponse extends GenericJson {
  @Key private List<TestMatrix> testMatrices;

  public List<TestMatrix> getTestMatrices() {
    return this.testMatrices;
  }

  public ListTestMatricesResponse setTestMatrices(List<TestMatrix> testMatrices) {
    this.testMatrices = testMatrices;
    return this;
  }

  public ListTestMatricesResponse set(String fieldName, Object value) {
    return (ListTestMatricesResponse) super.set(fieldName, value);
  }

  public ListTestMatricesResponse clone() {
    return (ListTestMatricesResponse) super.clone();
  }
}
