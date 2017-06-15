/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.Testing;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TestingRequest<T>
extends AbstractGoogleJsonClientRequest<T> {
    @Key(value="$.xgafv")
    private String $Xgafv;
    @Key(value="access_token")
    private String accessToken;
    @Key
    private String alt;
    @Key(value="bearer_token")
    private String bearerToken;
    @Key
    private String callback;
    @Key
    private String fields;
    @Key
    private String key;
    @Key(value="oauth_token")
    private String oauthToken;
    @Key
    private Boolean pp;
    @Key
    private Boolean prettyPrint;
    @Key
    private String quotaUser;
    @Key
    private String uploadType;
    @Key(value="upload_protocol")
    private String uploadProtocol;

    public TestingRequest(Testing client, String method, String uriTemplate, Object content, Class<T> responseClass) {
        super(client, method, uriTemplate, content, responseClass);
    }

    public String get$Xgafv() {
        return this.$Xgafv;
    }

    public TestingRequest<T> set$Xgafv(String $Xgafv) {
        this.$Xgafv = $Xgafv;
        return this;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public TestingRequest<T> setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getAlt() {
        return this.alt;
    }

    public TestingRequest<T> setAlt(String alt) {
        this.alt = alt;
        return this;
    }

    public String getBearerToken() {
        return this.bearerToken;
    }

    public TestingRequest<T> setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
        return this;
    }

    public String getCallback() {
        return this.callback;
    }

    public TestingRequest<T> setCallback(String callback) {
        this.callback = callback;
        return this;
    }

    public String getFields() {
        return this.fields;
    }

    public TestingRequest<T> setFields(String fields) {
        this.fields = fields;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public TestingRequest<T> setKey(String key) {
        this.key = key;
        return this;
    }

    public String getOauthToken() {
        return this.oauthToken;
    }

    public TestingRequest<T> setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }

    public Boolean getPp() {
        return this.pp;
    }

    public TestingRequest<T> setPp(Boolean pp) {
        this.pp = pp;
        return this;
    }

    public Boolean getPrettyPrint() {
        return this.prettyPrint;
    }

    public TestingRequest<T> setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }

    public String getQuotaUser() {
        return this.quotaUser;
    }

    public TestingRequest<T> setQuotaUser(String quotaUser) {
        this.quotaUser = quotaUser;
        return this;
    }

    public String getUploadType() {
        return this.uploadType;
    }

    public TestingRequest<T> setUploadType(String uploadType) {
        this.uploadType = uploadType;
        return this;
    }

    public String getUploadProtocol() {
        return this.uploadProtocol;
    }

    public TestingRequest<T> setUploadProtocol(String uploadProtocol) {
        this.uploadProtocol = uploadProtocol;
        return this;
    }

    @Override
    public final Testing getAbstractGoogleClient() {
        return (Testing)super.getAbstractGoogleClient();
    }

    @Override
    public TestingRequest<T> setDisableGZipContent(boolean disableGZipContent) {
        return (TestingRequest)super.setDisableGZipContent(disableGZipContent);
    }

    @Override
    public TestingRequest<T> setRequestHeaders(HttpHeaders headers) {
        return (TestingRequest)super.setRequestHeaders(headers);
    }

    @Override
    public TestingRequest<T> set(String parameterName, Object value) {
        return (TestingRequest)super.set(parameterName, value);
    }
}
