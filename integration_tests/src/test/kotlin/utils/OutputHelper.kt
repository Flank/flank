package utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import utils.testResults.TestSuites
import java.nio.file.Paths

fun String.findTestDirectoryFromOutput() = "results-dir:\\s.*\\s".toRegex().find(this)?.value.orEmpty().trim().replace("results-dir: ", "")

fun String.toJUnitXmlFile() = Paths.get("./", "results", this, "JUnitReport.xml").toFile()

fun TestSuites.assertTestResultContainsWebLinks() =
    testSuites.flatMap { it.testCases }.filter { it.skipped == null }.forEach {
        assertFalse(it.webLink.isNullOrBlank())
    }

fun TestSuites.assertCountOfSkippedTests(expectedCount: Int) = assertEquals(expectedCount, testSuites.sumBy { it.skipped })
