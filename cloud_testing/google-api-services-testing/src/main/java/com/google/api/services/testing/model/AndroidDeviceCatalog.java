package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.util.List;

public final class AndroidDeviceCatalog extends GenericJson {
  @Key private List<AndroidModel> models;
  @Key private AndroidRuntimeConfiguration runtimeConfiguration;
  @Key private List<AndroidVersion> versions;

  public List<AndroidModel> getModels() {
    return this.models;
  }

  public AndroidDeviceCatalog setModels(List<AndroidModel> models) {
    this.models = models;
    return this;
  }

  public AndroidRuntimeConfiguration getRuntimeConfiguration() {
    return this.runtimeConfiguration;
  }

  public AndroidDeviceCatalog setRuntimeConfiguration(
      AndroidRuntimeConfiguration runtimeConfiguration) {
    this.runtimeConfiguration = runtimeConfiguration;
    return this;
  }

  public List<AndroidVersion> getVersions() {
    return this.versions;
  }

  public AndroidDeviceCatalog setVersions(List<AndroidVersion> versions) {
    this.versions = versions;
    return this;
  }

  public AndroidDeviceCatalog set(String fieldName, Object value) {
    return (AndroidDeviceCatalog) super.set(fieldName, value);
  }

  public AndroidDeviceCatalog clone() {
    return (AndroidDeviceCatalog) super.clone();
  }

  static {
    Data.nullOf(AndroidVersion.class);
  }
}
