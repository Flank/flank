package ftl.client.google

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun fetchMatrices(
    matricesIds: List<String>,
    projectId: String
): List<TestExecution> = refreshTestMatrices(
    matrixIds = matricesIds,
    projectId = projectId
).getTestExecutions()

private fun refreshTestMatrices(
    matrixIds: List<String>,
    projectId: String
): List<TestMatrix> = runBlocking {
    matrixIds.map { matrixId ->
        async(Dispatchers.IO) {
            GcTestMatrix.refresh(matrixId, projectId)
        }
    }.awaitAll()
}

private fun List<TestMatrix>.getTestExecutions(): List<TestExecution> = this
    .mapNotNull(TestMatrix::getTestExecutions)
    .flatten()
