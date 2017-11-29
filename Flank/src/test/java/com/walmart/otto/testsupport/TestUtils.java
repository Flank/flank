package com.walmart.otto.testsupport;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
  public static Path readFileFromResources(String name) throws URISyntaxException {
    URL resource = TestUtils.class.getClassLoader().getResource(name);
    return Paths.get(resource.toURI());
  }
}
