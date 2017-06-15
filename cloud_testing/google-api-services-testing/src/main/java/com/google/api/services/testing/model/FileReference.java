/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.BlobstoreFile;

public final class FileReference
extends GenericJson {
    @Key
    private BlobstoreFile blob;
    @Key
    private String gcsPath;

    public BlobstoreFile getBlob() {
        return this.blob;
    }

    public FileReference setBlob(BlobstoreFile blob) {
        this.blob = blob;
        return this;
    }

    public String getGcsPath() {
        return this.gcsPath;
    }

    public FileReference setGcsPath(String gcsPath) {
        this.gcsPath = gcsPath;
        return this;
    }

    public FileReference set(String fieldName, Object value) {
        return (FileReference)super.set(fieldName, value);
    }

    public FileReference clone() {
        return (FileReference)super.clone();
    }
}
