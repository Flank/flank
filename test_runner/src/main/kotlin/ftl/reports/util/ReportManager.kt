package ftl.reports.util

import com.google.common.annotations.VisibleForTesting
import ftl.args.IArgs
import ftl.args.IgnoredTestCases
import ftl.args.IosArgs
import ftl.args.ShardChunks
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import ftl.json.isAllSuccessful
import ftl.json.validate
import ftl.reports.CostReport
import ftl.reports.FullJUnitReport
import ftl.reports.HtmlErrorReport
import ftl.reports.JUnitReport
import ftl.reports.MatrixResultsReport
import ftl.reports.api.processXmlFromApi
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.getSkippedJUnitTestSuite
import ftl.reports.xml.parseAllSuitesXml
import ftl.reports.xml.parseOneSuiteXml
import ftl.shard.createTestMethodDurationMap
import ftl.util.Artifacts
import ftl.util.resolveLocalRunPath
import java.io.File
import kotlin.math.roundToInt

object ReportManager {

    private fun findXmlFiles(matrices: MatrixMap, args: IArgs): List<File> {
        val xmlFiles = mutableListOf<File>()
        val rootFolder = File(resolveLocalRunPath(matrices, args))

        rootFolder.walk().forEach {
            if (it.name.matches(Artifacts.testResultRgx)) {
                xmlFiles.add(it)
            }
        }

        return xmlFiles
    }

    private fun getWebLink(matrices: MatrixMap, xmlFile: File): String = xmlFile.getMatrixPath(matrices.runPath)
        ?.findMatrixPath(matrices) ?: "".also { println("WARNING: Matrix path not found in JSON.") }

    private val deviceStringRgx = Regex("([^-]+-[^-]+-[^-]+-[^-]+).*")

    // NexusLowRes-28-en-portrait-rerun_1 =>  NexusLowRes-28-en-portrait
    fun getDeviceString(deviceString: String): String {
        val matchResult = deviceStringRgx.find(deviceString)
        return matchResult?.groupValues?.last().orEmpty()
    }

    @VisibleForTesting
    internal fun processXmlFromFile(
        matrices: MatrixMap,
        args: IArgs,
        process: (file: File) -> JUnitTestResult
    ): JUnitTestResult? {
        var mergedXml: JUnitTestResult? = null

        findXmlFiles(matrices, args).forEach { xmlFile ->
            val parsedXml = process(xmlFile)
            val webLink = getWebLink(matrices, xmlFile)
            val deviceName = getDeviceString(xmlFile.parentFile.name)

            parsedXml.testsuites?.forEach { testSuite ->
                testSuite.name = "$deviceName#${testSuite.name}"
                testSuite.testcases?.forEach { testCase ->
                    testCase.webLink = webLink
                }
            }

            mergedXml = parsedXml.merge(mergedXml)
        }

        return mergedXml
    }

    private fun parseTestSuite(matrices: MatrixMap, args: IArgs): JUnitTestResult? = when {
        // ios supports only legacy parsing
        args is IosArgs -> processXmlFromFile(matrices, args, ::parseAllSuitesXml)
        args.useLegacyJUnitResult -> processXmlFromFile(matrices, args, ::parseOneSuiteXml)
        else -> processXmlFromApi(matrices, args)
    }

    /** Returns true if there were no test failures */
    fun generate(
        matrices: MatrixMap,
        args: IArgs,
        testShardChunks: ShardChunks,
        ignoredTestCases: IgnoredTestCases = listOf()
    ) {
        val testSuite: JUnitTestResult? = parseTestSuite(matrices, args)
        if (args.useLegacyJUnitResult) {
            val useFlakyTests = args.flakyTestAttempts > 0
            if (useFlakyTests) JUnitDedupe.modify(testSuite)
        }
        listOf(
            CostReport,
            MatrixResultsReport
        ).map {
            it.run(matrices, testSuite, printToStdout = true, args = args)
        }

        if (!matrices.isAllSuccessful()) {
            listOf(
                HtmlErrorReport
            ).map { it.run(matrices, testSuite, printToStdout = false, args = args) }
        }
        JUnitReport.run(matrices, testSuite?.apply {
            if (ignoredTestCases.isNotEmpty()) {
                testsuites?.add(ignoredTestCases.toJunitTestsResults())
            }
        }, printToStdout = false, args = args)
        when {
            args.fullJUnitResult -> processFullJunitResult(args, matrices, testShardChunks)
            args.useLegacyJUnitResult -> processJunitXml(testSuite, args, testShardChunks)
            else -> processJunitXml(testSuite, args, testShardChunks)
        }
        matrices.validate(args.ignoreFailedTests)
    }

    private fun IgnoredTestCases.toJunitTestsResults() = getSkippedJUnitTestSuite(map {
        JUnitTestCase(
            classname = it.split("#").first().replace("class ", ""),
            name = it.split("#").last(),
            time = "0.0",
            skipped = null
        )
    })

    private fun processFullJunitResult(args: IArgs, matrices: MatrixMap, testShardChunks: ShardChunks) {
        val testSuite = processXmlFromApi(matrices, args, withStackTraces = true)
        FullJUnitReport.run(matrices, testSuite, printToStdout = false, args = args)
        processJunitXml(testSuite, args, testShardChunks)
    }

    data class ShardEfficiency(
        val shard: String,
        val expectedTime: Double,
        val finalTime: Double,
        val timeDiff: Double
    )

    fun createShardEfficiencyList(
        oldResult: JUnitTestResult,
        newResult: JUnitTestResult,
        args: IArgs,
        testShardChunks: ShardChunks
    ): List<ShardEfficiency> {
        val oldDurations = createTestMethodDurationMap(oldResult, args)
        val newDurations = createTestMethodDurationMap(newResult, args)

        return testShardChunks.mapIndexed { index, testSuite ->

            var expectedTime = 0.0
            var finalTime = 0.0
            testSuite.forEach { testCase ->
                expectedTime += oldDurations[testCase] ?: 0.0
                finalTime += newDurations[testCase] ?: 0.0
            }

            val timeDiff = (finalTime - expectedTime)
            ShardEfficiency("Shard $index", expectedTime, finalTime, timeDiff)
        }
    }

    private fun printActual(
        oldResult: JUnitTestResult,
        newResult: JUnitTestResult,
        args: IArgs,
        testShardChunks: ShardChunks
    ) {
        val list = createShardEfficiencyList(oldResult, newResult, args, testShardChunks)

        println("Actual shard times:\n" + list.joinToString("\n") {
            "  ${it.shard}: Expected: ${it.expectedTime.roundToInt()}s, Actual: ${it.finalTime.roundToInt()}s, Diff: ${it.timeDiff.roundToInt()}s"
        } + "\n")
    }

    private fun processJunitXml(
        newTestResult: JUnitTestResult?,
        args: IArgs,
        testShardChunks: ShardChunks
    ) {
        if (newTestResult == null) return

        val oldTestResult = GcStorage.downloadJunitXml(args)

        if (args.useLegacyJUnitResult) {
            newTestResult.mergeTestTimes(oldTestResult)
        }

        if (oldTestResult != null) {
            printActual(oldTestResult, newTestResult, args, testShardChunks)
        }

        GcStorage.uploadJunitXml(newTestResult, args)
    }
}

private fun String.findMatrixPath(matrices: MatrixMap) =
    matrices.map.values.firstOrNull { savedMatrix -> savedMatrix.gcsPath.endsWith(this) }?.webLink
        ?: "".also { println("WARNING: Matrix path not found in JSON. $this") }
