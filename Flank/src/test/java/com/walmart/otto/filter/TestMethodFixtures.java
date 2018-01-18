package com.walmart.otto.filter;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.linkedin.dex.parser.TestMethod;

public class TestMethodFixtures {

  static final TestMethod FOO_PACKAGE = new TestMethod("foo.ClassName#testName", emptyList());

  static final TestMethod BAR_PACKAGE = new TestMethod("bar.ClassName#testName", emptyList());

  static final TestMethod FOO_CLASSNAME = new TestMethod("whatever.Foo#testName", emptyList());

  static final TestMethod BAR_CLASSNAME = new TestMethod("whatever.Bar#testName", emptyList());

  static final TestMethod WITHOUT_IGNORE_ANNOTATION =
      new TestMethod("whatever.Foo#testName", emptyList());

  static final TestMethod WITH_IGNORE_ANNOTATION =
      new TestMethod("whatever.Foo#testName", singletonList("Ignore"));

  static final TestMethod WITH_FOO_ANNOTATION =
      new TestMethod("whatever.Foo#testName", singletonList("Foo"));

  static final TestMethod WITH_BAR_ANNOTATION =
      new TestMethod("whatever.Foo#testName", singletonList("Bar"));

  static final TestMethod WITHOUT_FOO_ANNOTATION =
      new TestMethod("whatever.Foo#testName", emptyList());

  static final TestMethod WITH_FOO_ANNOTATION_AND_PACKAGE =
      new TestMethod("foo.Bar#testName", singletonList("Foo"));
}
