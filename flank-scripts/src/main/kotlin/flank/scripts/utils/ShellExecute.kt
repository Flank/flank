package flank.scripts.utils

fun String.runCommand(retryCount: Int = 0) =
        ProcessBuilder(*split(" ").toTypedArray())
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .startWithRetry(retryCount)

private fun ProcessBuilder.startWithRetry(retryCount: Int): Int {
    var retryTries = 0
    var processResponse: Int
    do {
        processResponse = try {
            start().exitValue()
        } catch (e: Exception) {
            println("Error when making shell command, cannot try again")
            EXCEPTION_WHEN_CALLING_COMMAND_CODE
        }
        retryTries++
    } while (processResponse == 0 || processResponse == EXCEPTION_WHEN_CALLING_COMMAND_CODE || retryTries < retryCount)

    return processResponse
}

private const val EXCEPTION_WHEN_CALLING_COMMAND_CODE = -1
