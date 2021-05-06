package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.JUnitTest
import ftl.client.google.fetchMatrices
import ftl.client.junit.createJUnitTestResult

object GoogleJUnitTestFetch :
    JUnitTest.Result.GenerateFromApi,
    (JUnitTest.Result.ApiIdentity) -> JUnitTest.Result by { (projectId, matrixIds) ->
        fetchMatrices(matrixIds, projectId)
            .createJUnitTestResult()
            .toApiModel()
    }
