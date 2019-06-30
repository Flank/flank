import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestSuiteOverview
import com.google.gson.GsonBuilder
import ftl.gc.GcToolResults
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.xmlToString
import java.nio.file.Files
import java.nio.file.Paths

object Tmp {

    fun Step.testSuiteName(): String {
        val map = mutableMapOf<String, String>()
        this.dimensionValue.map { map[it.key] = it.value }
        return listOf(map["Model"], map["Version"], map["Locale"], map["Orientation"]).joinToString("-")
    }

    fun TestSuiteOverview.tests(): String {
        return (this.totalCount ?: 0).toString()
    }

    fun TestSuiteOverview.failures(): String {
        return (this.failureCount ?: 0).toString()
    }

    fun TestSuiteOverview.errors(): String {
        return (this.errorCount ?: 0).toString()
    }

    fun TestSuiteOverview.skipped(): String {
        return (this.skippedCount ?: 0).toString()
    }

    // TODO: TestMatrix.ResultStorage.resultsUrl -- link to web console
    private fun ToolResultsStep.webLink(): String {
        return "https://console.firebase.google.com/project/${this.projectId}/" +
                "testlab/histories/${this.historyId}/" +
                "matrices/${this.executionId}/" +
                "executions/${this.stepId}"
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val gson = GsonBuilder().setPrettyPrinting().create()!!
//        Files.write(Paths.get("./tool_results_step${it.matrixId}.json"), gson.toJson(it.toolResultsStep).toByteArray())

        val failedStep = "./tool_results_step_matrix-14eytaygn7sis_fail.json"
//        val successStep = "./tool_results_stepmatrix-3l3x3k8qzjmgg_success.json"
        val content = String(Files.readAllBytes(Paths.get(failedStep)))
        val toolResult = gson.fromJson<ToolResultsStep>(content, ToolResultsStep::class.java)
        val tests = GcToolResults.listTestCases(toolResult)
        val result = GcToolResults.getResults(toolResult)

        // todo: handle multiple overviews
        val overview = result.testExecutionStep.testSuiteOverviews.first()

        val testCases = mutableListOf<JUnitTestCase>()
        tests.testCases.forEach { testCase ->
            // TODO: time doesn't match real JUnit XML
            var failures: List<String>? = null
            var errors: List<String>? = null
            // skipped = true is represented by null. skipped = false is "absent"
            var skipped: String? = "absent"

            when (testCase.status) {
                "error" -> errors = testCase.stackTraces?.map { it.exception }
                "failed" -> failures = testCase.stackTraces?.map { it.exception }
                "passed" -> {
                }
                null -> {} // null status == passed
                "skipped" -> skipped = null
                else -> throw RuntimeException("Unknown TestCase status ${testCase.status}")
            }

            // manually divide to keep fractional precision
            val timeSeconds = (testCase.endTime.nanos - testCase.startTime.nanos) / 1E9
            testCases.add(
                JUnitTestCase(
                    name = testCase.testCaseReference.name,
                    classname = testCase.testCaseReference.className,
                    time = timeSeconds.toString(),
                    failures = failures,
                    errors = errors,
                    skipped = skipped
                )
            )
        }

        val testSuite = JUnitTestSuite(
            name = result.testSuiteName(),
            tests = overview.tests(),
            failures = overview.failures(),
            errors = overview.errors(),
            skipped = overview.skipped(),
            time = "",
            timestamp = "",
            hostname = "localhost",
            testcases = testCases
        )

        val xmlTestResult = JUnitTestResult(mutableListOf(testSuite))
        println(xmlTestResult.xmlToString())

        print(toolResult.webLink())

        // result.testExecutionStep.testTiming.testProcessDuration // 5 seconds
        // result.runDuration // 190 seconds

        /*

        Time: 0.839 -- from instrumentation.results

Real JUnit XML:
<?xml version='1.0' encoding='UTF-8' ?>
<testsuite name="" tests="1" failures="0" errors="0" skipped="0" time="0.839" timestamp="2019-05-17T19:02:03" hostname="localhost">
  <properties />
  <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.026" />
</testsuite>

XML from API:
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="NexusLowRes-28-en-portrait" tests="1" failures="0" errors="0" skipped="0" time="" timestamp="" hostname="localhost">
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.018"/>
  </testsuite>
</testsuites>


---- Failed Example


Real JUnit XML:
<testsuite name="" tests="2" failures="1" errors="0" skipped="0" time="3.14" timestamp="2019-05-18T04:11:51" hostname="localhost">
  <properties/>
  <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.655">
    <failure>
    junit.framework.AssertionFailedError: expected:<true> but was:<false>...
    </failure>
  </testcase>
  <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.0"/>
</testsuite>

XML from API:
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="NexusLowRes-28-en-portrait" tests="2" failures="1" errors="0" skipped="0" time="" timestamp="" hostname="localhost">
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.685">
      <failure>junit.framework.AssertionFailedError: expected:&lt;true> but was:&lt;false>...
      </failure>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.027"/>
  </testsuite>
</testsuites>
        */
    }
}
