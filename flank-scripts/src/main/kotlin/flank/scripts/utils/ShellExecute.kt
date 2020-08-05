package flank.scripts.utils

fun List<String>.runCommand(retryCount: Int = 0) =
        ProcessBuilder(this)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .startWithRetry(retryCount)

fun String.runCommand(retryCount: Int = 0) = split(" ").toList().runCommand(retryCount)

internal fun ProcessBuilder.startWithRetry(retryCount: Int): Int {
    var retryTries = 0
    var processResponse: Int
    do {
        processResponse = try {
            start().waitFor()
        } catch (e: Exception) {
            println("Error when making shell command ${e.message}, cannot try again")
            EXCEPTION_WHEN_CALLING_COMMAND_CODE
        }
        retryTries++
    } while (shouldRetry(processResponse, retryCount, retryTries))

    return processResponse
}

private fun shouldRetry(
        processResponse: Int,
        retryCount: Int,
        retryTries: Int
) = processResponse != 0 && processResponse != EXCEPTION_WHEN_CALLING_COMMAND_CODE && retryTries < retryCount

const val SUCCESS = 0
const val ERROR_WHEN_RUNNING = 1
private const val EXCEPTION_WHEN_CALLING_COMMAND_CODE = -1
