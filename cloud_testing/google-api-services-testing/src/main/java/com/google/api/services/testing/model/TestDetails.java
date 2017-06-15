/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class TestDetails
extends GenericJson {
    @Key
    private String errorDetails;
    @Key
    private String errorMessage;
    @Key
    private String progressDetails;
    @Key
    private List<String> progressMessages;

    public String getErrorDetails() {
        return this.errorDetails;
    }

    public TestDetails setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
        return this;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public TestDetails setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getProgressDetails() {
        return this.progressDetails;
    }

    public TestDetails setProgressDetails(String progressDetails) {
        this.progressDetails = progressDetails;
        return this;
    }

    public List<String> getProgressMessages() {
        return this.progressMessages;
    }

    public TestDetails setProgressMessages(List<String> progressMessages) {
        this.progressMessages = progressMessages;
        return this;
    }

    @Override
    public TestDetails set(String fieldName, Object value) {
        return (TestDetails)super.set(fieldName, value);
    }

    @Override
    public TestDetails clone() {
        return (TestDetails)super.clone();
    }
}
