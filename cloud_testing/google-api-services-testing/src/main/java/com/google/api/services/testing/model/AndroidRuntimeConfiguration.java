package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import java.util.List;

public final class AndroidRuntimeConfiguration extends GenericJson {
  @Key private List<Locale> locales;
  @Key private List<Orientation> orientations;

  public List<Locale> getLocales() {
    return this.locales;
  }

  public AndroidRuntimeConfiguration setLocales(List<Locale> locales) {
    this.locales = locales;
    return this;
  }

  public List<Orientation> getOrientations() {
    return this.orientations;
  }

  public AndroidRuntimeConfiguration setOrientations(List<Orientation> orientations) {
    this.orientations = orientations;
    return this;
  }

  public AndroidRuntimeConfiguration set(String fieldName, Object value) {
    return (AndroidRuntimeConfiguration) super.set(fieldName, value);
  }

  public AndroidRuntimeConfiguration clone() {
    return (AndroidRuntimeConfiguration) super.clone();
  }

  static {
    Data.nullOf(Locale.class);
    Data.nullOf(Orientation.class);
  }
}
