package ftl.client.google

import com.google.testing.model.TestMatrix

suspend fun refreshMatrix(
    matrixId: String,
    projectId: String
): TestMatrix = GcTestMatrix.refresh(matrixId, projectId)
