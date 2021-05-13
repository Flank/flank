package ftl.util

import ftl.api.TestMatrix

object MatrixState {
    // https://github.com/bootstraponline/gcloud_cli/blob/0752e88b155a417a18d244c242b4ab3fb9aa1c1f/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/testing_v1.json#L171
    // see testing_v1.json TestMatrix state enum

    const val VALIDATING = "VALIDATING"
    const val PENDING = "PENDING"
    const val RUNNING = "RUNNING"
    const val FINISHED = "FINISHED"
    const val TEST_STATE_UNSPECIFIED = "TEST_STATE_UNSPECIFIED"
    const val ERROR = "ERROR"
    const val UNSUPPORTED_ENVIRONMENT = "UNSUPPORTED_ENVIRONMENT"
    const val INCOMPATIBLE_ENVIRONMENT = "INCOMPATIBLE_ENVIRONMENT"
    const val INCOMPATIBLE_ARCHITECTURE = "INCOMPATIBLE_ARCHITECTURE"
    const val CANCELLED = "CANCELLED"
    const val INVALID = "INVALID"

    private val validStates = listOf(
        VALIDATING,
        PENDING,
        RUNNING,
        FINISHED
    )

    fun inProgress(state: String): Boolean {
        return when (state) {
            VALIDATING -> true
            PENDING -> true
            RUNNING -> true
            else -> false
        }
    }

    fun completed(state: String): Boolean {
        return !inProgress(state)
    }

    private fun isValid(state: String): Boolean {
        return validStates.contains(state)
    }

    fun isInvalid(state: String): Boolean {
        return !isValid(state)
    }
}

val TestMatrix.Data.isInvalid
    get() = MatrixState.isInvalid(this.state)
