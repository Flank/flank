package com.walmart.otto.aggregator;

import static com.walmart.otto.aggregator.ReportsAggregator.isTestReport;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import com.walmart.otto.testsupport.TestUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReportsAggregatorTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void findReportFiles() throws Exception {
    Path reports = TestUtils.readFileFromResources("reports");

    List<Path> pathList = ReportsAggregator.findReportFiles(reports).collect(Collectors.toList());
    assertThat(pathList.size(), equalTo(2));

    Path first = TestUtils.readFileFromResources("reports/0/test_result_0.xml");
    Path second = TestUtils.readFileFromResources("reports/1/test_result_0.xml");
    Path third = TestUtils.readFileFromResources("reports/2/file.txt");

    assertThat(pathList, hasItems(first, second));
    assertThat(pathList, not(hasItem(third)));
  }

  @Test
  public void shouldMatchXmlReportName() throws Exception {
    Path stubFile = folder.newFile("test_result_0.xml").toPath();
    assertThat(isTestReport(stubFile), is(true));
  }

  @Test
  public void shouldNotMatchOtherFiles() throws Exception {
    Path stubFile = folder.newFile("logcat").toPath();
    assertThat(isTestReport(stubFile), is(false));
  }
}
