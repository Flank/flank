package utils

import com.google.common.truth.IterableSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import utils.testResults.TestSuites
import java.io.File
import java.nio.file.Paths

fun String.findTestDirectoryFromOutput() =
    "results-dir:\\s.*\\s".toRegex().find(this)?.value.orEmpty().trim().replace("results-dir: ", "")

fun String.toJUnitXmlFile(): File = Paths.get("./", "results", this, "JUnitReport.xml").toFile()

fun String.toOutputReportFile(): File =
    if (isEmpty()) Paths.get("outputReport.json").toFile()
    else Paths.get("./", "results", this, "outputReport.json").toFile()

fun TestSuites.assertTestResultContainsWebLinks() =
    testSuites.flatMap { it.testCases }.filter { it.skipped == null }.forEach {
        assertFalse(it.webLink.isNullOrBlank())
    }

fun TestSuites.assertCountOfSkippedTests(expectedCount: Int) =
    assertEquals(expectedCount, testSuites.sumOf { it.skipped })

fun TestSuites.assertCountOfFailedTests(expectedCount: Int) =
    assertEquals(expectedCount, testSuites.count { it.failures > 0 })

fun TestSuites.assertTestPass(tests: List<String>) = assertEquals(
    tests.count(),
    testSuites.flatMap { it.testCases }.filter { it.name in tests && it.failure == null }.distinctBy { it.name }.count()
)

fun TestSuites.assertTestFail(tests: List<String>) = assertEquals(
    tests.count(),
    testSuites.flatMap { it.testCases }.filter { it.name in tests && it.failure != null }.distinctBy { it.name }.count()
)

fun IterableSubject.containsAll(vararg args: Any) = args.forEach { arg ->
    contains(arg)
}
