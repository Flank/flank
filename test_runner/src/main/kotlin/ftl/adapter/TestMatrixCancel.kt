package ftl.adapter

import ftl.adapter.google.cancelMatrices
import ftl.api.TestMatrix
import kotlinx.coroutines.runBlocking

object TestMatrixCancel :
    TestMatrix.Cancel,
    (TestMatrix.Identity) -> Unit by {
        runBlocking {
            cancelMatrices(it)
        }
    }
