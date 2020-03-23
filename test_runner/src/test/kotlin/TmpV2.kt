import com.google.api.client.json.GenericJson
import ftl.args.AndroidArgs
import ftl.reports.api.createJUnitTestResult
import ftl.reports.api.createTestExecutionDataListAsync
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.getTestExecutions
import ftl.reports.api.prepareForJUnitResult
import ftl.reports.api.refreshTestMatrices
import ftl.reports.xml.xmlToString
import java.io.File
import kotlin.system.exitProcess

object TmpV2 {
    private const val PREFIX = "REPORT_API_TEST"
    private const val MATRIX_ID_1 = "matrix-1dqqjntef5fje"
    private const val MATRIX_ID_2 = "matrix-u8bqaci6hhvsa"
    private const val JUNIT_REPORT_FILE = "$PREFIX-JUnitReport.xml"
    private const val API_JSON_FILE = "$PREFIX-api-result.json"

    @JvmStatic
    fun main(args: Array<String>) {

        print("fetching matrix")
        val matrices = refreshTestMatrices(
            matrixIds = listOf(MATRIX_ID_1, MATRIX_ID_2),
            projectId = AndroidArgs.default().project
        )
        println(" - OK")

        print("generating api results")
        val apiResult = matrices.getTestExecutions()
            .createTestExecutionDataListAsync()
            .prepareForJUnitResult()
            .createJson()
        println(" - OK")

        print("writing api results to file")
        File(API_JSON_FILE).writeText(apiResult.toPrettyString())
        println(" - OK")

        print("generating junit report")
        val jUnitTestResult = matrices
            .getTestExecutions()
            .createJUnitTestResult()
        println(" - OK")

        print("writing api results to file")
        File(JUNIT_REPORT_FILE).writeText(jUnitTestResult.xmlToString())
        println(" - OK")

        exitProcess(0)
    }

    private fun List<TestExecutionData>.createJson() = map { data ->
        GenericJson().apply {
            factory = data.testExecution.factory
            putAll(
                mapOf(
                    "testExecution" to data.testExecution,
                    "testCases" to data.testCases,
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
