package ftl.adapter

import ftl.api.TestMatrix
import ftl.client.google.cancelMatrices
import kotlinx.coroutines.runBlocking

object TestMatrixCancel :
    TestMatrix.Cancel,
    (TestMatrix.Identity) -> Unit by { identity ->
        runBlocking {
            cancelMatrices(identity.matrixId, identity.projectId)
        }
    }
