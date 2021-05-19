package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.api.TestMatrixIos
import ftl.client.google.run.ios.executeIosTests
import kotlinx.coroutines.runBlocking
import com.google.testing.model.TestMatrix as GoogleTestMatrix

object GoogleTestMatrixIos :
    TestMatrixIos.Execute,
    (TestMatrixIos.Config, List<TestMatrixIos.Type>) -> List<TestMatrix.Data> by { config, types ->
        runBlocking {
            executeIosTests(config, types).map(GoogleTestMatrix::toApiModel)
        }
    }
