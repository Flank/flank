package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.client.google.refreshMatrix
import kotlinx.coroutines.coroutineScope

object TestMatrixRefresh : TestMatrix.Refresh {
    override suspend fun invoke(identity: TestMatrix.Identity): TestMatrix.Data = coroutineScope {
        refreshMatrix(identity.matrixId, identity.projectId).toApiModel(identity)
    }
}
