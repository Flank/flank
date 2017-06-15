package com.google.api.services.testing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestingScopes {
  public static final String CLOUD_PLATFORM = "https://www.googleapis.com/auth/cloud-platform";

  public static Set<String> all() {
    Set<String> set = new HashSet();
    set.add("https://www.googleapis.com/auth/cloud-platform");
    return Collections.unmodifiableSet(set);
  }
}
