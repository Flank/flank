/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class LinuxVersion
extends GenericJson {
    @Key
    private String id;
    @Key
    private String versionString;

    public String getId() {
        return this.id;
    }

    public LinuxVersion setId(String id) {
        this.id = id;
        return this;
    }

    public String getVersionString() {
        return this.versionString;
    }

    public LinuxVersion setVersionString(String versionString) {
        this.versionString = versionString;
        return this;
    }

    public LinuxVersion set(String fieldName, Object value) {
        return (LinuxVersion)super.set(fieldName, value);
    }

    public LinuxVersion clone() {
        return (LinuxVersion)super.clone();
    }
}
