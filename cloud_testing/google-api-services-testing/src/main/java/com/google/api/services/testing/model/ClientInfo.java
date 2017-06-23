package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class ClientInfo extends GenericJson {
  @Key private String name;

  public String getName() {
    return this.name;
  }

  public ClientInfo setName(String name) {
    this.name = name;
    return this;
  }

  public ClientInfo set(String fieldName, Object value) {
    return (ClientInfo) super.set(fieldName, value);
  }

  public ClientInfo clone() {
    return (ClientInfo) super.clone();
  }
}
