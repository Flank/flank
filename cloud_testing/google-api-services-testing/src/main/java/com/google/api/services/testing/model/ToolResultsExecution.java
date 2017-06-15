/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class ToolResultsExecution
extends GenericJson {
    @Key
    private String executionId;
    @Key
    private String historyId;
    @Key
    private String projectId;

    public String getExecutionId() {
        return this.executionId;
    }

    public ToolResultsExecution setExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getHistoryId() {
        return this.historyId;
    }

    public ToolResultsExecution setHistoryId(String historyId) {
        this.historyId = historyId;
        return this;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public ToolResultsExecution setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public ToolResultsExecution set(String fieldName, Object value) {
        return (ToolResultsExecution)super.set(fieldName, value);
    }

    public ToolResultsExecution clone() {
        return (ToolResultsExecution)super.clone();
    }
}
