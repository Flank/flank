/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.ClientInfo;
import com.google.api.services.testing.model.EnvironmentMatrix;
import com.google.api.services.testing.model.ResultStorage;
import com.google.api.services.testing.model.TestExecution;
import com.google.api.services.testing.model.TestSpecification;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class TestMatrix
extends GenericJson {
    @Key
    private ClientInfo clientInfo;
    @Key
    private EnvironmentMatrix environmentMatrix;
    @Key
    private String projectId;
    @Key
    private ResultStorage resultStorage;
    @Key
    private String state;
    @Key
    private List<TestExecution> testExecutions;
    @Key
    private String testMatrixId;
    @Key
    private TestSpecification testSpecification;
    @Key
    private String timestamp;

    public ClientInfo getClientInfo() {
        return this.clientInfo;
    }

    public TestMatrix setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
        return this;
    }

    public EnvironmentMatrix getEnvironmentMatrix() {
        return this.environmentMatrix;
    }

    public TestMatrix setEnvironmentMatrix(EnvironmentMatrix environmentMatrix) {
        this.environmentMatrix = environmentMatrix;
        return this;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public TestMatrix setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public ResultStorage getResultStorage() {
        return this.resultStorage;
    }

    public TestMatrix setResultStorage(ResultStorage resultStorage) {
        this.resultStorage = resultStorage;
        return this;
    }

    public String getState() {
        return this.state;
    }

    public TestMatrix setState(String state) {
        this.state = state;
        return this;
    }

    public List<TestExecution> getTestExecutions() {
        return this.testExecutions;
    }

    public TestMatrix setTestExecutions(List<TestExecution> testExecutions) {
        this.testExecutions = testExecutions;
        return this;
    }

    public String getTestMatrixId() {
        return this.testMatrixId;
    }

    public TestMatrix setTestMatrixId(String testMatrixId) {
        this.testMatrixId = testMatrixId;
        return this;
    }

    public TestSpecification getTestSpecification() {
        return this.testSpecification;
    }

    public TestMatrix setTestSpecification(TestSpecification testSpecification) {
        this.testSpecification = testSpecification;
        return this;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public TestMatrix setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public TestMatrix set(String fieldName, Object value) {
        return (TestMatrix)super.set(fieldName, value);
    }

    @Override
    public TestMatrix clone() {
        return (TestMatrix)super.clone();
    }

    static {
        Data.nullOf(TestExecution.class);
    }
}
