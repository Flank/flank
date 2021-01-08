package ftl.run.exception

import flank.common.logLn
import ftl.json.SavedMatrix
import ftl.run.cancelMatrices
import ftl.util.captureError
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun withGlobalExceptionHandling(block: () -> Int) {
    try {
        exitProcess(block())
    } catch (t: Throwable) {
        when (t) {
            is FlankGeneralError -> {
                printError("\n${t.message}")
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
                printError("\n${t.message}")
                exitProcess(INCOMPATIBLE_TEST_DIMENSION)
            }

            is MatrixCanceledError -> {
                printError("The matrix was cancelled by the user.")
                printError("Details: ${t.messageOrUnavailable}")
                exitProcess(CANCELED_BY_USER)
            }

            is InfrastructureError -> {
                captureError(t)
                printError("An infrastructure error occurred.")
                printError("Details: ${t.messageOrUnavailable}")
                exitProcess(INFRASTRUCTURE_ERROR)
            }

            is FailedMatrixError -> {
                if (t.ignoreFailed) exitProcess(SUCCESS)
                else exitProcess(NOT_PASSED)
            }

            is FTLError -> {
                captureError(t)
                t.matrix.logError()
                exitProcess(UNEXPECTED_ERROR)
            }

            is MatrixValidationError,
            is YmlValidationError,
            is FlankConfigurationError -> {
                captureError(t)
                printError(t.message)
                exitProcess(CONFIGURATION_FAIL)
            }

            // We need to cover the case where some component in the call stack starts a non-daemon
            // thread, and then throws an Error that kills the main thread. This is extra safe implementation
            else -> {
                captureError(t)
                t.printStackTrace()
                exitProcess(UNEXPECTED_ERROR)
            }
        }
    }
}

private val FlankException.messageOrUnavailable: String
    get() = if (message.isNullOrBlank()) "[Unavailable]" else message as String

private fun printError(string: String?) = System.err.println(string)

private fun SavedMatrix.logError() {
    logLn("Matrix is $state")
}
