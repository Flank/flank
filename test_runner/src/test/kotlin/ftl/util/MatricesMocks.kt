package ftl.util

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import ftl.http.executeWithRetry
import io.mockk.every
import io.mockk.mockkStatic

fun mockTestMatrices(
    vararg mocks: TestMatrix
) {
    mockkStatic("ftl.http.ExecuteWithRetryKt")
    every {
        any<Testing.Projects.TestMatrices.Get>().executeWithRetry()
    } returnsMany listOf(*mocks)
}

fun getMockedTestMatrix() = TestMatrix().apply {
    state = "INVALID"
    testMatrixId = "matrix-12345"
    testExecutions = listOf(
        TestExecution().apply {
            resultStorage = ResultStorage().apply {
                googleCloudStorage = GoogleCloudStorage().apply {
                    gcsPath = "any/Path"
                }
            }
        }
    )
}
