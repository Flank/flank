package com.walmart.otto.aggregator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.walmart.otto.testsupport.TestUtils;
import com.walmart.otto.testsupport.XmlUtils;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JunitReportTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  private JunitReport junitReport;

  @Before
  public void setUp() throws Exception {
    junitReport = new JunitReport();
  }

  @Test
  public void shouldProperlyReadTestSuiteFromFile() throws Exception {
    Path reportPath = TestUtils.readFileFromResources("test_report_with_failures.xml");

    TestSuite testSuite = junitReport.readFromFile(reportPath);
    assertThat(testSuite.getFailuresCount(), equalTo(1));
    assertThat(testSuite.getTestsCount(), equalTo(4));
    assertThat(testSuite.getDurantion(), equalTo(123.0F));

    long failingTestCasesCount =
        testSuite.getTestCaseList().stream().filter(TestCase::isFailure).count();
    assertThat(failingTestCasesCount, equalTo(1L));

    TestCase failure =
        testSuite.getTestCaseList().stream().filter(TestCase::isFailure).findFirst().get();

    assertThat(failure.getExceptionMessage(), equalTo("stacktrace"));
    assertThat(failure.getTestName(), equalTo("test1"));
    assertThat(failure.getClassName(), equalTo("com.foo.Class"));
  }

  @Test
  public void shouldGenerateAggregateReportFromTestSuites() throws Exception {
    TestSuite firstTestSuite = Fixtures.suiteWithoutFailure();
    TestSuite secondTestSuite = Fixtures.suiteWithFailure();

    Path outputFile = folder.newFile().toPath();

    junitReport.generate(outputFile, Arrays.asList(firstTestSuite, secondTestSuite));

    Path expectedFile = TestUtils.readFileFromResources("combined_test_report.xml");

    assertThat(XmlUtils.haveSameContent(outputFile, expectedFile), is(true));
  }
}
