import com.google.api.client.json.GenericJson
import ftl.args.AndroidArgs
import ftl.gc.GcTestMatrix
import ftl.reports.api.createJUnitTestResult
import ftl.reports.api.createTestExecutionDataListAsync
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.filterForJUnitResult
import ftl.reports.xml.xmlToString
import java.io.File
import kotlin.system.exitProcess

object TmpV2 {
    private const val PREFIX = "REPORT_API_TEST"
    private const val MATRIX_ID = "matrix-2ut05apux3ugd"
    private const val JUNIT_REPORT_FILE = "$PREFIX-JUnitReport.xml"
    private const val API_JSON_FILE = "$PREFIX-api-result.json"

    @JvmStatic
    fun main(args: Array<String>) {

        print("fetching matrix")
        val matrix = GcTestMatrix.refresh(MATRIX_ID, AndroidArgs.default())
        println(" - OK")

        print("generating api results")
        val apiResult = matrix.testExecutions
            .createTestExecutionDataListAsync()
            .filterForJUnitResult()
            .createJson()
        println(" - OK")

        print("writing api results to file")
        File(API_JSON_FILE).writeText(apiResult.toPrettyString())
        println(" - OK")

        print("generating junit report")
        val jUnitTestResult = matrix.createJUnitTestResult()
        println(" - OK")

        print("writing api results to file")
        File(JUNIT_REPORT_FILE).writeText(jUnitTestResult.xmlToString())
        println(" - OK")

        exitProcess(0)
    }

    private fun List<TestExecutionData>.createJson() = map { data ->
        GenericJson().apply {
            factory = data.response.factory
            putAll(
                mapOf(
                    "testExecution" to data.testExecution,
                    "listTestCasesResponse" to data.response,
                    "step" to data.step
                )
            )
        }
    }.let { objects ->
        GenericJson().apply {
            factory = objects.first().factory
            put("objects", objects)
        }
    }
}