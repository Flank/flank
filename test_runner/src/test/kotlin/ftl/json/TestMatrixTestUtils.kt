package ftl.json

import com.google.api.services.testing.model.TestMatrix
import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix.Data
import ftl.domain.testmatrix.updateWithMatrix

fun createAndUpdateMatrix(testMatrix: TestMatrix) = Data().updateWithMatrix(testMatrix.toApiModel())
