package com.walmart.otto;

import static org.junit.Assert.assertEquals;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

public class ParsingTest {

  @Test(expected = OptionException.class)
  public void parsingShouldFailIfTestApkIsMissing() {
    OptionParser parser = Flank.getOptionParser();
    parser.parse("-a", "app.apk");
  }

  @Test(expected = OptionException.class)
  public void parsingShouldFailIfApkIsMissing() {
    OptionParser parser = Flank.getOptionParser();
    parser.parse("-t", "test.apk");
  }

  @Test
  public void configPropertiesShouldBeTheDefault() {
    OptionParser parser = Flank.getOptionParser();
    OptionSet options = parser.parse("-a", "app.apk", "-t", "test.apk");
    assertEquals(options.valueOf("c"), "config.properties");
  }

  @Test(expected = OptionException.class)
  public void havingAdditionalParametersShouldFail() {
    OptionParser parser = Flank.getOptionParser();
    parser.parse("-a", "app.apk", "-t", "test.apk", "-z", "hello");
  }

  @Test
  public void havingAllParametersShouldWork() {
    OptionParser parser = Flank.getOptionParser();
    parser.parse(
        "-a", "app.apk", "-t", "test.apk", "-c", "my.config", "-f", "package com.my.package");
  }
}
