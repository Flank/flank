/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TestingScopes {
    public static final String CLOUD_PLATFORM = "https://www.googleapis.com/auth/cloud-platform";

    public static Set<String> all() {
        HashSet<String> set = new HashSet<String>();
        set.add("https://www.googleapis.com/auth/cloud-platform");
        return Collections.unmodifiableSet(set);
    }

    private TestingScopes() {
    }
}
