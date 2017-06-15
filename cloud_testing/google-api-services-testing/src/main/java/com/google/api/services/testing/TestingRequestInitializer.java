/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import com.google.api.services.testing.TestingRequest;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TestingRequestInitializer
extends CommonGoogleJsonClientRequestInitializer {
    public TestingRequestInitializer() {
    }

    public TestingRequestInitializer(String key) {
        super(key);
    }

    public TestingRequestInitializer(String key, String userIp) {
        super(key, userIp);
    }

    @Override
    public final void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request) throws IOException {
        super.initializeJsonRequest(request);
        this.initializeTestingRequest((TestingRequest)request);
    }

    protected void initializeTestingRequest(TestingRequest<?> request) throws IOException {
    }
}
