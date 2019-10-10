import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestSuiteOverview
import com.google.gson.GsonBuilder
import ftl.args.AndroidArgs
import ftl.gc.GcTestMatrix
import ftl.gc.GcToolResults
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.xmlToString
import ftl.util.firstToolResults
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

@Suppress("UnusedPrivateMember")
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

    fun TestSuiteOverview.time(): String {
        val time = this.elapsedTime ?: return "0"
        val seconds = time.seconds ?: 0
        val nanos = nanosToSeconds(time.nanos)

        return (seconds + nanos).toString()
    }

    // TODO: TestMatrix.ResultStorage.resultsUrl -- link to web console
    private fun ToolResultsStep.webLink(): String {
        return "https://console.firebase.google.com/project/${this.projectId}/" +
                "testlab/histories/${this.historyId}/" +
                "matrices/${this.executionId}/" +
                "executions/${this.stepId}"
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()!!
    private const val androidSuccessStep = "android_matrix-27s4d0h0p53da_success.json"
    private const val iosSuccessStep = "ios_matrix-3vg7fnfansppa_success.json"

    private fun stepFromMatrixId(matrixId: String) {
        val matrix = GcTestMatrix.refresh(matrixId, AndroidArgs.default())
        val toolResult = matrix.firstToolResults()
        println(toolResult?.webLink())

        Files.write(Paths.get("tool_results_step_${matrix.testMatrixId}.json"), gson.toJson(toolResult).toByteArray())
    }

    @JvmStatic
    fun main(args: Array<String>) {
//        stepFromMatrixId("matrix-3vg7fnfansppa")

        resultToXml(iosSuccessStep)
//        resultToXml(androidSuccessStep)
        exitProcess(0)
    }

    fun nanosToSeconds(seconds: Int?): Double {
        // manually divide to keep fractional precision
        if (seconds == null) return 0.0
        return seconds / 1E9
    }

    private fun resultToXml(step: String) {
        val content = String(Files.readAllBytes(Paths.get(step)))
        val toolResult = gson.fromJson(content, ToolResultsStep::class.java)

        val tests = GcToolResults.listTestCases(toolResult)
        val result = GcToolResults.getStepResult(toolResult)

        // todo: handle multiple overviews (iOS only)
        val overview = result.testExecutionStep.testSuiteOverviews.first()
        println("overview name: ${overview.name}") // null on android

        val testCases = mutableListOf<JUnitTestCase>()
        tests.testCases.forEach { testCase ->
            // null on android, EarlGreyExampleSwiftTests on iOS
            println("test case suite name: ${testCase.testCaseReference.testSuiteName}")
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
                null -> {
                } // null status == passed
                "skipped" -> skipped = null
                else -> throw RuntimeException("Unknown TestCase status ${testCase.status}")
            }

            // TODO: On iOS testCase.endTime & testCase.startTime are always null.
            // TODO: On iOS map test cases back to test suites testCase.testCaseReference.testSuiteName
            val timeSeconds = nanosToSeconds(testCase.endTime.nanos - testCase.startTime.nanos)
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
            time = overview.time(),
            timestamp = "",
            hostname = "localhost",
            testLabExecutionId = "",
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
