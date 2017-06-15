/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.Date;
import com.google.api.services.testing.model.Distribution;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AndroidVersion
extends GenericJson {
    @Key
    private Integer apiLevel;
    @Key
    private String codeName;
    @Key
    private Distribution distribution;
    @Key
    private String id;
    @Key
    private Date releaseDate;
    @Key
    private List<String> tags;
    @Key
    private String versionString;

    public Integer getApiLevel() {
        return this.apiLevel;
    }

    public AndroidVersion setApiLevel(Integer apiLevel) {
        this.apiLevel = apiLevel;
        return this;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public AndroidVersion setCodeName(String codeName) {
        this.codeName = codeName;
        return this;
    }

    public Distribution getDistribution() {
        return this.distribution;
    }

    public AndroidVersion setDistribution(Distribution distribution) {
        this.distribution = distribution;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public AndroidVersion setId(String id) {
        this.id = id;
        return this;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public AndroidVersion setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public AndroidVersion setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getVersionString() {
        return this.versionString;
    }

    public AndroidVersion setVersionString(String versionString) {
        this.versionString = versionString;
        return this;
    }

    @Override
    public AndroidVersion set(String fieldName, Object value) {
        return (AndroidVersion)super.set(fieldName, value);
    }

    @Override
    public AndroidVersion clone() {
        return (AndroidVersion)super.clone();
    }
}
