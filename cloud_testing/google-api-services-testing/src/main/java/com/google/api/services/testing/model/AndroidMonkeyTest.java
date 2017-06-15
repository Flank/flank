/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.FileReference;

public final class AndroidMonkeyTest
extends GenericJson {
    @Key
    private FileReference appApk;
    @Key
    private String appPackageId;
    @Key
    private Integer eventCount;
    @Key
    private String eventDelay;
    @Key
    private Integer randomSeed;

    public FileReference getAppApk() {
        return this.appApk;
    }

    public AndroidMonkeyTest setAppApk(FileReference appApk) {
        this.appApk = appApk;
        return this;
    }

    public String getAppPackageId() {
        return this.appPackageId;
    }

    public AndroidMonkeyTest setAppPackageId(String appPackageId) {
        this.appPackageId = appPackageId;
        return this;
    }

    public Integer getEventCount() {
        return this.eventCount;
    }

    public AndroidMonkeyTest setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
        return this;
    }

    public String getEventDelay() {
        return this.eventDelay;
    }

    public AndroidMonkeyTest setEventDelay(String eventDelay) {
        this.eventDelay = eventDelay;
        return this;
    }

    public Integer getRandomSeed() {
        return this.randomSeed;
    }

    public AndroidMonkeyTest setRandomSeed(Integer randomSeed) {
        this.randomSeed = randomSeed;
        return this;
    }

    public AndroidMonkeyTest set(String fieldName, Object value) {
        return (AndroidMonkeyTest)super.set(fieldName, value);
    }

    public AndroidMonkeyTest clone() {
        return (AndroidMonkeyTest)super.clone();
    }
}
