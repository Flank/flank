package ftl.client.google

import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import ftl.api.TestMatrix
import ftl.api.TestMatrix.Identity
import ftl.api.refreshTestMatrix
import ftl.args.IArgs
import ftl.json.MatrixMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun fetchMatrices(matricesIds: List<String>, projectId: String
): List<TestMatrix.TestExecution> = refreshTestMatrices(
    matrixIds = matricesIds,
    projectId = projectId
).getTestExecutions()

private fun refreshTestMatrices(
    matrixIds: List<String>,
    projectId: String
): List<TestMatrix.Data> = runBlocking {
    matrixIds.map { matrixId ->
        async(Dispatchers.IO) {
            refreshTestMatrix(Identity(matrixId, projectId))
        }
    }.awaitAll()
}

private fun List<TestMatrix.Data>.getTestExecutions(): List<TestMatrix.TestExecution> =
    mapNotNull(TestMatrix.Data::testExecutions)
        .flatten()
