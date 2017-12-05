package com.walmart.otto.aggregator;

class TestCase {

  private final String shardName;
  private final String testName;
  private final String className;
  private final String exceptionMessage;
  private final Result result;
  private final String id;

  private TestCase(
      Result result, String shardName, String testName, String className, String exceptionMessage) {
    this.result = result;
    this.shardName = shardName;
    this.testName = testName;
    this.className = className;
    this.exceptionMessage = exceptionMessage;
    this.id = generateTestId(testName, className);
  }

  static String generateTestId(String testName, String className) {
    final String[] tokens = className.split("\\.");
    return tokens[tokens.length - 1] + "_" + testName;
  }

  static TestCase success(String shardName, String testName, String className) {
    return new TestCase(Result.SUCCESS, shardName, testName, className, null);
  }

  static TestCase failure(
      String shardName, String testName, String className, String exceptionMessage) {
    return new TestCase(Result.FAILURE, shardName, testName, className, exceptionMessage);
  }

  static TestCase error(
      String shardName, String testName, String className, String exceptionMessage) {
    return new TestCase(Result.ERROR, shardName, testName, className, exceptionMessage);
  }

  static TestCase skipped(String shardName, String testName, String className) {
    return new TestCase(Result.SKIPPED, shardName, testName, className, null);
  }

  public boolean isFailure() {
    return result == Result.FAILURE;
  }

  public String getTestName() {
    return testName;
  }

  public String getExceptionMessage() {
    return exceptionMessage;
  }

  public String getClassName() {
    return className;
  }

  public String getShardName() {
    return shardName;
  }

  public Result getResult() {
    return result;
  }

  public String getId() {
    return id;
  }
}
