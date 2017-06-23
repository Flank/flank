package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.util.List;

public final class WebDriverCatalog extends GenericJson {
  @Key private List<Browser> browsers;

  public List<Browser> getBrowsers() {
    return this.browsers;
  }

  public WebDriverCatalog setBrowsers(List<Browser> browsers) {
    this.browsers = browsers;
    return this;
  }

  public WebDriverCatalog set(String fieldName, Object value) {
    return (WebDriverCatalog) super.set(fieldName, value);
  }

  public WebDriverCatalog clone() {
    return (WebDriverCatalog) super.clone();
  }

  static {
    Data.nullOf(Browser.class);
  }
}
