package ftl.client.google

import com.google.api.services.testing.model.TestMatrix

suspend fun refreshMatrix(
    matrixId: String,
    projectId: String
): TestMatrix = GcTestMatrix.refresh(matrixId, projectId)
