package ftl.client.google

import com.google.testing.model.CancelTestMatrixResponse
import com.google.testing.model.TestMatrix
import ftl.gc.GcTesting
import ftl.http.executeWithRetry
import ftl.run.exception.FlankGeneralError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Duration.ofHours

object GcTestMatrix {

    // Getting the test matrix may throw an internal server error.
    //  {
    //      "code" : 500,
    //      "errors" : [ {
    //      "domain" : "global",
    //      "message" : "Internal error encountered.",
    //      "reason" : "backendError"
    //  } ],
    //      "message" : "Internal error encountered.",
    //      "status" : "INTERNAL"
    //  }
    //
    // Randomly throws errors... yay FTL
    //    com.google.api.client.googleapis.json.GoogleJsonResponseException: 503 Service Unavailable
    //    {
    //        "code" : 503,
    //        "errors" : [ {
    //        "domain" : "global",
    //        "message" : "The service is currently unavailable.",
    //        "reason" : "backendError"
    //    } ],
    //        "message" : "The service is currently unavailable.",
    //        "status" : "UNAVAILABLE"
    //    }
    suspend fun refresh(testMatrixId: String, projectId: String): TestMatrix {
        val getMatrix = withContext(Dispatchers.IO) {
            GcTesting.get.projects().testMatrices().get(projectId, testMatrixId)
        }
        var failed = 0
        val maxWait = ofHours(1).seconds

        while (failed < maxWait) {
            try {
                return getMatrix.executeWithRetry()
            } catch (e: Exception) {
                delay(1_000)
                failed += 1
            }
        }

        throw FlankGeneralError("Failed to refresh matrix")
    }

    suspend fun cancel(testMatrixId: String, projectId: String): CancelTestMatrixResponse {
        val cancelMatrix = withContext(Dispatchers.IO) {
            GcTesting.get.projects().testMatrices().cancel(projectId, testMatrixId)
        }
        var failed = 0
        val maxTries = 3

        while (failed < maxTries) {
            try {
                return cancelMatrix.executeWithRetry()
            } catch (e: Exception) {
                System.err.println("Error cancelling $testMatrixId; Attempt ${failed + 1} of $maxTries; Exception: ${e.localizedMessage}")
                delay(2_000)
                failed += 1
            }
        }

        throw FlankGeneralError("Failed to cancel matrix")
    }
}
