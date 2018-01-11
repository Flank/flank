package com.walmart.otto.filter;

import static com.walmart.otto.filter.TestFilters.notIgnored;
import static com.walmart.otto.filter.TestMethodFixtures.BAR_CLASSNAME;
import static com.walmart.otto.filter.TestMethodFixtures.BAR_PACKAGE;
import static com.walmart.otto.filter.TestMethodFixtures.FOO_CLASSNAME;
import static com.walmart.otto.filter.TestMethodFixtures.FOO_PACKAGE;
import static com.walmart.otto.filter.TestMethodFixtures.WITHOUT_FOO_ANNOTATION;
import static com.walmart.otto.filter.TestMethodFixtures.WITHOUT_IGNORE_ANNOTATION;
import static com.walmart.otto.filter.TestMethodFixtures.WITH_FOO_ANNOTATION;
import static com.walmart.otto.filter.TestMethodFixtures.WITH_FOO_ANNOTATION_AND_PACKAGE;
import static com.walmart.otto.filter.TestMethodFixtures.WITH_IGNORE_ANNOTATION;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.walmart.otto.testsupport.TestUtils;
import java.nio.file.Path;
import org.junit.Test;

public class TestFiltersTest {

  @Test
  public void testFilteringByPackage() {
    TestFilter filter = TestFilters.fromCommandLineArguments("package foo");

    assertTrue(filter.shouldRun(FOO_PACKAGE));
    assertFalse(filter.shouldRun(BAR_PACKAGE));
  }

  @Test
  public void testFilteringByPackageNegative() {
    TestFilter filter = TestFilters.fromCommandLineArguments("notPackage foo");

    assertFalse(filter.shouldRun(FOO_PACKAGE));
    assertTrue(filter.shouldRun(BAR_PACKAGE));
  }

  @Test
  public void testFilteringByClassName() {
    TestFilter filter = TestFilters.fromCommandLineArguments("class whatever.Foo");

    assertTrue(filter.shouldRun(FOO_CLASSNAME));
    assertFalse(filter.shouldRun(BAR_CLASSNAME));
  }

  @Test
  public void testFilteringByClassNameNegative() {
    TestFilter filter = TestFilters.fromCommandLineArguments("notClass whatever.Foo");

    assertFalse(filter.shouldRun(FOO_CLASSNAME));
    assertTrue(filter.shouldRun(BAR_CLASSNAME));
  }

  @Test
  public void notIgnoredShouldFilterTestsWithTheIgnoreAnnotation() {
    TestFilter filter = notIgnored();

    assertFalse(filter.shouldRun(WITH_IGNORE_ANNOTATION));
    assertTrue(filter.shouldRun(WITHOUT_IGNORE_ANNOTATION));
  }

  @Test
  public void testFilteringByAnnotation() {
    TestFilter filter = TestFilters.fromCommandLineArguments("annotation Foo");

    assertTrue(filter.shouldRun(WITH_FOO_ANNOTATION));
    assertFalse(filter.shouldRun(WITHOUT_FOO_ANNOTATION));
  }

  @Test
  public void testFilteringByAnnotationNegative() {
    TestFilter filter = TestFilters.fromCommandLineArguments("notAnnotation Foo");

    assertFalse(filter.shouldRun(WITH_FOO_ANNOTATION));
    assertTrue(filter.shouldRun(WITHOUT_FOO_ANNOTATION));
  }

  @Test
  public void allOfProperlyChecksAllFilters() {
    TestFilter filter = TestFilters.fromCommandLineArguments("package foo,bar; annotation Foo");

    assertFalse(filter.shouldRun(FOO_PACKAGE));
    assertFalse(filter.shouldRun(BAR_PACKAGE));
    assertFalse(filter.shouldRun(WITH_FOO_ANNOTATION));
    assertTrue(filter.shouldRun(WITH_FOO_ANNOTATION_AND_PACKAGE));
  }

  @Test
  public void testFilteringFromFileNegative() throws Exception {
    Path file = TestUtils.readFileFromResources("dummy-tests-file.txt");
    String filePath = file.toString();

    TestFilter filter = TestFilters.fromCommandLineArguments("testFile " + filePath);

    assertTrue(filter.shouldRun(FOO_PACKAGE));
    assertTrue(filter.shouldRun(BAR_PACKAGE));
  }

  @Test
  public void testFilteringFromFile() throws Exception {
    Path file = TestUtils.readFileFromResources("dummy-tests-file.txt");
    String filePath = file.toString();

    TestFilter filter = TestFilters.fromCommandLineArguments("notTestFile " + filePath);

    assertFalse(filter.shouldRun(FOO_PACKAGE));
    assertFalse(filter.shouldRun(BAR_PACKAGE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void passingMalformedCommandWillThrowException() {
    TestFilters.fromCommandLineArguments("class=com.my.package");
  }

  @Test(expected = IllegalArgumentException.class)
  public void passingInvalidCommandWillThrowException() {
    TestFilters.fromCommandLineArguments("invalidCommand com.my.package");
  }
}
