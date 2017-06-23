package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class TestEnvironmentCatalog extends GenericJson {
  @Key private AndroidDeviceCatalog androidDeviceCatalog;
  @Key private WebDriverCatalog webDriverCatalog;

  public AndroidDeviceCatalog getAndroidDeviceCatalog() {
    return this.androidDeviceCatalog;
  }

  public TestEnvironmentCatalog setAndroidDeviceCatalog(AndroidDeviceCatalog androidDeviceCatalog) {
    this.androidDeviceCatalog = androidDeviceCatalog;
    return this;
  }

  public WebDriverCatalog getWebDriverCatalog() {
    return this.webDriverCatalog;
  }

  public TestEnvironmentCatalog setWebDriverCatalog(WebDriverCatalog webDriverCatalog) {
    this.webDriverCatalog = webDriverCatalog;
    return this;
  }

  public TestEnvironmentCatalog set(String fieldName, Object value) {
    return (TestEnvironmentCatalog) super.set(fieldName, value);
  }

  public TestEnvironmentCatalog clone() {
    return (TestEnvironmentCatalog) super.clone();
  }
}
