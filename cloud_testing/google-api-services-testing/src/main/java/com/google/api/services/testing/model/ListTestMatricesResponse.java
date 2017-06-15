/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.TestMatrix;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ListTestMatricesResponse
extends GenericJson {
    @Key
    private List<TestMatrix> testMatrices;

    public List<TestMatrix> getTestMatrices() {
        return this.testMatrices;
    }

    public ListTestMatricesResponse setTestMatrices(List<TestMatrix> testMatrices) {
        this.testMatrices = testMatrices;
        return this;
    }

    @Override
    public ListTestMatricesResponse set(String fieldName, Object value) {
        return (ListTestMatricesResponse)super.set(fieldName, value);
    }

    @Override
    public ListTestMatricesResponse clone() {
        return (ListTestMatricesResponse)super.clone();
    }
}
