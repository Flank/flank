package ftl.adapter

import ftl.client.google.cancelMatrices
import ftl.api.TestMatrix
import kotlinx.coroutines.runBlocking

object TestMatrixCancel :
    TestMatrix.Cancel,
    (TestMatrix.Identity) -> Unit by { identity ->
        runBlocking {
            cancelMatrices(identity.matrixId, identity.projectId)
        }
    }
