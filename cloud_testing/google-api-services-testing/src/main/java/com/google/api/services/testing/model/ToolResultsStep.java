/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class ToolResultsStep
extends GenericJson {
    @Key
    private String executionId;
    @Key
    private String historyId;
    @Key
    private String projectId;
    @Key
    private String stepId;

    public String getExecutionId() {
        return this.executionId;
    }

    public ToolResultsStep setExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getHistoryId() {
        return this.historyId;
    }

    public ToolResultsStep setHistoryId(String historyId) {
        this.historyId = historyId;
        return this;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public ToolResultsStep setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getStepId() {
        return this.stepId;
    }

    public ToolResultsStep setStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }

    public ToolResultsStep set(String fieldName, Object value) {
        return (ToolResultsStep)super.set(fieldName, value);
    }

    public ToolResultsStep clone() {
        return (ToolResultsStep)super.clone();
    }
}
