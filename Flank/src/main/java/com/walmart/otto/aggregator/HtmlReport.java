package com.walmart.otto.aggregator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.walmart.otto.configurator.Configurator;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

class HtmlReport {

  private final Configurator configurator;

  HtmlReport(Configurator configurator) {
    this.configurator = configurator;
  }

  public void generate(String baseUrl, Path outputFile, List<TestSuite> testSuites)
      throws HtmlReportGenerationException {

    long testsCount = testSuites.stream().mapToInt(TestSuite::getTestsCount).sum();
    long failuresCount = testSuites.stream().mapToInt(TestSuite::getFailuresCount).sum();

    List<TestClass> testClasses =
        testSuites
            .stream()
            .flatMap(testSuite -> testSuite.getTestCaseList().stream())
            .collect(groupingBy(TestCase::getClassName))
            .values()
            .stream()
            .map(this::createTestClass)
            .sorted(comparingLong(TestClass::getFailingTestsCount).reversed())
            .collect(toList());

    int durationSeconds = testSuites.stream().mapToInt(s -> (int) s.getDurantion()).sum();
    Duration duration = Duration.ofSeconds(durationSeconds);

    long shardsCount =
        testSuites
            .stream()
            .flatMap(testSuite -> testSuite.getTestCaseList().stream())
            .map(TestCase::getShardName)
            .distinct()
            .count();

    HashMap<Object, Object> parameters = new HashMap<>();
    parameters.put("testsCount", testsCount);
    parameters.put("failuresCount", failuresCount);
    parameters.put("passedCounts", testsCount - failuresCount);
    parameters.put("failed", failuresCount > 0);
    parameters.put("testClasses", testClasses);
    parameters.put("duration", TimeUtils.formatDuration(duration));
    parameters.put("shardsCount", shardsCount);
    parameters.put("baseUrl", baseUrl);
    parameters.put("splitVideo", configurator.isGenerateSplitVideo());

    InputStreamReader inputStreamReader;
    try {
      InputStream templateStream =
          ReportsAggregator.class.getResourceAsStream("/tests_results.mustache");
      inputStreamReader = new InputStreamReader(templateStream, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new HtmlReportGenerationException(e);
    }

    Template template = Mustache.compiler().compile(inputStreamReader);

    try (BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.forName("UTF-8"))) {
      template.execute(parameters, writer);
      writer.flush();
      System.out.println("HTML report written to: " + outputFile.toString());
    } catch (IOException ex) {
      System.out.println("Failed to generate HTML report");
      throw new HtmlReportGenerationException(ex);
    }
  }

  private TestClass createTestClass(List<TestCase> testCases) {
    // we want the failing test cases to be the firsts in the list
    List<TestCase> sorted =
        testCases
            .stream()
            .sorted(comparing(TestCase::isFailure).reversed())
            .collect(Collectors.toList());
    return new TestClass(sorted);
  }
}
