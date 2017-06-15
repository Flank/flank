package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public final class AndroidMatrix extends GenericJson {
  @Key private List<String> androidModelIds;
  @Key private List<String> androidVersionIds;
  @Key private List<String> locales;
  @Key private List<String> orientations;

  public List<String> getAndroidModelIds() {
    return this.androidModelIds;
  }

  public AndroidMatrix setAndroidModelIds(List<String> androidModelIds) {
    this.androidModelIds = androidModelIds;
    return this;
  }

  public List<String> getAndroidVersionIds() {
    return this.androidVersionIds;
  }

  public AndroidMatrix setAndroidVersionIds(List<String> androidVersionIds) {
    this.androidVersionIds = androidVersionIds;
    return this;
  }

  public List<String> getLocales() {
    return this.locales;
  }

  public AndroidMatrix setLocales(List<String> locales) {
    this.locales = locales;
    return this;
  }

  public List<String> getOrientations() {
    return this.orientations;
  }

  public AndroidMatrix setOrientations(List<String> orientations) {
    this.orientations = orientations;
    return this;
  }

  public AndroidMatrix set(String fieldName, Object value) {
    return (AndroidMatrix) super.set(fieldName, value);
  }

  public AndroidMatrix clone() {
    return (AndroidMatrix) super.clone();
  }
}
