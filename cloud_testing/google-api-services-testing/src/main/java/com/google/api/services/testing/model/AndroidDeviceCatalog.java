/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.AndroidModel;
import com.google.api.services.testing.model.AndroidRuntimeConfiguration;
import com.google.api.services.testing.model.AndroidVersion;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AndroidDeviceCatalog
extends GenericJson {
    @Key
    private List<AndroidModel> models;
    @Key
    private AndroidRuntimeConfiguration runtimeConfiguration;
    @Key
    private List<AndroidVersion> versions;

    public List<AndroidModel> getModels() {
        return this.models;
    }

    public AndroidDeviceCatalog setModels(List<AndroidModel> models) {
        this.models = models;
        return this;
    }

    public AndroidRuntimeConfiguration getRuntimeConfiguration() {
        return this.runtimeConfiguration;
    }

    public AndroidDeviceCatalog setRuntimeConfiguration(AndroidRuntimeConfiguration runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
        return this;
    }

    public List<AndroidVersion> getVersions() {
        return this.versions;
    }

    public AndroidDeviceCatalog setVersions(List<AndroidVersion> versions) {
        this.versions = versions;
        return this;
    }

    @Override
    public AndroidDeviceCatalog set(String fieldName, Object value) {
        return (AndroidDeviceCatalog)super.set(fieldName, value);
    }

    @Override
    public AndroidDeviceCatalog clone() {
        return (AndroidDeviceCatalog)super.clone();
    }

    static {
        Data.nullOf(AndroidVersion.class);
    }
}
