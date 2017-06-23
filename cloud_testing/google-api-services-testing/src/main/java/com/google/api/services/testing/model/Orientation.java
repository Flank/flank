package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public final class Orientation extends GenericJson {
  @Key private String id;
  @Key private String name;
  @Key private List<String> tags;

  public String getId() {
    return this.id;
  }

  public Orientation setId(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public Orientation setName(String name) {
    this.name = name;
    return this;
  }

  public List<String> getTags() {
    return this.tags;
  }

  public Orientation setTags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public Orientation set(String fieldName, Object value) {
    return (Orientation) super.set(fieldName, value);
  }

  public Orientation clone() {
    return (Orientation) super.clone();
  }
}
