package ftl.client.google

import ftl.gc.GcTestMatrix

suspend fun cancelMatrices(matrixId: String, projectId: String) {
    GcTestMatrix.cancel(matrixId, projectId)
}
