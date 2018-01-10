package com.walmart.otto.configurator;

import java.io.File;
import org.junit.Test;

public class ConfigReaderTest {

  @Test(expected = IllegalArgumentException.class)
  public void testSkipTests() {
    File properties = new File("src/test/resources/skip-tests.properties");
    Configurator configurator = new Configurator();
    new ConfigReader(properties.getPath(), configurator); //this should throw an exception now
  }
}
