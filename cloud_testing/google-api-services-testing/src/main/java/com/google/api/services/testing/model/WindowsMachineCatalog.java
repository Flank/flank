/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.WindowsVersion;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class WindowsMachineCatalog
extends GenericJson {
    @Key
    private List<WindowsVersion> versions;

    public List<WindowsVersion> getVersions() {
        return this.versions;
    }

    public WindowsMachineCatalog setVersions(List<WindowsVersion> versions) {
        this.versions = versions;
        return this;
    }

    @Override
    public WindowsMachineCatalog set(String fieldName, Object value) {
        return (WindowsMachineCatalog)super.set(fieldName, value);
    }

    @Override
    public WindowsMachineCatalog clone() {
        return (WindowsMachineCatalog)super.clone();
    }
}
