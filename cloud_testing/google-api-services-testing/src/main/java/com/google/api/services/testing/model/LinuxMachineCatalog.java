/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.LinuxVersion;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class LinuxMachineCatalog
extends GenericJson {
    @Key
    private List<LinuxVersion> versions;

    public List<LinuxVersion> getVersions() {
        return this.versions;
    }

    public LinuxMachineCatalog setVersions(List<LinuxVersion> versions) {
        this.versions = versions;
        return this;
    }

    @Override
    public LinuxMachineCatalog set(String fieldName, Object value) {
        return (LinuxMachineCatalog)super.set(fieldName, value);
    }

    @Override
    public LinuxMachineCatalog clone() {
        return (LinuxMachineCatalog)super.clone();
    }
}
