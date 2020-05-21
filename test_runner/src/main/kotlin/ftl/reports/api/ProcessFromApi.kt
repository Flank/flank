package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.reports.xml.model.JUnitTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun processXmlFromApi(
    matrices: MatrixMap,
    args: IArgs
): JUnitTestResult = refreshTestMatrices(
    matrixIds = matrices.map.values.map { it.matrixId },
    projectId = args.project
)
    .getTestExecutions()
    .createJUnitTestResult()

private fun refreshTestMatrices(
    matrixIds: List<String>,
    projectId: String
): List<TestMatrix> = runBlocking {
    matrixIds.map { matrixId ->
        async(Dispatchers.IO) {
            GcTestMatrix.refresh(
                matrixId,
                projectId
            )
        }
    }.awaitAll()
}

private fun List<TestMatrix>.getTestExecutions(): List<TestExecution> = this
    .mapNotNull(TestMatrix::getTestExecutions)
    .flatten()
