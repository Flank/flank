package ftl.run.exception

import ftl.config.FtlConstants
import ftl.json.SavedMatrix
import ftl.run.cancelMatrices
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun withGlobalExceptionHandling(block: () -> Int) {
    try {
        exitProcess(block())
    } catch (t: Throwable) {
        when (t) {
            is FlankGeneralError -> {
                err("\n${t.message}")
                exitProcess(GENERAL_FAILURE)
            }

            is FlankTimeoutError -> {
                println("\nCanceling flank due to timeout")
                runBlocking {
                    t.map?.run {
                        cancelMatrices(t.map, t.projectId)
                    }
                }
                exitProcess(GENERAL_FAILURE)
            }

            is IncompatibleTestDimensionError -> {
                err("\n${t.message}")
                exitProcess(INCOMPATIBLE_TEST_DIMENSION)
            }

            is MatrixCanceledError -> {
                err("The matrix was cancelled by the user.")
                err("Details: ${t.messageOrUnavailable}")
                exitProcess(CANCELED_BY_USER)
            }

            is InfrastructureError -> {
                err("An infrastructure error occurred.")
                err("Details: ${t.messageOrUnavailable}")
                exitProcess(INFRASTRUCTURE_ERROR)
            }

            is FailedMatrixError -> {
                if (t.ignoreFailed) exitProcess(SUCCESS)
                else exitProcess(NOT_PASSED)
            }

            is FTLError -> {
                t.matrix.logError()
                exitProcess(UNEXPECTED_ERROR)
            }

            is YmlValidationError,
            is FlankConfigurationError -> {
                err(t.message)
                exitProcess(CONFIGURATION_FAIL)
            }

            // We need to cover the case where some component in the call stack starts a non-daemon
            // thread, and then throws an Error that kills the main thread. This is extra safe implementation
            else -> {
                FtlConstants.bugsnag?.notify(t)
                t.printStackTrace()
                exitProcess(UNEXPECTED_ERROR)
            }
        }
    }
}

private val FlankException.messageOrUnavailable: String
    get() = if (message.isNullOrBlank()) "[Unavailable]" else message as String

private fun err(string: String?) = System.err.println(string)

private fun SavedMatrix.logError() {
    println("Matrix is $state")
}
