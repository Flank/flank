package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.api.TestMatrixAndroid
import ftl.client.google.run.android.executeAndroidTests
import kotlinx.coroutines.runBlocking
import com.google.testing.model.TestMatrix as GoogleTestMatrix

object GoogleTestMatrixAndroid :
    TestMatrixAndroid.Execute,
    (List<TestMatrixAndroid.TestSetup>) -> List<TestMatrix.Data> by { configTypePairs ->
        runBlocking {
            executeAndroidTests(configTypePairs).map(GoogleTestMatrix::toApiModel)
        }
    }
