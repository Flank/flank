package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public final class LinuxMachineCatalog extends GenericJson {
  @Key private List<LinuxVersion> versions;

  public List<LinuxVersion> getVersions() {
    return this.versions;
  }

  public LinuxMachineCatalog setVersions(List<LinuxVersion> versions) {
    this.versions = versions;
    return this;
  }

  public LinuxMachineCatalog set(String fieldName, Object value) {
    return (LinuxMachineCatalog) super.set(fieldName, value);
  }

  public LinuxMachineCatalog clone() {
    return (LinuxMachineCatalog) super.clone();
  }
}
