/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.Browser;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class WebDriverCatalog
extends GenericJson {
    @Key
    private List<Browser> browsers;

    public List<Browser> getBrowsers() {
        return this.browsers;
    }

    public WebDriverCatalog setBrowsers(List<Browser> browsers) {
        this.browsers = browsers;
        return this;
    }

    @Override
    public WebDriverCatalog set(String fieldName, Object value) {
        return (WebDriverCatalog)super.set(fieldName, value);
    }

    @Override
    public WebDriverCatalog clone() {
        return (WebDriverCatalog)super.clone();
    }

    static {
        Data.nullOf(Browser.class);
    }
}
