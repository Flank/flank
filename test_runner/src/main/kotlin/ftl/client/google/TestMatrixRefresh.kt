package ftl.client.google

import ftl.api.TestMatrix
import ftl.gc.GcTestMatrix

suspend fun refreshMatrix(identity: TestMatrix.Identity) = GcTestMatrix.refresh(identity.matrixId, identity.projectId)
