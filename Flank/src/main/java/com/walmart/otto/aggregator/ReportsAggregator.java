package com.walmart.otto.aggregator;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.walmart.otto.configurator.Configurator;
import com.walmart.otto.tools.GsutilTool;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class ReportsAggregator {

  private final JunitReport junitReport = new JunitReport();
  private final HtmlReport htmlReport;
  private final ArtifactsProcessor artifactsProcessor;
  private final GsutilTool gsutilTool;
  private final Configurator configurator;

  public ReportsAggregator(Configurator configurator, GsutilTool gsutilTool) {
    this.artifactsProcessor = new ArtifactsProcessor(configurator);
    this.gsutilTool = gsutilTool;
    this.configurator = configurator;
    this.htmlReport = new HtmlReport(configurator);
  }

  public void aggregate(Path reportsBaseDir)
      throws XmlReportGenerationException, HtmlReportGenerationException, IOException,
          InterruptedException {
    String baseUrl = generateCloudStorageReportsBaseUrl(reportsBaseDir);

    System.out.println("Generating combined reports starting from: " + reportsBaseDir.toString());

    List<TestSuite> testSuites = readTestSuites(reportsBaseDir);

    Map<String, List<TestSuite>> map =
        testSuites.stream().collect(groupingBy(TestSuite::getMatrixName));

    for (Entry<String, List<TestSuite>> entry : map.entrySet()) {

      String matrixName = entry.getKey();
      List<TestSuite> suites = entry.getValue();

      if (configurator.isGenerateAggregatedXmlReport()) {
        Path xmlOutputFile = reportsBaseDir.resolve(matrixName + "_results.xml");

        junitReport.generate(xmlOutputFile, suites);
        gsutilTool.uploadAggregatedXmlFiles(reportsBaseDir.toFile());

        System.out.println("XML report uploaded to: " + baseUrl + "/" + getFileName(xmlOutputFile));
      }

      if (configurator.isGenerateAggregatedHtmlReport()) {
        Path htmlOutputFile = reportsBaseDir.resolve(matrixName + "_results.html");

        suites
            .stream()
            .flatMap(testSuite -> testSuite.getTestCaseList().stream())
            .filter(TestCase::isFailure)
            .parallel()
            .forEach(testCase -> processArtifacts(reportsBaseDir, matrixName, testCase));

        htmlReport.generate(baseUrl, htmlOutputFile, suites);
        gsutilTool.uploadAggregatedHtmlReports(reportsBaseDir.toFile());

        System.out.println(
            "HTML report uploaded to: " + baseUrl + "/" + getFileName(htmlOutputFile));
      }
    }
  }

  private String generateCloudStorageReportsBaseUrl(Path reportBaseDir) {
    return "https://storage.cloud.google.com/"
        + configurator.getProjectName()
        + "/"
        + getFileName(reportBaseDir);
  }

  //we know we are going to call this for existing files
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private static String getFileName(Path htmlOutputFile) {
    return htmlOutputFile.getFileName().toString();
  }

  private void processArtifacts(Path reportBaseDir, String matrixName, TestCase testCase) {
    try {
      artifactsProcessor.generateArtifactsForTestCase(reportBaseDir, matrixName, testCase);
    } catch (IOException | InterruptedException e) {
      System.out.println("Can't process test artifacts for: " + testCase.getTestName());
      throw new RuntimeException(e);
    }
  }

  private List<TestSuite> readTestSuites(Path reportsBaseDir) throws IOException {
    return findReportFiles(reportsBaseDir).map(junitReport::readTestSuite).collect(toList());
  }

  static Stream<Path> findReportFiles(Path reportsBaseDir) throws IOException {
    return Files.find(reportsBaseDir, 3, (path, basicFileAttributes) -> isTestReport(path));
  }

  static boolean isTestReport(Path path) {
    return getFileName(path).startsWith("test_result_");
  }
}
