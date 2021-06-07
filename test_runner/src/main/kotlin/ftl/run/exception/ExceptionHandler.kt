package ftl.run.exception

import flank.common.logLn
import ftl.api.TestMatrix
import ftl.reports.output.add
import ftl.reports.output.generate
import ftl.reports.output.outputReport
import ftl.reports.printTotalDuration
import ftl.run.cancelMatrices
import ftl.util.closeCrashReporter
import ftl.util.report
import io.sentry.SentryLevel
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun withGlobalExceptionHandling(block: () -> Int) {
    withGlobalExceptionHandling(block) {
        closeCrashReporter()
        outputReport.generate()
        printTotalDuration()
        exitProcess(it)
    }
}

// Overloading this function makes tests easier to implement and exit with the correct exit code
internal fun withGlobalExceptionHandling(block: () -> Int, exitProcessFunction: (Int) -> Unit) {
    try {
        exitProcessFunction(block())
    } catch (t: Throwable) {
        when (t) {
            is FlankGeneralError -> {
                printError("\n${t.message}")
                exitProcessFunction(GENERAL_FAILURE)
            }

            is FlankTimeoutError -> {
                println("\nCanceling flank due to timeout")
                runBlocking {
                    t.map?.run {
                        cancelMatrices(t.map, t.projectId)
                    }
                }
                exitProcessFunction(GENERAL_FAILURE)
            }

            is IncompatibleTestDimensionError -> {
                printError("\n${t.message}")
                exitProcessFunction(INCOMPATIBLE_TEST_DIMENSION)
            }

            is MatrixCanceledError -> {
                printError("The matrix was cancelled by the user.")
                printError("Details: ${t.messageOrUnavailable}")
                exitProcessFunction(CANCELED_BY_USER)
            }

            is InfrastructureError -> {
                t.report(SentryLevel.DEBUG)
                printError("An infrastructure error occurred.")
                printError("Details: ${t.messageOrUnavailable}")
                exitProcessFunction(INFRASTRUCTURE_ERROR)
            }

            is FailedMatrixError -> {
                if (t.ignoreFailed) exitProcessFunction(SUCCESS)
                else exitProcessFunction(NOT_PASSED)
            }

            is FTLError -> {
                t.report()
                t.matrix.logError()
                exitProcessFunction(UNEXPECTED_ERROR)
            }

            is MatrixValidationError,
            is YmlValidationError,
            is FlankConfigurationError -> {
                t.message?.let(::printError)
                exitProcessFunction(CONFIGURATION_FAIL)
            }

            // We need to cover the case where some component in the call stack starts a non-daemon
            // thread, and then throws an Error that kills the main thread. This is extra safe implementation
            else -> {
                t.printStackTrace()
                t.report()
                exitProcessFunction(UNEXPECTED_ERROR)
            }
        }
    }
}

private val FlankException.messageOrUnavailable: String
    get() = if (message.isNullOrBlank()) "[Unavailable]" else message as String

private fun printError(string: String?) {
    System.err.println(string)
    string?.let { outputReport.add("error", it) }
}

private fun TestMatrix.Data.logError() {
    logLn("Matrix is $state")
}
