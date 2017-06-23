package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public final class Locale extends GenericJson {
  @Key private String id;
  @Key private String name;
  @Key private String region;
  @Key private List<String> tags;

  public String getId() {
    return this.id;
  }

  public Locale setId(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public Locale setName(String name) {
    this.name = name;
    return this;
  }

  public String getRegion() {
    return this.region;
  }

  public Locale setRegion(String region) {
    this.region = region;
    return this;
  }

  public List<String> getTags() {
    return this.tags;
  }

  public Locale setTags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public Locale set(String fieldName, Object value) {
    return (Locale) super.set(fieldName, value);
  }

  public Locale clone() {
    return (Locale) super.clone();
  }
}
