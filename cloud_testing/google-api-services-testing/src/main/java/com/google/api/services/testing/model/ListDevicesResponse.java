/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.Device;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ListDevicesResponse
extends GenericJson {
    @Key
    private List<Device> devices;
    @Key
    private String nextPageToken;

    public List<Device> getDevices() {
        return this.devices;
    }

    public ListDevicesResponse setDevices(List<Device> devices) {
        this.devices = devices;
        return this;
    }

    public String getNextPageToken() {
        return this.nextPageToken;
    }

    public ListDevicesResponse setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }

    @Override
    public ListDevicesResponse set(String fieldName, Object value) {
        return (ListDevicesResponse)super.set(fieldName, value);
    }

    @Override
    public ListDevicesResponse clone() {
        return (ListDevicesResponse)super.clone();
    }

    static {
        Data.nullOf(Device.class);
    }
}
