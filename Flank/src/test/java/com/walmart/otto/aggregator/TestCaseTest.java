package com.walmart.otto.aggregator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestCaseTest {

  @Test
  public void shouldGenerateIdWithClassNameAndTestname() throws Exception {
    String testId = TestCase.generateTestId("testName", "com.foo.Bar");
    assertThat(testId, equalTo("Bar_testName"));
  }
}
