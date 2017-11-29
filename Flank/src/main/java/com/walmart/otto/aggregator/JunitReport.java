package com.walmart.otto.aggregator;

import com.walmart.otto.utils.XMLUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class JunitReport {

  private static final String TESTCASE_TAG = "testcase";
  private static final String TESTSUITE_TAG = "testsuite";
  private static final String ERROR_TAG = "error";
  private static final String FAILURE_TAG = "failure";
  private static final String SKIPPED_TAG = "skipped";

  private static final String NAME_ATTRIBUTE = "name";
  private static final String CLASSNAME_ATTRIBUTE = "classname";
  private static final String TESTS_ATTRIBUTE = "tests";
  private static final String FAILURES_ATTRIBUTE = "failures";
  private static final String ERRORS_ATTRIBUTE = "errors";
  private static final String SKIPPED_ATTRIBUTE = "skipped";
  private static final String TIME_ATTRIBUTE = "time";
  private static final String HOSTNAME_ATTRIBUTE = "hostname";

  public void generate(Path outputFile, List<TestSuite> testSuites)
      throws XmlReportGenerationException {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setExpandEntityReferences(false);

    DocumentBuilder documentBuilder;
    try {
      documentBuilder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new XmlReportGenerationException(e);
    }

    Document document = documentBuilder.newDocument();
    document.setXmlStandalone(false);

    long tests = testSuites.stream().mapToInt(TestSuite::getTestsCount).sum();
    long failures = testSuites.stream().mapToInt(TestSuite::getFailuresCount).sum();
    long errors = testSuites.stream().mapToInt(TestSuite::getErrorsCount).sum();
    long skipped = testSuites.stream().mapToInt(TestSuite::getSkippedCount).sum();
    double duration = testSuites.stream().mapToDouble(TestSuite::getDurantion).sum();

    Element testSuiteElement = document.createElement(TESTSUITE_TAG);
    testSuiteElement.setAttribute(TESTS_ATTRIBUTE, Long.toString(tests));
    testSuiteElement.setAttribute(FAILURES_ATTRIBUTE, Long.toString(failures));
    testSuiteElement.setAttribute(ERRORS_ATTRIBUTE, Long.toString(errors));
    testSuiteElement.setAttribute(SKIPPED_ATTRIBUTE, Long.toString(skipped));
    testSuiteElement.setAttribute(TIME_ATTRIBUTE, new DecimalFormat("#0.00").format(duration));
    testSuiteElement.setAttribute(HOSTNAME_ATTRIBUTE, "localhost");

    document.appendChild(testSuiteElement);

    for (TestSuite testSuite : testSuites) {
      for (TestCase testCase : testSuite.getTestCaseList()) {
        Element testCaseElement = document.createElement(TESTCASE_TAG);
        testCaseElement.setAttribute(NAME_ATTRIBUTE, testCase.getTestName());
        testCaseElement.setAttribute(CLASSNAME_ATTRIBUTE, testCase.getClassName());

        String exceptionMessage = testCase.getExceptionMessage();
        switch (testCase.getResult()) {
          case SKIPPED:
            Element skippedElement = document.createElement(SKIPPED_TAG);
            testCaseElement.appendChild(skippedElement);
            break;
          case ERROR:
            Element errorElement = document.createElement(ERROR_TAG);
            errorElement.setTextContent(exceptionMessage);
            testCaseElement.appendChild(errorElement);
            break;
          case FAILURE:
            Element failureElement = document.createElement(FAILURE_TAG);
            failureElement.setTextContent(exceptionMessage);
            testCaseElement.appendChild(failureElement);
            break;
          default:
            break;
        }

        testSuiteElement.appendChild(testCaseElement);
      }
    }

    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      BufferedWriter outputStream = Files.newBufferedWriter(outputFile, Charset.forName("UTF-8"));

      StreamResult result = new StreamResult(outputStream);

      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      transformer.transform(new DOMSource(document), result);
    } catch (Exception e) {
      throw new XmlReportGenerationException(e);
    }

    System.out.println("XML report written to: " + outputFile.toString());
  }

  public TestSuite readFromFile(Path filePath) {
    List<TestCase> results = new ArrayList<>();
    String shardName = extractShardName(filePath);
    String matrixName = extractMatrixName(filePath);
    Document document = XMLUtils.getXMLFile(filePath.toFile().getAbsolutePath());

    NodeList nodes = document.getElementsByTagName(TESTCASE_TAG);

    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node instanceof Element) {
        Element testCase = (Element) node;

        String testName = testCase.getAttribute(NAME_ATTRIBUTE);
        String className = testCase.getAttribute(CLASSNAME_ATTRIBUTE);

        boolean hasChildNodes = testCase.hasChildNodes();
        if (hasChildNodes) {

          Node firstElement = null;

          NodeList childNodes = testCase.getChildNodes();
          for (int j = 0; j < childNodes.getLength(); j++) {
            Node item = childNodes.item(j);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
              firstElement = item;
              break;
            }
          }

          if (firstElement == null) {
            throw new IllegalStateException();
          }

          String message = firstElement.getTextContent().trim();
          switch (firstElement.getNodeName()) {
            case FAILURE_TAG:
              results.add(TestCase.failure(shardName, testName, className, message));
              break;
            case ERROR_TAG:
              results.add(TestCase.error(shardName, testName, className, message));
              break;
            case SKIPPED_TAG:
              results.add(TestCase.skipped(shardName, testName, className));
            default:
              throw new IllegalStateException("Unable to process element: " + firstElement);
          }
        } else {
          results.add(TestCase.success(shardName, testName, className));
        }
      }
    }

    NodeList elements = document.getElementsByTagName(TESTSUITE_TAG);
    Element testSuite = (Element) elements.item(0);

    String testCount = testSuite.getAttribute(TESTS_ATTRIBUTE);
    String failuresCount = testSuite.getAttribute(FAILURES_ATTRIBUTE);
    String errorsCount = testSuite.getAttribute(ERRORS_ATTRIBUTE);
    String skippedCount = testSuite.getAttribute(SKIPPED_ATTRIBUTE);
    String duration = testSuite.getAttribute(TIME_ATTRIBUTE);

    return new TestSuite(
        matrixName,
        Integer.parseInt(testCount),
        Integer.parseInt(failuresCount),
        Integer.parseInt(errorsCount),
        Integer.parseInt(skippedCount),
        Float.parseFloat(duration),
        results);
  }

  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private String extractShardName(Path filePath) {
    return filePath.getParent().getParent().getFileName().toString();
  }

  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private String extractMatrixName(Path filePath) {
    return filePath.getParent().getFileName().toString();
  }
}
