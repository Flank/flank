/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.GoogleCloudStorage;
import com.google.api.services.testing.model.ToolResultsExecution;
import com.google.api.services.testing.model.ToolResultsHistory;

public final class ResultStorage
extends GenericJson {
    @Key
    private GoogleCloudStorage googleCloudStorage;
    @Key
    private ToolResultsExecution toolResultsExecution;
    @Key
    private String toolResultsExecutionId;
    @Key
    private ToolResultsHistory toolResultsHistory;
    @Key
    private String toolResultsHistoryId;
    @Key
    private String toolResultsStepId;

    public GoogleCloudStorage getGoogleCloudStorage() {
        return this.googleCloudStorage;
    }

    public ResultStorage setGoogleCloudStorage(GoogleCloudStorage googleCloudStorage) {
        this.googleCloudStorage = googleCloudStorage;
        return this;
    }

    public ToolResultsExecution getToolResultsExecution() {
        return this.toolResultsExecution;
    }

    public ResultStorage setToolResultsExecution(ToolResultsExecution toolResultsExecution) {
        this.toolResultsExecution = toolResultsExecution;
        return this;
    }

    public String getToolResultsExecutionId() {
        return this.toolResultsExecutionId;
    }

    public ResultStorage setToolResultsExecutionId(String toolResultsExecutionId) {
        this.toolResultsExecutionId = toolResultsExecutionId;
        return this;
    }

    public ToolResultsHistory getToolResultsHistory() {
        return this.toolResultsHistory;
    }

    public ResultStorage setToolResultsHistory(ToolResultsHistory toolResultsHistory) {
        this.toolResultsHistory = toolResultsHistory;
        return this;
    }

    public String getToolResultsHistoryId() {
        return this.toolResultsHistoryId;
    }

    public ResultStorage setToolResultsHistoryId(String toolResultsHistoryId) {
        this.toolResultsHistoryId = toolResultsHistoryId;
        return this;
    }

    public String getToolResultsStepId() {
        return this.toolResultsStepId;
    }

    public ResultStorage setToolResultsStepId(String toolResultsStepId) {
        this.toolResultsStepId = toolResultsStepId;
        return this;
    }

    public ResultStorage set(String fieldName, Object value) {
        return (ResultStorage)super.set(fieldName, value);
    }

    public ResultStorage clone() {
        return (ResultStorage)super.clone();
    }
}
