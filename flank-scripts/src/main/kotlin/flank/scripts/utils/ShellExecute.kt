package flank.scripts.utils

import java.io.File

fun List<String>.runCommand(retryCount: Int = 0, fileForOutput: File? = null) =
    ProcessBuilder(this).apply {
        if (fileForOutput != null) {
            redirectOutput(fileForOutput)
            redirectError(fileForOutput)
        } else {
            redirectOutput(ProcessBuilder.Redirect.INHERIT)
            redirectError(ProcessBuilder.Redirect.INHERIT)
        }
    }
        .startWithRetry(retryCount)

fun String.runCommand(retryCount: Int = 0, fileForOutput: File? = null) =
    split(" ").toList().runCommand(retryCount, fileForOutput)

fun String.runForOutput(retryCount: Int = 0): String = File
    .createTempFile(hashCode().toString(), "")
    .let { file ->
        runCommand(retryCount, file)
        file.readText()
    }

fun String.checkCommandExists() = (if (isWindows) "where " else "command -v ").plus(this).runCommand() == 0

fun String.checkAndInstallIfNeed(installCommand: String) = checkCommandExists().takeUnless { it }?.let {
    installCommand.runCommand()
}

fun String.commandInstalledOr(orAction: () -> Unit) = checkCommandExists().takeUnless { it }?.let {
    orAction()
}

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
