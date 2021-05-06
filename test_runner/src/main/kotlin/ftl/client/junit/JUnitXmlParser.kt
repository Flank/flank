package ftl.client.junit

import com.google.common.annotations.VisibleForTesting
import ftl.run.exception.FlankGeneralError
import java.io.File

fun File.parseJUnit(): JUnitTestResult = parse(::parseAllSuitesXml)

fun File.parseLegacyJUnit(): JUnitTestResult = parse(::parseOneSuiteXml)

private fun File.parse(
    process: (file: File) -> JUnitTestResult
): JUnitTestResult = runCatching(process)
    .getOrElse { e -> throw FlankGeneralError("Cannot process xml file: $absolutePath", e) }
    .updateTestSuites(deviceName = getDeviceString(parentFile.name))

private val deviceStringRgx = Regex("([^-]+-[^-]+-[^-]+-[^-]+).*")

// NexusLowRes-28-en-portrait-rerun_1 =>  NexusLowRes-28-en-portrait
@VisibleForTesting
internal fun getDeviceString(deviceString: String): String {
    val matchResult = deviceStringRgx.find(deviceString)
    return matchResult?.groupValues?.last().orEmpty()
}

private fun JUnitTestResult.updateTestSuites(deviceName: String) = apply {
    testsuites?.forEach { testSuite ->
        testSuite.name = "$deviceName#${testSuite.name}"
    }
}

/*todo move it */
/*
*         testSuite.testcases?.forEach { testCase ->
            testCase.webLink = ReportManager.getWebLink(matrices, xmlFile)
        }
* */
