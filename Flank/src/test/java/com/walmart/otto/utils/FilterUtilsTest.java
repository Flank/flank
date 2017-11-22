package com.walmart.otto.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class FilterUtilsTest {

  @Test
  public void testFilterTestsWithSkipTests() {
    List<String> tests =
        Arrays.asList(
            "com.example.app.MyClass#test1",
            "com.example.app.MyClass#test2",
            "com.example.app.screenshot.MyClass#test1",
            "com.example.app.screenshot.MyClass#test2",
            "com.example.app.other.package.MyClass#test1");

    List<String> skipTests =
        Arrays.asList("com.example.app.screenshot", "com.example.app.excluded");
    List<String> filtered = FilterUtils.filterTests(tests, skipTests, "com.example.app");

    List<String> expected =
        Arrays.asList(
            "com.example.app.MyClass#test1",
            "com.example.app.MyClass#test2",
            "com.example.app.other.package.MyClass#test1");

    assertEquals(expected, filtered);
  }

  @Test
  public void testFilterTestsWithEmptySkipTests() {
    List<String> tests =
        Arrays.asList(
            "com.example.app.MyClass#test1",
            "com.example.app.MyClass#test2",
            "com.example.app.screenshot.MyClass#test1",
            "com.example.app.screenshot.MyClass#test2",
            "com.example.app.other.package.MyClass#test1");

    List<String> filtered =
        FilterUtils.filterTests(tests, Collections.emptyList(), "com.example.app");
    assertEquals(tests, filtered);
  }
}
