/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class GceInstanceDetails
extends GenericJson {
    @Key
    private String name;
    @Key
    private String projectId;
    @Key
    private String zone;

    public String getName() {
        return this.name;
    }

    public GceInstanceDetails setName(String name) {
        this.name = name;
        return this;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public GceInstanceDetails setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getZone() {
        return this.zone;
    }

    public GceInstanceDetails setZone(String zone) {
        this.zone = zone;
        return this;
    }

    public GceInstanceDetails set(String fieldName, Object value) {
        return (GceInstanceDetails)super.set(fieldName, value);
    }

    public GceInstanceDetails clone() {
        return (GceInstanceDetails)super.clone();
    }
}
