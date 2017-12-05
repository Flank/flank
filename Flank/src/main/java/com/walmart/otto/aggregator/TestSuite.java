package com.walmart.otto.aggregator;

import java.util.List;

public class TestSuite {

  private final String matrixName;
  private final int testsCount;
  private final int failuresCount;
  private final int errorsCount;
  private final int skippedCount;
  private final float durantion;
  private final List<TestCase> testCaseList;

  public TestSuite(
      String matrixName,
      int testsCount,
      int failuresCount,
      int errorsCount,
      int skippedCount,
      float durantion,
      List<TestCase> testCaseList) {
    this.matrixName = matrixName;
    this.testsCount = testsCount;
    this.failuresCount = failuresCount;
    this.errorsCount = errorsCount;
    this.skippedCount = skippedCount;
    this.durantion = durantion;
    this.testCaseList = testCaseList;
  }

  public int getTestsCount() {
    return testsCount;
  }

  public int getFailuresCount() {
    return failuresCount;
  }

  public int getSkippedCount() {
    return skippedCount;
  }

  public float getDurantion() {
    return durantion;
  }

  public List<TestCase> getTestCaseList() {
    return testCaseList;
  }

  public int getErrorsCount() {
    return errorsCount;
  }

  public String getMatrixName() {
    return matrixName;
  }
}
