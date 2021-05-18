package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.api.TestMatrixAndroid
import ftl.client.google.run.android.executeAndroidTests
import kotlinx.coroutines.runBlocking
import com.google.testing.model.TestMatrix as GoogleTestMatrix

object GoogleTestMatrixAndroid :
    TestMatrixAndroid.Execute,
    (TestMatrixAndroid.Config, List<TestMatrixAndroid.Type>) -> List<TestMatrix.Data> by { config, types ->
        runBlocking {
            executeAndroidTests(config, types).map(GoogleTestMatrix::toApiModel)
        }
    }
