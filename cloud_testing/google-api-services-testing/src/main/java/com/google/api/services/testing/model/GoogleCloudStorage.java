/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class GoogleCloudStorage
extends GenericJson {
    @Key
    private String gcsPath;

    public String getGcsPath() {
        return this.gcsPath;
    }

    public GoogleCloudStorage setGcsPath(String gcsPath) {
        this.gcsPath = gcsPath;
        return this;
    }

    public GoogleCloudStorage set(String fieldName, Object value) {
        return (GoogleCloudStorage)super.set(fieldName, value);
    }

    public GoogleCloudStorage clone() {
        return (GoogleCloudStorage)super.clone();
    }
}
