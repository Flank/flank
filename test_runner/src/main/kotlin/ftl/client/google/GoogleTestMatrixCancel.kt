package ftl.client.google

suspend fun cancelMatrices(matrixId: String, projectId: String) {
    GcTestMatrix.cancel(matrixId, projectId)
}
