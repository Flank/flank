package com.walmart.otto.aggregator;

import java.util.List;

public class TestClass {

  private final String name;
  private final List<TestCase> testCases;

  private final long failingTestsCount;

  public TestClass(List<TestCase> testCases) {
    this.testCases = testCases;
    this.failingTestsCount = testCases.stream().filter(TestCase::isFailure).count();
    this.name = testCases.stream().findFirst().map(TestCase::getClassName).orElse(null);
  }

  public List<TestCase> getTestCases() {
    return testCases;
  }

  public long getFailingTestsCount() {
    return failingTestsCount;
  }

  public boolean hasFailure() {
    return failingTestsCount > 0;
  }

  public String getName() {
    return name;
  }

  public int getCount() {
    return testCases.size();
  }
}
