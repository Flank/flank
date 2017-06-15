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
public final class Orientation
extends GenericJson {
    @Key
    private String id;
    @Key
    private String name;
    @Key
    private List<String> tags;

    public String getId() {
        return this.id;
    }

    public Orientation setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Orientation setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public Orientation setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    @Override
    public Orientation set(String fieldName, Object value) {
        return (Orientation)super.set(fieldName, value);
    }

    @Override
    public Orientation clone() {
        return (Orientation)super.clone();
    }
}
