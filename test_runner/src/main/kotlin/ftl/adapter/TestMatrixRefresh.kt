package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.client.google.refreshMatrix
import kotlinx.coroutines.runBlocking

object TestMatrixRefresh :
    TestMatrix.Refresh,
    (TestMatrix.Identity) -> TestMatrix.Data by { identity ->
        runBlocking {
            refreshMatrix(identity.matrixId, identity.projectId).toApiModel(identity)
        }
    }
