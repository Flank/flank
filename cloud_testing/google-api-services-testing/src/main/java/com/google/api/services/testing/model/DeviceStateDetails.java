/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class DeviceStateDetails
extends GenericJson {
    @Key
    private String errorDetails;
    @Key
    private String progressDetails;

    public String getErrorDetails() {
        return this.errorDetails;
    }

    public DeviceStateDetails setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
        return this;
    }

    public String getProgressDetails() {
        return this.progressDetails;
    }

    public DeviceStateDetails setProgressDetails(String progressDetails) {
        this.progressDetails = progressDetails;
        return this;
    }

    public DeviceStateDetails set(String fieldName, Object value) {
        return (DeviceStateDetails)super.set(fieldName, value);
    }

    public DeviceStateDetails clone() {
        return (DeviceStateDetails)super.clone();
    }
}
