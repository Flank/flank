package ftl.reports.util

import com.google.common.annotations.VisibleForTesting
import com.google.testing.model.TestExecution
import flank.common.logLn
import ftl.api.JUnitTest
import ftl.api.RemoteStorage
import ftl.api.downloadAsJunitXML
import ftl.api.generateJUnitTestResultFromApi
import ftl.api.parseJUnitLegacyTestResultFromFile
import ftl.api.parseJUnitTestResultFromFile
import ftl.api.uploadToRemoteStorage
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IgnoredTestCases
import ftl.args.IosArgs
import ftl.args.ShardChunks
import ftl.client.google.GcStorage
import ftl.config.FtlConstants
import ftl.domain.junit.merge
import ftl.domain.junit.mergeTestTimes
import ftl.json.MatrixMap
import ftl.json.isAllSuccessful
import ftl.reports.CostReport
import ftl.reports.FullJUnitReport
import ftl.reports.HtmlErrorReport
import ftl.reports.JUnitReport
import ftl.reports.MatrixResultsReport
import ftl.reports.api.getAndUploadPerformanceMetrics
import ftl.reports.api.utcDateFormat
import ftl.reports.toXmlString
import ftl.run.common.getMatrixFilePath
import ftl.shard.createTestMethodDurationMap
import ftl.util.Artifacts
import ftl.util.getSmartFlankGCSPathAsFileReference
import ftl.util.resolveLocalRunPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Date
import kotlin.math.roundToInt

object ReportManager {

    /** Returns true if there were no test failures */
    fun generate(
        matrices: MatrixMap,
        args: IArgs,
        testShardChunks: ShardChunks,
        ignoredTestCases: IgnoredTestCases = listOf()
    ) {
        val testSuite: JUnitTest.Result? = parseTestSuite(matrices, args)
        if (args.useLegacyJUnitResult) {
            val useFlakyTests = args.flakyTestAttempts > 0
            if (useFlakyTests) JUnitDedupe.modify(testSuite)
        }
        listOf(CostReport, MatrixResultsReport)
            .map { it.run(matrices, testSuite, printToStdout = true, args = args) }

        if (!matrices.isAllSuccessful()) {
            listOf(
                HtmlErrorReport
            ).map { it.run(matrices, testSuite, printToStdout = false, args = args) }
        }
        JUnitReport.run(
            matrices,
            testSuite?.apply {
                if (ignoredTestCases.isNotEmpty()) {
                    testsuites?.add(ignoredTestCases.toJunitTestsResults())
                }
            },
            printToStdout = false,
            args = args
        )

        val testsResult = generateJUnitTestResultFromApi((args to matrices).toApiIdentity())
        processJunitResults(args, matrices, testSuite, testShardChunks, testsResult)
        // TODO move it to next #1756
        // createAndUploadPerformanceMetricsForAndroid(args, testsResult, matrices)
        uploadMatricesId(args, matrices)
    }

    private fun Pair<IArgs, MatrixMap>.toApiIdentity(): JUnitTest.Result.ApiIdentity {
        val (args, matrices) = this
        return JUnitTest.Result.ApiIdentity(
            projectId = args.project,
            matrixIds = matrices.map.values.map { it.matrixId }
        )
    }

    @VisibleForTesting
    internal fun processXmlFromFile(
        matrices: MatrixMap,
        args: IArgs,
        parsingFunction: (File) -> JUnitTest.Result
    ): JUnitTest.Result? = findXmlFiles(matrices, args)
        .map { xmlFile -> parsingFunction(xmlFile).updateWebLink(getWebLink(matrices, xmlFile)) }
        .reduceOrNull { acc, result -> result.merge(acc) }

    private fun findXmlFiles(
        matrices: MatrixMap,
        args: IArgs
    ) = File(resolveLocalRunPath(matrices, args))
        .walk()
        .filter { it.name.matches(Artifacts.testResultRgx) }
        .fold(listOf<File>()) { xmlFiles, file -> xmlFiles + file }

    private fun getWebLink(matrices: MatrixMap, xmlFile: File): String =
        xmlFile.getMatrixPath(matrices.runPath)
            ?.findMatrixPath(matrices)
            ?: "".also { logLn("WARNING: Matrix path not found in JSON.") }

    private fun String.findMatrixPath(matrices: MatrixMap) = matrices.map.values
        .firstOrNull { savedMatrix -> savedMatrix.gcsPath.endsWithTextWithOptionalSlashAtTheEnd(this) }
        ?.webLink
        ?: "".also { logLn("WARNING: Matrix path not found in JSON. $this") }

    @VisibleForTesting
    internal fun String.endsWithTextWithOptionalSlashAtTheEnd(text: String) =
        "($text)/*$".toRegex().containsMatchIn(this)

    private fun JUnitTest.Result.updateWebLink(
        webLink: String
    ) = apply {
        testsuites?.forEach { testSuite ->
            testSuite.testcases?.forEach { testCase ->
                testCase.webLink = webLink
            }
        }
    }

    private fun parseTestSuite(matrices: MatrixMap, args: IArgs): JUnitTest.Result? = when {
        // ios supports only legacy parsing
        args is IosArgs -> processXmlFromFile(matrices, args, parseJUnitTestResultFromFile)
        args.useLegacyJUnitResult -> processXmlFromFile(matrices, args, parseJUnitLegacyTestResultFromFile)
        else -> generateJUnitTestResultFromApi((args to matrices).toApiIdentity())
    }

    @VisibleForTesting
    internal fun uploadReportResult(testResult: String, args: IArgs, fileName: String) {
        if (args.resultsBucket.isBlank() || args.resultsDir.isBlank() || args.disableResultsUpload) return
        uploadToRemoteStorage(
            RemoteStorage.Dir(args.resultsBucket, args.resultsDir),
            RemoteStorage.Data(fileName, testResult.toByteArray())
        )
    }

    private fun processJunitResults(
        args: IArgs,
        matrices: MatrixMap,
        testSuite: JUnitTest.Result?,
        testShardChunks: ShardChunks,
        testsResult: JUnitTest.Result
    ) {
        when {
            args.fullJUnitResult -> processFullJunitResult(args, matrices, testShardChunks, testsResult)
            args.useLegacyJUnitResult -> processJunitXml(testSuite, args, testShardChunks)
            else -> processJunitXml(testSuite, args, testShardChunks)
        }
    }

    private fun createAndUploadPerformanceMetricsForAndroid(
        args: IArgs,
        testExecutions: List<TestExecution>,
        matrices: MatrixMap
    ) {
        testExecutions
            .takeIf { args is AndroidArgs }
            ?.run { withGcsStoragePath(matrices, args.resultsDir).getAndUploadPerformanceMetrics(args) }
    }

    private fun List<TestExecution>.withGcsStoragePath(
        matrices: MatrixMap,
        defaultResultDir: String
    ) = map { testExecution ->
        testExecution to (matrices.map[testExecution.matrixId]?.gcsPathWithoutRootBucket ?: defaultResultDir)
    }

    private fun IgnoredTestCases.toJunitTestsResults() = getSkippedJUnitTestSuite(
        map {
            JUnitTest.Case(
                classname = it.split("#").first().replace("class ", ""),
                name = it.split("#").last(),
                time = "0.0",
                skipped = null
            )
        }
    )

    @VisibleForTesting
    internal fun getSkippedJUnitTestSuite(listOfJUnitTestCase: List<JUnitTest.Case>) = JUnitTest.Suite(
        name = "junit-ignored",
        tests = listOfJUnitTestCase.size.toString(),
        errors = "0",
        failures = "0",
        skipped = listOfJUnitTestCase.size.toString(),
        time = "0.0",
        timestamp = utcDateFormat.format(Date()),
        testcases = listOfJUnitTestCase.toMutableList()
    )

    private fun processFullJunitResult(
        args: IArgs,
        matrices: MatrixMap,
        testShardChunks: ShardChunks,
        testSuite: JUnitTest.Result
    ) {
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
        oldResult: JUnitTest.Result,
        newResult: JUnitTest.Result,
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
        oldResult: JUnitTest.Result,
        newResult: JUnitTest.Result,
        args: IArgs,
        testShardChunks: ShardChunks
    ) {
        val list = createShardEfficiencyList(oldResult, newResult, args, testShardChunks)

        logLn(
            "Actual shard times:\n" + list.joinToString("\n") {
                "  ${it.shard}: Expected: ${it.expectedTime.roundToInt()}s, Actual: ${it.finalTime.roundToInt()}s, Diff: ${it.timeDiff.roundToInt()}s"
            } + "\n"
        )
    }

    private fun processJunitXml(
        newTestResult: JUnitTest.Result?,
        args: IArgs,
        testShardChunks: ShardChunks
    ) {
        if (newTestResult == null || newTestResult.testsuites.isNullOrEmpty()) return

        val oldTestResult = downloadAsJunitXML(args.getSmartFlankGCSPathAsFileReference())

        if (args.useLegacyJUnitResult) {
            newTestResult.mergeTestTimes(oldTestResult)
        }

        printActual(oldTestResult, newTestResult, args, testShardChunks)

        GcStorage.uploadJunitXml(newTestResult.toXmlString(), args)
    }

    private fun uploadMatricesId(args: IArgs, matrixMap: MatrixMap) {
        if (args.disableResultsUpload) return
        val file = args.getMatrixFilePath(matrixMap).toString()
        if (file.startsWith(FtlConstants.GCS_PREFIX)) return

        uploadToRemoteStorage(
            RemoteStorage.Dir(args.resultsBucket, args.resultsDir),
            RemoteStorage.Data(file, Files.readAllBytes(Paths.get(file)))
        )
    }
}
