/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;

public final class Distribution
extends GenericJson {
    @Key
    private Double marketShare;
    @Key
    private String measurementTime;

    public Double getMarketShare() {
        return this.marketShare;
    }

    public Distribution setMarketShare(Double marketShare) {
        this.marketShare = marketShare;
        return this;
    }

    public String getMeasurementTime() {
        return this.measurementTime;
    }

    public Distribution setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
        return this;
    }

    public Distribution set(String fieldName, Object value) {
        return (Distribution)super.set(fieldName, value);
    }

    public Distribution clone() {
        return (Distribution)super.clone();
    }
}
