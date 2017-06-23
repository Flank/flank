package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public final class WindowsMachineCatalog extends GenericJson {
  @Key private List<WindowsVersion> versions;

  public List<WindowsVersion> getVersions() {
    return this.versions;
  }

  public WindowsMachineCatalog setVersions(List<WindowsVersion> versions) {
    this.versions = versions;
    return this;
  }

  public WindowsMachineCatalog set(String fieldName, Object value) {
    return (WindowsMachineCatalog) super.set(fieldName, value);
  }

  public WindowsMachineCatalog clone() {
    return (WindowsMachineCatalog) super.clone();
  }
}
