package com.walmart.otto.models;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class DeviceTest {

  @Test
  public void testDefaultValues() {
    Device device = new Device.Builder().build();
    assertEquals("Nexus6P", device.getId());
    assertEquals("en", device.getLocale());
    assertEquals("portrait", device.getOrientation());
    assertEquals("25", device.getOsVersion());
  }

  @Test
  public void testToStringWillMatchFirebaseExpectedConfig() {
    Device device = new Device.Builder().build();
    assertEquals("model=Nexus6P,locale=en,orientation=portrait,version=25", device.toString());
  }

  @Test
  public void testParsingDevice() {
    String exampleInput =
        "model:Nexus5X;version:23;orientation:landscape;locale:br,model:Nexus6P;version:24,model:pixel;version:26";
    List<Device> devices = Device.parseDevices(exampleInput);
    List<String> expectedResult =
        Arrays.asList(
            "model=Nexus5X,locale=br,orientation=landscape,version=23",
            "model=Nexus6P,locale=en,orientation=portrait,version=24",
            "model=pixel,locale=en,orientation=portrait,version=26");

    for (int i = 0; i < 3; i++) {
      assertEquals(expectedResult.get(i), devices.get(i).toString());
    }
  }
}
