package ftl.adapter.google

import ftl.api.TestMatrix
import ftl.gc.GcTestMatrix

suspend fun cancelMatrices(identity: TestMatrix.Identity) = GcTestMatrix.cancel(identity.matrixId, identity.projectId)
