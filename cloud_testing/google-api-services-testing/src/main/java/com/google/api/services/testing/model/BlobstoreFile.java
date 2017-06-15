package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class BlobstoreFile extends GenericJson {
  @Key private String blobId;
  @Key private String md5Hash;

  public String getBlobId() {
    return this.blobId;
  }

  public BlobstoreFile setBlobId(String blobId) {
    this.blobId = blobId;
    return this;
  }

  public String getMd5Hash() {
    return this.md5Hash;
  }

  public BlobstoreFile setMd5Hash(String md5Hash) {
    this.md5Hash = md5Hash;
    return this;
  }

  public BlobstoreFile set(String fieldName, Object value) {
    return (BlobstoreFile) super.set(fieldName, value);
  }

  public BlobstoreFile clone() {
    return (BlobstoreFile) super.clone();
  }
}
