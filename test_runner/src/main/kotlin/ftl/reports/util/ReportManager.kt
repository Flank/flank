package ftl.reports.util

import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import ftl.reports.CostReport
import ftl.reports.HtmlErrorReport
import ftl.reports.JUnitReport
import ftl.reports.MatrixResultsReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseAllSuitesXml
import ftl.reports.xml.parseOneSuiteXml
import ftl.shard.Shard
import ftl.util.Artifacts
import ftl.util.resolveLocalRunPath
import java.io.File
import java.nio.file.Paths
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

    private fun getWebLink(matrices: MatrixMap, xmlFile: File): String {
        val matrixFolder = xmlFile.parentFile.parentFile.name
        val matrixPath = Paths.get(matrices.runPath).fileName.resolve(matrixFolder).toString()
        var webLink = ""
        val savedMatrix = matrices.map.values.firstOrNull { it.gcsPath.endsWith(matrixPath) }
        if (savedMatrix != null) {
            webLink = savedMatrix.webLink
        } else {
            println("WARNING: Matrix path not found in JSON. $matrixPath")
        }
        return webLink
    }

    private val deviceStringRgx = Regex("([^-]+-[^-]+-[^-]+-[^-]+).*")
    // NexusLowRes-28-en-portrait-rerun_1 =>  NexusLowRes-28-en-portrait
    fun getDeviceString(deviceString: String): String {
        val matchResult = deviceStringRgx.find(deviceString)
        return matchResult?.groupValues?.last().orEmpty()
    }

    private fun processXml(matrices: MatrixMap, args: IArgs, process: (file: File) -> JUnitTestResult): JUnitTestResult? {
        var mergedXml: JUnitTestResult? = null

        findXmlFiles(matrices, args).forEach { xmlFile ->
            val parsedXml = process(xmlFile)
            val webLink = getWebLink(matrices, xmlFile)
            val suiteName = getDeviceString(xmlFile.parentFile.name)

            parsedXml.testsuites?.forEach { testSuite ->
                testSuite.name = suiteName
                testSuite.testcases?.forEach { testCase ->
                    testCase.webLink = webLink
                }
            }

            mergedXml = parsedXml.merge(mergedXml)
        }

        return mergedXml
    }

    private fun parseTestSuite(matrices: MatrixMap, args: IArgs): JUnitTestResult? {
        val iosXml = args is IosArgs
        return if (iosXml) {
            processXml(matrices, args, ::parseAllSuitesXml)
        } else {
            processXml(matrices, args, ::parseOneSuiteXml)
        }
    }

    /** Returns true if there were no test failures */
    fun generate(matrices: MatrixMap, args: IArgs): Int {
        val testSuite = parseTestSuite(matrices, args)

        val useFlakyTests = args.flakyTestAttempts > 0
        if (useFlakyTests) JUnitDedupe.modify(testSuite)

        val testSuccessful = if (useFlakyTests) testSuite?.successful() ?: false else matrices.allSuccessful()

        listOf(
            CostReport,
            MatrixResultsReport
        ).map {
            it.run(matrices, testSuite, printToStdout = true, args = args)
        }

        if (!testSuccessful) {
            listOf(
                HtmlErrorReport
            ).map { it.run(matrices, testSuite, printToStdout = false, args = args) }
        }

        JUnitReport.run(matrices, testSuite, printToStdout = false, args = args)
        processJunitXml(testSuite, args)

        // FTL has a bug with matrix roll-up when using flakyTestAttempts
        // as a work around, we calculate the success based on JUnit XML results.
        val exitCode = if (useFlakyTests) {
            if (testSuccessful) 0 else 1
        } else {
            matrices.exitCode()
        }
        return exitCode
    }

    data class ShardEfficiency(
        val shard: String,
        val expectedTime: Double,
        val finalTime: Double,
        val timeDiff: Double
    )

    fun createShardEfficiencyList(oldResult: JUnitTestResult, newResult: JUnitTestResult, args: IArgs):
            List<ShardEfficiency> {
        val oldJunitMap = Shard.createJunitMap(oldResult, args)
        val newJunitMap = Shard.createJunitMap(newResult, args)

        val timeList = mutableListOf<ShardEfficiency>()
        args.testShardChunks.forEachIndexed { index, testSuite ->

            var expectedTime = 0.0
            var finalTime = 0.0
            testSuite.forEach { testCase ->
                expectedTime += oldJunitMap[testCase] ?: 0.0
                finalTime += newJunitMap[testCase] ?: 0.0
            }

            val timeDiff = (finalTime - expectedTime)
            timeList.add(ShardEfficiency("Shard $index", expectedTime, finalTime, timeDiff))
        }

        return timeList
    }

    private fun printActual(oldResult: JUnitTestResult, newResult: JUnitTestResult, args: IArgs) {
        val list = createShardEfficiencyList(oldResult, newResult, args)

        println("Actual shard times:\n" + list.joinToString("\n") {
            "  ${it.shard}: Expected: ${it.expectedTime.roundToInt()}s, Actual: ${it.finalTime.roundToInt()}s, Diff: ${it.timeDiff.roundToInt()}s"
        } + "\n")
    }

    private fun processJunitXml(newTestResult: JUnitTestResult?, args: IArgs) {
        if (newTestResult == null) return

        val oldTestResult = GcStorage.downloadJunitXml(args)

        newTestResult.mergeTestTimes(oldTestResult)

        if (oldTestResult != null) {
            printActual(oldTestResult, newTestResult, args)
        }

        GcStorage.uploadJunitXml(newTestResult, args)
    }
}
