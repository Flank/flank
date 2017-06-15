package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class Browser extends GenericJson {
  @Key private AndroidDeviceCatalog androidCatalog;
  @Key private String id;
  @Key private LinuxMachineCatalog linuxCatalog;
  @Key private String name;
  @Key private String release;
  @Key private String versionString;
  @Key private WindowsMachineCatalog windowsCatalog;

  public AndroidDeviceCatalog getAndroidCatalog() {
    return this.androidCatalog;
  }

  public Browser setAndroidCatalog(AndroidDeviceCatalog androidCatalog) {
    this.androidCatalog = androidCatalog;
    return this;
  }

  public String getId() {
    return this.id;
  }

  public Browser setId(String id) {
    this.id = id;
    return this;
  }

  public LinuxMachineCatalog getLinuxCatalog() {
    return this.linuxCatalog;
  }

  public Browser setLinuxCatalog(LinuxMachineCatalog linuxCatalog) {
    this.linuxCatalog = linuxCatalog;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public Browser setName(String name) {
    this.name = name;
    return this;
  }

  public String getRelease() {
    return this.release;
  }

  public Browser setRelease(String release) {
    this.release = release;
    return this;
  }

  public String getVersionString() {
    return this.versionString;
  }

  public Browser setVersionString(String versionString) {
    this.versionString = versionString;
    return this;
  }

  public WindowsMachineCatalog getWindowsCatalog() {
    return this.windowsCatalog;
  }

  public Browser setWindowsCatalog(WindowsMachineCatalog windowsCatalog) {
    this.windowsCatalog = windowsCatalog;
    return this;
  }

  public Browser set(String fieldName, Object value) {
    return (Browser) super.set(fieldName, value);
  }

  public Browser clone() {
    return (Browser) super.clone();
  }
}
