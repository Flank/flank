package com.walmart.otto.aggregator;

import java.util.Collections;
import org.jetbrains.annotations.NotNull;

class Fixtures {

  @NotNull
  static TestSuite suiteWithoutFailure() {
    return new TestSuite(
        "matrixName",
        1,
        0,
        0,
        0,
        123,
        Collections.singletonList(TestCase.success("1", "testSuccess", "ClassName")));
  }

  @NotNull
  static TestSuite suiteWithFailure() {
    return new TestSuite(
        "matrixName",
        1,
        1,
        0,
        0,
        123,
        Collections.singletonList(TestCase.failure("1", "testFailure", "ClassName", "stacktrace")));
  }
}
