package ftl.gc

import com.google.api.services.testing.model.CancelTestMatrixResponse
import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.http.executeWithRetry
import ftl.util.sleep
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
    fun refresh(testMatrixId: String, args: IArgs): TestMatrix {
        val getMatrix = GcTesting.get.projects().testMatrices().get(args.project, testMatrixId)
        var failed = 0
        val maxWait = ofHours(1).seconds

        while (failed < maxWait) {
            try {
                return getMatrix.executeWithRetry()
            } catch (e: Exception) {
                sleep(1)
                failed += 1
            }
        }

        throw RuntimeException("Failed to refresh matrix")
    }

    fun cancel(testMatrixId: String, args: IArgs): CancelTestMatrixResponse {
        val cancelMatrix = GcTesting.get.projects().testMatrices().cancel(args.project, testMatrixId)
        var failed = 0
        val maxTries = 3

        while (failed < maxTries) {
            try {
                return cancelMatrix.executeWithRetry()
            } catch (e: Exception) {
                System.err.println("Error cancelling $testMatrixId; Attempt ${failed + 1} of $maxTries; Exception: ${e.localizedMessage}")
                sleep(2)
                failed += 1
            }
        }

        throw RuntimeException("Failed to cancel matrix")
    }
}
