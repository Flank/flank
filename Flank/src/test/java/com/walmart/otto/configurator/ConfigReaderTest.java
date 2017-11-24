package com.walmart.otto.configurator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ConfigReaderTest {

  @Test
  public void testSkipTests() {
    File properties = new File("src/test/resources/skip-tests.properties");
    Configurator configurator = new Configurator();
    ConfigReader configReader = new ConfigReader(properties.getPath(), configurator);

    List<String> expected = Arrays.asList("com.example.app.screenshot", "com.example.app.excluded");
    assertEquals(expected, configReader.getConfiguration().getSkipTests());
  }
}
