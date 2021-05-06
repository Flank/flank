package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.client.google.fetchMatrixOutcome

object TestMatrixFetch :
    TestMatrix.Summary.Fetch,
    (TestMatrix.Data) -> TestMatrix.Summary by {
        fetchMatrixOutcome(it).toApiModel()
    }
