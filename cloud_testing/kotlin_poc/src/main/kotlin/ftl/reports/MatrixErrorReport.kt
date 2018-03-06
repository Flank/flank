package ftl.reports

import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import java.io.File

/**

Used to create matrix error report

Example:

Test Name	Link	Failure
SpeedGraderPageTest#displaySubmissionPickerDialog	31x
https://console.firebase.google.com/project/bootstraponline-awesome-sauce/testlab/histories/bh.2c123bc6844a34b8/matrices/8756758526511083166	android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: with string from resource id: <2131756174>submission_versions] value: Submission versions
https://console.firebase.google.com/project/bootstraponline-awesome-sauce/testlab/histories/bh.2c123bc6844a34b8/matrices/8810474127535026941
 **/
object MatrixErrorReport : IReport {

    private fun list(vararg args:String): String {
        return args.joinToString("\t")
    }

    override fun run(matrices: MatrixMap, testSuite: TestSuite) {
        val matrixErrorReport = File(reportPath(matrices) + ".csv")
        matrixErrorReport.printWriter().use { writer ->
            writer.println(list("Test Name", "Link", "Failure"))

            testSuite.testCases.forEach { test ->
                val testName = test.key.split(".").last()
                val testFailures = test.value.failures
                if (testFailures.isEmpty()) return@forEach

                writer.println(list(testName, testFailures.size.toString() + "x"))
                testFailures.forEach {
                    writer.println(list("", it.webLink, it.stackTrace.split("\n").firstOrNull() ?: ""))
                }
            }
        }
    }
}
