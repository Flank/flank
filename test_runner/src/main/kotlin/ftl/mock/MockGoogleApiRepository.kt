package ftl.mock

import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.toolresults.model.ListTestCasesResponse
import com.google.api.services.toolresults.model.Step
import ftl.config.FtlConstants
import java.io.File

object MockGoogleApiRepository {

    fun getMatrix(
        projectId: String,
        matrixId: String
    ): TestMatrix? = read(
        projectId,
        matrixId
    )

    fun getStep(
        projectId: String,
        executionId: String,
        historyId: String,
        stepId: String
    ): Step? = read(
        projectId,
        executionId,
        historyId,
        stepId
    )

    fun getListTestCasesResponse(
        projectId: String,
        executionId: String,
        historyId: String,
        stepId: String
    ): ListTestCasesResponse? = read(
        projectId,
        executionId,
        historyId,
        stepId
    )

    private const val MULTIPLE_FLAKY_PATH = "src/test/kotlin/ftl/fixtures/multiple_flaky"

    private inline fun <reified T> read(vararg name: String): T? = try {
        FtlConstants.JSON_FACTORY.fromReader(
            getFileReader<T>(name),
            T::class.java
        )
    } catch (e: Throwable) {
        null
    }

    private inline fun <reified T> getFileReader(name: Array<out String>) = File(MULTIPLE_FLAKY_PATH)
        .resolve(parseFileName<T>(name))
        .reader()

    private inline fun <reified T> parseFileName(name: Array<out String>) =
        (arrayOf(T::class.java.simpleName) + name).joinToString("_") + ".json"
}
