package ftl.adapter

import ftl.api.JUnitTest
import ftl.client.google.createAndUploadPerformanceMetricsForAndroid
import ftl.client.google.fetchMatrices
import ftl.client.junit.createJUnitTestResult

object GoogleJUnitTestFetch :
    JUnitTest.Result.GenerateFromApi,
    (JUnitTest.Result.ApiIdentity) -> JUnitTest.Result by { (args, matrixMap) ->
        fetchMatrices(matrixMap.map.values.map { it.matrixId }, args.project)
            .createAndUploadPerformanceMetricsForAndroid(args, matrixMap)
            .createJUnitTestResult()
    }
