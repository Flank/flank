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
public final class AndroidModel
extends GenericJson {
    @Key
    private String brand;
    @Key
    private String codename;
    @Key
    private String form;
    @Key
    private String id;
    @Key
    private String manufacturer;
    @Key
    private String name;
    @Key
    private Integer screenX;
    @Key
    private Integer screenY;
    @Key
    private List<String> supportedVersionIds;
    @Key
    private List<String> tags;

    public String getBrand() {
        return this.brand;
    }

    public AndroidModel setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public String getCodename() {
        return this.codename;
    }

    public AndroidModel setCodename(String codename) {
        this.codename = codename;
        return this;
    }

    public String getForm() {
        return this.form;
    }

    public AndroidModel setForm(String form) {
        this.form = form;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public AndroidModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public AndroidModel setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public AndroidModel setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getScreenX() {
        return this.screenX;
    }

    public AndroidModel setScreenX(Integer screenX) {
        this.screenX = screenX;
        return this;
    }

    public Integer getScreenY() {
        return this.screenY;
    }

    public AndroidModel setScreenY(Integer screenY) {
        this.screenY = screenY;
        return this;
    }

    public List<String> getSupportedVersionIds() {
        return this.supportedVersionIds;
    }

    public AndroidModel setSupportedVersionIds(List<String> supportedVersionIds) {
        this.supportedVersionIds = supportedVersionIds;
        return this;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public AndroidModel setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    @Override
    public AndroidModel set(String fieldName, Object value) {
        return (AndroidModel)super.set(fieldName, value);
    }

    @Override
    public AndroidModel clone() {
        return (AndroidModel)super.clone();
    }
}
