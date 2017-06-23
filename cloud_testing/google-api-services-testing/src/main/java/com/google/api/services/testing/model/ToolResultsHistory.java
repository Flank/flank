package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class ToolResultsHistory extends GenericJson {
  @Key private String historyId;
  @Key private String projectId;

  public String getHistoryId() {
    return this.historyId;
  }

  public ToolResultsHistory setHistoryId(String historyId) {
    this.historyId = historyId;
    return this;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public ToolResultsHistory setProjectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  public ToolResultsHistory set(String fieldName, Object value) {
    return (ToolResultsHistory) super.set(fieldName, value);
  }

  public ToolResultsHistory clone() {
    return (ToolResultsHistory) super.clone();
  }
}
